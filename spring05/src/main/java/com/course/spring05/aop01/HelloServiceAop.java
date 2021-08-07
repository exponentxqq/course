package com.course.spring05.aop01;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class HelloServiceAop {
  @Pointcut(value = "execution(* com.course.spring05.aop01.HelloService.*(..))")
  public void point() {
    // aop point cut
  }

  @Before(value = "point()")
  public void before() {
    log.info("========> begin... //2");
  }

  @AfterReturning(value = "point()")
  public void after() {
    log.info("========> after... //4");
  }

  @Around("point()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info("========> around begin //1");
    final Object result = joinPoint.proceed();
    log.info("========> around after //3");
    return result;
  }
}
