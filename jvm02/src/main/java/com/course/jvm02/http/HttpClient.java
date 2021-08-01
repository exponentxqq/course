package com.course.jvm02.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpClient {
  private static final String NETTY_SERVER_URL = "http://127.0.0.1:8020/test";

  public static void main(String[] args) throws IOException {
    requestByOkHttp();
    requestByHttpClient();
    requestByNetty();
  }

  private static void requestByOkHttp() throws IOException {
    final OkHttpClient client =
        new Builder().callTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
    final Request reqeust = new Request.Builder().url(NETTY_SERVER_URL).get().build();
    final Response response = client.newCall(reqeust).execute();
    System.out.println(response.body().string());
  }

  private static void requestByHttpClient() throws IOException {
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet httpGet = new HttpGet(NETTY_SERVER_URL);
    final CloseableHttpResponse response = httpClient.execute(httpGet);
    System.out.println(EntityUtils.toString(response.getEntity()));
  }

  private static void requestByNetty() {
    EventLoopGroup group = new NioEventLoopGroup();
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
                      .addLast(new HttpObjectAggregator(65536))
                      .addLast(
                          new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg)
                                throws Exception {
                              System.out.println("msg -> " + msg);
                              if (msg instanceof FullHttpResponse) {
                                FullHttpResponse response = (FullHttpResponse) msg;
                                ByteBuf buf = response.content();
                                String result = buf.toString(CharsetUtil.UTF_8);
                                System.out.println("response -> " + result);
                              }
                              ctx.close();
                            }
                          });
                }
              });
      final ChannelFuture future = bootstrap.connect("127.0.0.1", 8020).sync();
      FullHttpRequest request =
          new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/test");
      request.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      request.headers().add(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
      future.channel().writeAndFlush(request);
      future.channel().closeFuture().sync();
      future.channel().close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      System.out.println("done....");
      group.shutdownGracefully();
    }
  }
}
