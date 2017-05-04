package org.axonframework.samples.bankcqrs;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeansException;

@Component
public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {

	// simply return the instantiated bean as-is
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean; // we could potentially return any object reference here...
	}

	static Package thisPackage = InstantiationTracingBeanPostProcessor.class.getPackage();

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean == null) return bean;
		if(bean.getClass()==null)return bean;
		if(bean.getClass().getPackage()==null)return bean;
		if (bean.getClass().getPackage().getName().startsWith(thisPackage.getName())) {
			System.out.println("Bean '" + beanName + "' created : " + bean.getClass().getName());
		}
		return bean;
	}

}