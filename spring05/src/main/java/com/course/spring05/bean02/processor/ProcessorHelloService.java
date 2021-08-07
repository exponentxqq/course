package com.course.spring05.bean02.processor;

import com.course.spring05.bean02.HelloService;

public class ProcessorHelloService implements HelloService {

  @Override
  public String hello() {
    return "Hello ProcessorHelloService!";
  }
}
