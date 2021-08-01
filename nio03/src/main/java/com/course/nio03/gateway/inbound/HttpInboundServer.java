package com.course.nio03.gateway.inbound;

import com.course.nio03.gateway.domain.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HttpInboundServer {

  private final int port;

  private final List<ServerConfig> proxyServers;

  public void run() throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup(16);

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.option(ChannelOption.SO_BACKLOG, 128)
          .childOption(ChannelOption.TCP_NODELAY, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.SO_REUSEADDR, true)
          .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
          .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
          .childOption(EpollChannelOption.SO_REUSEPORT, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.DEBUG))
          .childHandler(new HttpInboundInitializer(this.proxyServers));

      Channel ch = b.bind(port).sync().channel();
      log.info("开启netty http服务器[http://127.0.0.1:{}]", port);
      ch.closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
