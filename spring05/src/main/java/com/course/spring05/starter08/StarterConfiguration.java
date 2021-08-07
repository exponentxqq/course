package com.course.spring05.starter08;

import com.course.spring05.starter.Spring05StarterAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.course.spring05.starter08")
@Import(Spring05StarterAutoConfiguration.class)
public class StarterConfiguration {}
