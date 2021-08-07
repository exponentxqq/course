package com.course.spring05.starter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring05") // 自动获取配置文件中前缀为http的属性，把值传入对象参数
@Setter
@Getter
public class Spring05StarterProperties {
  private String studentName = "autoconfig";
}
