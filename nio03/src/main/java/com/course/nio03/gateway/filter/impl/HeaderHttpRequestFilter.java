package com.course.nio03.gateway.filter.impl;

import com.course.nio03.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HeaderHttpRequestFilter implements HttpRequestFilter {
  @Override
  public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
    fullRequest.headers().set("mao", "soul");
  }
}
