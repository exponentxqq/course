package com.course.spring05.aop01;

import org.springframework.stereotype.Component;

@Component
public class HelloService {
  public String hello() {
    return "Hello Spring AOP!";
  }
}
