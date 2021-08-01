package com.course.nio03.gateway;

import com.course.nio03.gateway.domain.ServerConfig;
import com.course.nio03.gateway.inbound.HttpInboundServer;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GatewayApplication {
  public static final String GATEWAY_NAME = "NIOGateway";
  public static final String GATEWAY_VERSION = "1.0.0";

  public static void main(String[] args) {
    String proxyPort = System.getProperty("proxyPort", "8888");

    String proxyServers =
        System.getProperty("proxyServers", "http://127.0.0.1:8020,http://127.0.0.1:8021");
    int port = Integer.parseInt(proxyPort);
    log.info("{} {} starting...", GATEWAY_NAME, GATEWAY_VERSION);
    HttpInboundServer server =
        new HttpInboundServer(
            port,
            Arrays.asList(proxyServers.split(",")).stream()
                .map(serverHost -> ServerConfig.create(serverHost, 1))
                .collect(Collectors.toList()));
    log.info(
        "{} {} started at http://127.0.0.1:{} for server:{}",
        GATEWAY_NAME,
        GATEWAY_VERSION,
        port,
        server);
    try {
      server.run();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }
}
