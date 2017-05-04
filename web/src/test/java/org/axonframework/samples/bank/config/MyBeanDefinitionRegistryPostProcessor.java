package org.axonframework.samples.bank.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.stereotype.Component;

import com.nebula.cqrs.axon.CQRSBuilder;
import com.nebula.cqrs.axonweb.CQRSWebSpringApplicationListener;

@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	CQRSBuilder cqrsBuilder;
	CQRSWebSpringApplicationListener applicationListener;
	BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

	public MyBeanDefinitionRegistryPostProcessor() {
		this.cqrsBuilder = new CQRSBuilder();
		// this.applicationListener = new CQRSWebSpringApplicationListener();
		cqrsBuilder.add(applicationListener);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
//		AnnotatedBeanDefinitionReader beanDefinitionReader = new AnnotatedBeanDefinitionReader(beanDefinitionRegistry);

		System.out.println("## MyBeanDefinitionRegistryPostProcessor ----" + beanDefinitionRegistry.getBeanDefinitionCount());
//
//		String[] candidateNames = beanDefinitionRegistry.getBeanDefinitionNames();
//		for (String beanName : candidateNames) {
//			BeanDefinition beanDef = beanDefinitionRegistry.getBeanDefinition(beanName);
//			if (AggregateClassUtils.checkAggregateClassCandidate(beanDef)) {
//				String className = beanDef.getBeanClassName();
//				beanDefinitionRegistry.removeBeanDefinition(beanName);
//				System.out.println("## removeBeanDefinition ----" + beanDefinitionRegistry.getBeanDefinitionCount());
//				doDeal(beanDefinitionRegistry, className);
//				System.out.println("## finished doDeal      ----" + beanDefinitionRegistry.getBeanDefinitionCount());
//			}
//		}
//		System.out.println("## MyBeanDefinitionRegistryPostProcessor ----" + beanDefinitionRegistry.getBeanDefinitionCount());

	}

	private void doDeal(BeanDefinitionRegistry beanDefinitionRegistry, String domainClassName) {
		System.out.println("\t" + domainClassName);
		cqrsBuilder.makeDomainCQRSHelper(domainClassName);
		registerDomain(beanDefinitionRegistry, domainClassName);
		// applicationListener.beanTypes.forEach(t ->
		// register(beanDefinitionRegistry, t.getClassName()));
		// String commandHandlerClassName = beanDef.getBeanClassName() +
		// "CommandHandler";
		//
		// register(beanDefinitionRegistry, commandHandlerClassName);

		// BeanDefinition beanDef =
		// beanDefinitionRegistry.getBeanDefinition(commandHandlerClassName);
		// BeanDefinitionBuilder.rootBeanDefinition(getClass(), null)
		// Type typeConfig =
		// Type.getObjectType(beanDef.getBeanClassName().replace('.','/') +
		// "AxonConfig");
		// BeanDefinitionBuilder.
	}

	@SuppressWarnings("unchecked")
	private void register(BeanDefinitionRegistry beanDefinitionRegistry, String name) {
		try {
			Class<?> clz = cqrsBuilder.loadClass(name);
			// beanDefinitionReader.registerBean(clz,Bean.class);
			BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(clz).getBeanDefinition();
			String beanName = beanNameGenerator.generateBeanName(beanDefinition, beanDefinitionRegistry);
			beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
		} catch (ClassNotFoundException e) {
			throw new NoSuchBeanDefinitionException(name);
		}
	}

	private void registerDomain(BeanDefinitionRegistry beanDefinitionRegistry, String name) {
		try {
			Class<?> clz = cqrsBuilder.loadClass(name);
			// beanDefinitionReader.registerBean(clz,Bean.class);
			BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(clz).getBeanDefinition();
			beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			String beanName = beanNameGenerator.generateBeanName(beanDefinition, beanDefinitionRegistry);
			beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
		} catch (ClassNotFoundException e) {
			throw new NoSuchBeanDefinitionException(name);
		}
	}
}
