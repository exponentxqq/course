package com.course.nio03.gateway.router.impl;

import com.course.nio03.gateway.domain.ServerConfig;
import com.course.nio03.gateway.router.HttpEndpointRouter;
import java.util.List;
import java.util.Random;

public class RandomHttpEndpointRouter implements HttpEndpointRouter {

  @Override
  public ServerConfig route(List<ServerConfig> urls) {
    int size = urls.size();
    Random random = new Random(System.currentTimeMillis());
    return urls.get(random.nextInt(size));
  }
}
