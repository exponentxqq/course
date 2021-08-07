package com.course.spring05.bean02.configuration;

import com.course.spring05.bean02.HelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/** @date 2021/8/6 20:14 */
@Configuration
@ComponentScan("com.course.spring05.bean02.configuration")
public class BeanConfiguration {
  @Bean
  public HelloService beanHelloService() {
    return () -> "Hello Bean Annotation HelloService!";
  }
}
