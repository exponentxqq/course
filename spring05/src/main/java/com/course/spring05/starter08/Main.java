package com.course.spring05.starter08;

import com.course.spring05.starter.Klass;
import com.course.spring05.starter.School;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** @date 2021/8/7 20:51 */
public class Main {
  public static void main(String[] args) {
    final AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(StarterConfiguration.class);
    final School school = context.getBean(School.class);
    school.ding();
    final Klass klass = context.getBean(Klass.class);
    klass.dong();
  }
}
