package com.course.spring05.starter;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
public class Klass {

  @Autowired List<Student> students;

  public void dong() {
    log.info(this.getStudents().stream().map(Student::toString).collect(Collectors.joining(";")));
  }
}
