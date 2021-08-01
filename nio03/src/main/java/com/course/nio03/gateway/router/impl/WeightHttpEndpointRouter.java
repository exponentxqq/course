package com.course.nio03.gateway.router.impl;

import com.course.nio03.gateway.domain.ServerConfig;
import com.course.nio03.gateway.router.HttpEndpointRouter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;

public class WeightHttpEndpointRouter implements HttpEndpointRouter {
  private static final WeightHttpEndpointRouter instance = new WeightHttpEndpointRouter();
  private final Map<String, Integer> weightCountMap = new HashMap<>();
  private final Map<String, ServerConfig> configMap = new HashMap<>();

  public static WeightHttpEndpointRouter getInstance() {
    return instance;
  }

  private WeightHttpEndpointRouter() {}

  @Override
  public synchronized ServerConfig route(List<ServerConfig> endpoints) {
    for (ServerConfig endpoint : endpoints) {
      if (!configMap.containsKey(endpoint.getHost())) {
        configMap.put(endpoint.getHost(), endpoint);
      }
      if (!weightCountMap.containsKey(endpoint.getHost())) {
        weightCountMap.put(endpoint.getHost(), 1);
        return endpoint;
      }
    }
    List<Map.Entry<String, Integer>> list = new ArrayList<>(weightCountMap.entrySet());
    list.sort(new WeightComparator(configMap));
    final Entry<String, Integer> result = list.get(0);
    weightCountMap.put(result.getKey(), result.getValue() + 1);
    return configMap.get(result.getKey());
  }
}

@AllArgsConstructor
class WeightComparator implements Comparator<Entry<String, Integer>> {
  private final Map<String, ServerConfig> configMap;

  @Override
  public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
    float result =
        (configMap.get(o1.getKey()).getWeight() / (float) o1.getValue())
            - (configMap.get(o2.getKey()).getWeight() / (float) o2.getValue());
    if (result == 0) {
      return 0;
    }
    return result > 0 ? -1 : 1;
  }
}
