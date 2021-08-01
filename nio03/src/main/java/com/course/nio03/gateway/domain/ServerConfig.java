package com.course.nio03.gateway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerConfig {
  private final String domain;
  private final int port;
  private final String host;
  private final int weight;

  public static ServerConfig create(String host, int weight) {
    final String[] hostInfo = host.split(":");
    return new ServerConfig(hostInfo[1].substring(2), Integer.parseInt(hostInfo[2]), host, weight);
  }
}
