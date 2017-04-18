package org.axonframework.samples.bank;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.samples.bank.command.BankAccount;
import org.axonframework.samples.bank.command.BankAccountCommandHandler;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.nebula.cqrs.axon.CQRSBuilder;
import com.nebula.cqrs.axon.CQRSWebDomainListener;

@Deprecated
public class MyApplicationContextInitializer<C extends ConfigurableApplicationContext> implements ApplicationContextInitializer<C> {

	CQRSBuilder cqrsBuilder;

	public MyApplicationContextInitializer(CQRSBuilder cqrsBuilder) {
		this.cqrsBuilder = cqrsBuilder;
	}

	@Override
	public void initialize(C applicationContext) {
		cqrsBuilder.add(new CQRSWebDomainListener());
		
		cqrsBuilder.makeDomainCQRSHelper("org.axonframework.samples.bank.cqrs.MyBankAccount");
		
		ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		AxonConfiguration axonConfiguration= applicationContext.getBean(AxonConfiguration.class);
		EventBus eventBus = applicationContext.getBean(EventBus.class);
		BankAccountCommandHandler bankAccountCommandHandler = 	new BankAccountCommandHandler(axonConfiguration.repository(BankAccount.class), eventBus);
		((DefaultListableBeanFactory) beanFactory).registerSingleton(BankAccountCommandHandler.class.getName(), bankAccountCommandHandler);
		register(beanFactory, "org.axonframework.samples.bank.web.MyBankAccountController");		
		register(beanFactory, "org.axonframework.samples.bank.web.BankTransferController");
	}

	private void register(ConfigurableListableBeanFactory beanFactory, String name) {
		try {
			Class<?> clz = cqrsBuilder.loadClass(name);
			((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(name, BeanDefinitionBuilder.genericBeanDefinition(clz).getBeanDefinition());
		} catch (ClassNotFoundException e) {
			throw new NoSuchBeanDefinitionException(name);
		}
	}

}
