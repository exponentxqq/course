package com.course.spring05.bean02.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** @date 2021/8/6 20:10 */
@Slf4j
public class Main {
  public static void main(String[] args) {
    final ClassPathXmlApplicationContext context =
        new ClassPathXmlApplicationContext("bean/annotation/application-context.xml");
    final AnnotationService service = context.getBean(AnnotationService.class);
    log.info(service.hello());
  }
}
