package com.course.spring05.starter;

import javax.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
public class School implements ISchool {

  // Resource
  @Autowired // primary
  Klass class1;

  @Resource(name = "student100")
  Student student100;

  @Override
  public void ding() {
    log.info(
        "Class1 have "
            + this.class1.getStudents().size()
            + " students and one is "
            + this.student100);
  }
}
