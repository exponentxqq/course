package com.course.spring05.bean02.xml;

import com.course.spring05.bean02.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Slf4j
public class Main {
  public static void main(String[] args) {
    final ClassPathXmlApplicationContext context =
        new ClassPathXmlApplicationContext("bean/xml/application-context.xml");
    final HelloService service = context.getBean(XmlHelloService.class);
    log.info(service.hello());
  }
}
