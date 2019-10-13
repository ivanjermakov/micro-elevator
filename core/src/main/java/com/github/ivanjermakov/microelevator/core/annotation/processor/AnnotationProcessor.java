package com.github.ivanjermakov.microelevator.core.annotation.processor;

import com.github.ivanjermakov.microelevator.core.annotation.injector.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class AnnotationProcessor implements BeanPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotationProcessor.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Injector.inject(bean);
		return bean;
	}

}