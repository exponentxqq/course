package com.course.spring05.bean02.xml;

import com.course.spring05.bean02.HelloService;

public class XmlHelloService implements HelloService {

  @Override
  public String hello() {
    return "Hello XmlHelloService!";
  }
}
