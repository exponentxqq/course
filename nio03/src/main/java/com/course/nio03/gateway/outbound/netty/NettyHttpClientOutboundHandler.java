package com.course.nio03.gateway.outbound.netty;

import com.course.nio03.gateway.domain.ServerConfig;
import com.course.nio03.gateway.filter.HttpRequestFilter;
import com.course.nio03.gateway.outbound.BaseOutboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpClientOutboundHandler extends BaseOutboundHandler {
  private static NettyHttpClientOutboundHandler instance;
  private final EventLoopGroup group;

  public static NettyHttpClientOutboundHandler getInstance(List<ServerConfig> configs) {
    if (instance == null) {
      instance = new NettyHttpClientOutboundHandler(configs);
    }
    return instance;
  }

  private NettyHttpClientOutboundHandler(List<ServerConfig> backends) {
    super(backends);
    group = new NioEventLoopGroup();
  }

  private Channel createClient(
      ServerConfig serverConfig, FullHttpRequest request, ChannelHandlerContext ctx) {
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap
          .group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline()
                      .addLast(new HttpClientCodec())
                      .addLast(new HttpObjectAggregator(1024 * 1024))
                      .addLast(new NettyClientHandler(responseFilter, request, ctx));
                }
              });
      final ChannelFuture channelFuture =
          bootstrap.connect("127.0.0.1", serverConfig.getPort()).sync();
      return channelFuture.channel();
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
      throw new RuntimeException("connect failed");
    }
  }

  @Override
  public void handle(
      FullHttpRequest fullRequest, ChannelHandlerContext ctx, HttpRequestFilter filter) {
    ServerConfig serverConfig = router.route(this.backendUrls);
    log.info("route to " + serverConfig.getHost());
    filter.filter(fullRequest, ctx);
    FullHttpRequest request =
        new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, fullRequest.uri());
    request.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    request.headers().add(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
    final Channel client = createClient(serverConfig, request, ctx);
    client.writeAndFlush(request);
    try {
      client.closeFuture().sync();
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
    }
    client.close();
  }
}
