package com.course.spring05.singleton05;

import lombok.Getter;

/** 非线程安全 单例 */
public class NTSSingleton {
  private static NTSSingleton instance = null;

  @Getter private int count = 0;

  /** 这种方式的单例模式也是线程不安全的，多线程下需要加上 synchronized 关键字 */
  public static synchronized NTSSingleton getInstance() {
    if (instance == null) {
      instance = new NTSSingleton();
    }
    return instance;
  }
}
