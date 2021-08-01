package com.course.nio03.gateway.outbound.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import com.course.nio03.gateway.filter.HttpResponseFilter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
  private final HttpResponseFilter responseFilter;
  private final FullHttpRequest fullHttpRequest;
  private final ChannelHandlerContext responseContext;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof FullHttpResponse) {
      handleResponse(fullHttpRequest, responseContext, (FullHttpResponse) msg);
    }
    ctx.close();
  }

  private void handleResponse(
      final FullHttpRequest fullRequest,
      final ChannelHandlerContext ctx,
      final FullHttpResponse endpointResponse) {
    FullHttpResponse response = null;
    try {
      final ByteBuf body = endpointResponse.content();
      response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

      response.headers().set("Content-Type", "application/json");
      response.headers().setInt("Content-Length", body.readableBytes());

      responseFilter.filter(response);

      for (Entry<String, String> header : endpointResponse.headers()) {
        response.headers().set(header.getKey(), header.getValue());
        log.info("header: {} => {}", header.getKey(), header.getValue());
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

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error(cause.getMessage(), cause);
    ctx.close();
  }
}
