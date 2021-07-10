package com.course.jvm02.http;

import static io.netty.handler.codec.http.HttpHeaderNames.KEEP_ALIVE;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;

public class HttpServer {
  public static void main(String[] args) throws InterruptedException {
    int port = 8020;
    EventLoopGroup bossGroup = new NioEventLoopGroup(2);
    EventLoopGroup workerGroup = new NioEventLoopGroup(8);

    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap
          .option(ChannelOption.SO_BACKLOG, 128)
          .childOption(ChannelOption.TCP_NODELAY, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.SO_REUSEADDR, true)
          .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
          .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
          .childOption(EpollChannelOption.SO_REUSEPORT, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
      bootstrap
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new HttpInitializer());

      final Channel channel = bootstrap.bind(port).sync().channel();
      System.out.println("netty http server[127.0.0.1:" + port + "] start");
      channel.closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}

class HttpInitializer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ch.pipeline()
        .addLast(new HttpServerCodec())
        .addLast(new HttpObjectAggregator(1024 * 1024))
        .addLast(new HttpHandler());
  }
}

class HttpHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    try {
      FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
      final String uri = fullHttpRequest.uri();
      handle(fullHttpRequest, ctx, uri);
    } catch (Exception e) {
      ReferenceCountUtil.release(msg);
    }
  }

  private void handle(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx, String uri) {
    FullHttpResponse response = null;
    try {
      if (!uri.contains("/test")) {
        throw new RuntimeException("not found");
      }
      String value = "hello, netty!!";
      response =
          new DefaultFullHttpResponse(
              HttpVersion.HTTP_1_1,
              HttpResponseStatus.OK,
              Unpooled.wrappedBuffer(value.getBytes()));
      response
          .headers()
          .set("Content-Type", "application/json")
          .setInt("Content-Length", response.content().readableBytes());
    } catch (Exception e) {
      response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    } finally {
      if (fullHttpRequest != null) {
        if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
          ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
          response.headers().set(HttpHeaderNames.CONNECTION, KEEP_ALIVE);
          ctx.write(response);
        }
      }
    }
  }
}
