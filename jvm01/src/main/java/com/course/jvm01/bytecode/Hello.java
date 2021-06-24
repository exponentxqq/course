package com.course.jvm01.bytecode;

public class Hello {

  // 自己写一个简单的 Hello.java，里面需要涉及基本类型，四则运行，if 和 for，然后自己分析一下对应的字节码，有问题群里讨论
  public static void main(String[] args) {
    int a = 1;
    long b = 2;
    String c = "ccc";
    int count = 8;
    int[] nums = {111, 222};
    String result = String.valueOf(a);
    for (int i = 0; i < count; i++) {
      a += i;
    }

    if (nums[1] - nums[0] - a > b) {
      result += c;
    }
  }
}
