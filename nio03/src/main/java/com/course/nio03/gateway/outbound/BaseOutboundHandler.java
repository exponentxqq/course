package com.course.nio03.gateway.outbound;

import com.course.nio03.gateway.domain.ServerConfig;
import com.course.nio03.gateway.filter.HttpResponseFilter;
import com.course.nio03.gateway.filter.impl.HeaderHttpResponseFilter;
import com.course.nio03.gateway.outbound.httpclient.NamedThreadFactory;
import com.course.nio03.gateway.router.HttpEndpointRouter;
import com.course.nio03.gateway.router.impl.WeightHttpEndpointRouter;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseOutboundHandler implements OutboundHandler {
  protected final HttpEndpointRouter router = WeightHttpEndpointRouter.getInstance();
  protected final HttpResponseFilter responseFilter = new HeaderHttpResponseFilter();

  protected final List<ServerConfig> backendUrls;
  protected final ExecutorService proxyService;

  protected BaseOutboundHandler(List<ServerConfig> backends) {
    backendUrls =
        backends.stream()
            .map(config -> ServerConfig.create(formatUrl(config.getHost()), 1))
            .collect(Collectors.toList());

    int cores = Runtime.getRuntime().availableProcessors();
    long keepAliveTime = 1000;
    int queueSize = 2048;
    RejectedExecutionHandler handler =
        new ThreadPoolExecutor.CallerRunsPolicy();
    proxyService =
        new ThreadPoolExecutor(
            cores,
            cores,
            keepAliveTime,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueSize),
            new NamedThreadFactory("proxyService"),
            handler);
  }

  protected String formatUrl(String backend) {
    return backend.endsWith("/") ? backend.substring(0, backend.length() - 1) : backend;
  }
}
