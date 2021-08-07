package com.course.spring05.bean02.processor;

import com.course.spring05.bean02.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Main {
  public static void main(String[] args) {
    final AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(ProcessorConfiguration.class);
    final HelloService service1 = (HelloService) context.getBean("helloService1");
    final HelloService service2 = (HelloService) context.getBean("helloService2");
    log.info("service1: " + service1.hello());
    log.info("service2: " + service2.hello());
    log.info("service1 == service2: " + (service1 == service2));
  }
}
