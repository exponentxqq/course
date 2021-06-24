package com.course.jvm01.bytecode;

public class Calculate {

  private int num = 0;

  public void incr(int i) {
    for (int j = 0; j < i; j++) {
      num++;
    }
  }
}
