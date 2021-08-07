package com.course.spring05.starter08;

import com.course.spring05.starter.School;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** @date 2021/8/7 21:44 */
@SpringBootApplication
@Slf4j
public class CommandMain implements CommandLineRunner {
  @Autowired private School school;

  public static void main(String[] args) {
    SpringApplication.run(CommandMain.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    school.ding();
  }
}
