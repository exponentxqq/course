package com.course.spring05.bean02.configuration;

import com.course.spring05.bean02.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Main {
  public static void main(String[] args) {
    final AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(BeanConfiguration.class);
    final ConfigurationService service = context.getBean(ConfigurationService.class);
    log.info(service.hello());
    final HelloService beanHelloService = (HelloService) context.getBean("beanHelloService");
    log.info(beanHelloService.hello());
  }
}
