package com.course.spring05.bean02.annotation;

import org.springframework.stereotype.Component;

@Component
public class AnnotationService {
  public String hello() {
    return "Hello Bean Annotation!";
  }
}
