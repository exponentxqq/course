package com.course.jvm01.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

public class ResourceClassLoader extends ClassLoader {
  public static void main(String[] args) {
    try {
      final Constructor<?> constructor =
          new ResourceClassLoader()
              .findClass("com.course.jvm01.classloader.Hello")
              .getConstructor(String.class);
      final Object hello = constructor.newInstance("world");
      hello.getClass().getDeclaredMethod("hello").invoke(hello);
      System.out.println(hello.toString());
    } catch (Exception e) {
      e.getStackTrace();
    }
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    final String classPath = name.replace(".", "/");
    try (final InputStream inputStream =
        this.getClass().getClassLoader().getResourceAsStream(classPath + ".xlass")) {
      if (inputStream == null) {
        throw new ClassNotFoundException(name);
      }
      final int len = inputStream.available();

      byte[] formattedBytes = new byte[len];
      inputStream.read(formattedBytes);
      for (int i = 0; i < len; i++) {
        formattedBytes[i] = (byte) (255 - formattedBytes[i]);
      }

      return defineClass(name, formattedBytes, 0, len);
    } catch (IOException | ClassNotFoundException e) {
      throw new ClassNotFoundException();
    }
  }
}
