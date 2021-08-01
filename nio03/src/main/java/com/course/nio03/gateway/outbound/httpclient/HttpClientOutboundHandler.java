package com.course.nio03.gateway.outbound.httpclient;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import com.course.nio03.gateway.domain.ServerConfig;
import com.course.nio03.gateway.filter.HttpRequestFilter;
import com.course.nio03.gateway.outbound.BaseOutboundHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

@Slf4j
public class HttpClientOutboundHandler extends BaseOutboundHandler {
  private CloseableHttpAsyncClient httpclient;

  public HttpClientOutboundHandler(List<ServerConfig> backends) {
    super(backends);

    IOReactorConfig ioConfig =
        IOReactorConfig.custom()
            .setConnectTimeout(1000)
            .setSoTimeout(1000)
            .setIoThreadCount(Runtime.getRuntime().availableProcessors())
            .setRcvBufSize(32 * 1024)
            .build();

    httpclient =
        HttpAsyncClients.custom()
            .setMaxConnTotal(40)
            .setMaxConnPerRoute(8)
            .setDefaultIOReactorConfig(ioConfig)
            .setKeepAliveStrategy((response, context) -> 6000)
            .build();
    httpclient.start();
  }

  @Override
  public void handle(
      final FullHttpRequest fullRequest,
      final ChannelHandlerContext ctx,
      HttpRequestFilter filter) {
    ServerConfig serverConfig = router.route(this.backendUrls);
    log.info("route to " + serverConfig.getHost());
    final String url = serverConfig.getHost() + fullRequest.uri();
    filter.filter(fullRequest, ctx);
    proxyService.submit(() -> fetchGet(fullRequest, ctx, url));
  }

  private void fetchGet(
      final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
    final HttpGet httpGet = new HttpGet(url);
    // httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
    httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
    httpGet.setHeader("mao", inbound.headers().get("mao"));

    httpclient.execute(
        httpGet,
        new FutureCallback<>() {
          @Override
          public void completed(final HttpResponse endpointResponse) {
            try {
              handleResponse(inbound, ctx, endpointResponse);
            } catch (Exception e) {
              log.error(e.getMessage(), e);
            }
          }

          @Override
          public void failed(final Exception ex) {
            httpGet.abort();
            log.error(ex.getMessage(), ex);
          }

          @Override
          public void cancelled() {
            httpGet.abort();
          }
        });
  }

  private void handleResponse(
      final FullHttpRequest fullRequest,
      final ChannelHandlerContext ctx,
      final HttpResponse endpointResponse) {
    FullHttpResponse response = null;
    try {
      byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());
      response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

      response.headers().set("Content-Type", "application/json");
      response
          .headers()
          .setInt(
              "Content-Length",
              Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

      responseFilter.filter(response);

      for (Header e : endpointResponse.getAllHeaders()) {
        response.headers().set(e.getName(), e.getValue());
        log.info("header:{} => {}", e.getName(), e.getValue());
      }
    } catch (Exception e) {
      response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
      exceptionCaught(ctx, e);
    } finally {
      if (fullRequest != null) {
        if (!HttpUtil.isKeepAlive(fullRequest)) {
          ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
          // response.headers().set(CONNECTION, KEEP_ALIVE);
          ctx.write(response);
        }
      }
      ctx.flush();
      // ctx.close();
    }
  }

  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error(cause.getMessage(), cause);
    ctx.close();
  }
}
