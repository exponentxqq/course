package com.course.nio03.gateway.outbound.okhttp;

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
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class OkhttpOutboundHandler extends BaseOutboundHandler {
  private final OkHttpClient client;

  public OkhttpOutboundHandler(List<ServerConfig> backendUrls) {
    super(backendUrls);
    client =
        new Builder().callTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
  }

  @Override
  public void handle(
      FullHttpRequest fullRequest, ChannelHandlerContext ctx, HttpRequestFilter filter) {
    ServerConfig serverConfig = router.route(this.backendUrls);
    log.info("route to " + serverConfig.getHost());
    final String url = serverConfig.getHost() + fullRequest.uri();
    filter.filter(fullRequest, ctx);
    proxyService.submit(() -> fetchGet(fullRequest, ctx, url));
  }

  private void fetchGet(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx, String url) {
    final Request request =
        new Request.Builder()
            .url(url)
            .header(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())
            .header("mao", fullHttpRequest.headers().get("mao"))
            .get()
            .build();
    client
        .newCall(request)
        .enqueue(
            new Callback() {
              @Override
              public void onFailure(@NotNull Call call, @NotNull IOException e) {
                exceptionCaught(ctx, e);
              }

              @Override
              public void onResponse(@NotNull Call call, @NotNull Response response)
                  throws IOException {
                try {
                  handleResponse(fullHttpRequest, ctx, response);
                } catch (Exception e) {
                  log.error(e.getMessage(), e);
                }
              }
            });
  }

  private void handleResponse(
      final FullHttpRequest fullRequest,
      final ChannelHandlerContext ctx,
      final Response endpointResponse) {
    FullHttpResponse response = null;
    try {
      final byte[] body = endpointResponse.body().bytes();
      response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

      response.headers().set("Content-Type", "application/json");
      response.headers().setInt("Content-Length", body.length);

      responseFilter.filter(response);

      for (Pair<? extends String, ? extends String> header : endpointResponse.headers()) {
        response.headers().set(header.getFirst(), header.getSecond());
        log.info("header:{} => {}", header.getFirst(), header.getSecond());
      }
    } catch (Exception e) {
      response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
      exceptionCaught(ctx, e);
    } finally {
      if (fullRequest != null) {
        if (!HttpUtil.isKeepAlive(fullRequest)) {
          ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
          ctx.write(response);
        }
      }
      ctx.flush();
    }
  }

  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error(cause.getMessage(), cause);
    ctx.close();
  }
}
