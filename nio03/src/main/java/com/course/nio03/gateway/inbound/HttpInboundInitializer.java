package com.course.nio03.gateway.inbound;

import com.course.nio03.gateway.domain.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {
  private final List<ServerConfig> proxyServer;

  @Override
  public void initChannel(SocketChannel ch) {
    ch.pipeline()
        .addLast(new HttpServerCodec())
        .addLast(new HttpObjectAggregator(1024 * 1024))
        .addLast(new HttpInboundHandler(this.proxyServer));
  }
}
