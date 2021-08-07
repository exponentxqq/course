package com.course.spring05.bean02.configuration;

import org.springframework.stereotype.Component;

/** @date 2021/8/6 20:15 */
@Component
public class ConfigurationService {
  public String hello() {
    return "Hello Bean Configuration!";
  }
}
