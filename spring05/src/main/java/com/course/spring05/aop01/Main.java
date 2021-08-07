package com.course.spring05.aop01;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Main {
  public static void main(String[] args) {
    final AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(AopConfiguration.class);
    final HelloService helloService = context.getBean(HelloService.class);
    log.info(helloService.hello());
  }
}
