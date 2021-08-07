package com.course.spring05.aop01;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan({"com.course.spring05.aop01"})
@EnableAspectJAutoProxy
public class AopConfiguration {}
