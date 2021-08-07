package com.course.spring05.starter;

import javax.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Spring05StarterProperties.class)
@ComponentScan("com.course.spring05.starter")
public class Spring05StarterAutoConfiguration {
  @Resource Spring05StarterProperties properties;

  @Bean
  public Student student100() {
    return Student.create(properties.getStudentName());
  }
}
