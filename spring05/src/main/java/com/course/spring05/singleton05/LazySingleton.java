package com.course.spring05.singleton05;

public class LazySingleton {
  private static LazySingleton instance = null;

  private LazySingleton() {}

  // 如果编写的是多线程程序，则必须使用 synchronized，否则该单例是线程不安全的
  // 加上 synchronized 能保证线程安全，但是每次访问时都要同步，会影响性能，且消耗更多的资源
  public static synchronized LazySingleton getInstance() {
    if (instance == null) {
      instance = new LazySingleton();
    }
    return instance;
  }
}
