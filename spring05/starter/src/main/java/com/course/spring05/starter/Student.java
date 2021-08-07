package com.course.spring05.starter;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
public class Student implements Serializable, BeanNameAware, ApplicationContextAware {

  private int id;
  private String name;

  private String beanName;
  private ApplicationContext applicationContext;

  public void init() {
    log.info("hello...........");
  }

  public static Student create(String name) {
    return new Student(102, name, null, null);
  }

  public void print() {
    log.info(this.beanName);
    log.info(
        "   context.getBeanDefinitionNames() ===>> "
            + String.join(",", applicationContext.getBeanDefinitionNames()));
  }
}
