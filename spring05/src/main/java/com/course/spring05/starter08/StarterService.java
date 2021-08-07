package com.course.spring05.starter08;

import com.course.spring05.starter.Klass;
import com.course.spring05.starter.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @date 2021/8/7 21:37 */
@Component
public class StarterService {
  @Autowired private School school;

  @Autowired private Klass klass;
}
