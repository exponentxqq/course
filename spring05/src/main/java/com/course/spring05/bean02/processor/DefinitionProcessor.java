package com.course.spring05.bean02.processor;

import com.course.spring05.bean02.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * 在扫描xml，构建好dom之后，会走到这个类
 *
 * <p>可以获得当前扫描到的所有bean，此时这些bean还未初始化，即所有的bean的成员属性现在都是null(设了默认值的除外)
 */
@Component
@Slf4j
public class DefinitionProcessor implements BeanDefinitionRegistryPostProcessor {

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
      throws BeansException {
    RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(ProcessorHelloService.class);
    beanDefinitionRegistry.registerBeanDefinition("helloService1", rootBeanDefinition);
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    final HelloService processorBean = new ProcessorHelloService();
    beanFactory.registerSingleton("helloService2", processorBean);
  }
}
