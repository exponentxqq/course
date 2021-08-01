package com.course.nio03.gateway.inbound;

import com.course.nio03.gateway.domain.ServerConfig;
import com.course.nio03.gateway.filter.HttpRequestFilter;
import com.course.nio03.gateway.filter.impl.HeaderHttpRequestFilter;
import com.course.nio03.gateway.outbound.OutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

  private final OutboundHandler handler;
  private final HttpRequestFilter filter = new HeaderHttpRequestFilter();

  public HttpInboundHandler(List<ServerConfig> proxyServer) {
    this.handler = OutboundHandler.getInstance(proxyServer);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
      log.info("channelRead流量接口请求开始，时间为{}", Instant.now());
      FullHttpRequest fullRequest = (FullHttpRequest) msg;
      String uri = fullRequest.uri();
      log.info("接收到的请求url为{}", uri);
      handler.handle(fullRequest, ctx, filter);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }
}
