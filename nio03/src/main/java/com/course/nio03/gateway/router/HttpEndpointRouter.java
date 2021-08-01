package com.course.nio03.gateway.router;

import com.course.nio03.gateway.domain.ServerConfig;
import java.util.List;

public interface HttpEndpointRouter {
  ServerConfig route(List<ServerConfig> endpoints);
}
