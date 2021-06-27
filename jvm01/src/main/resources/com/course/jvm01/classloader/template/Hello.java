package com.course.jvm01.classloader;

public class Hello {
  static {
    System.out.println("Hello class loaded!!!");
  }

  private final String target;

  public Hello(String target) {
    this.target = target;
  }

  public void hello() {
    System.out.println("hello, " + target);
  }
}
