package org.axonframework.samples.bank;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.samples.bank.command.BankAccount;
import org.axonframework.samples.bank.command.BankAccountCommandHandler;
import org.axonframework.spring.config.AxonConfiguration;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import com.nebula.cqrs.axon.CQRSBuilder;
import com.nebula.cqrs.axon.CQRSWebDomainListener;

public class CQRSWebSpringApplicationListener implements ApplicationListener<ApplicationPreparedEvent > {

	CQRSBuilder cqrsBuilder;

	public CQRSWebSpringApplicationListener(CQRSBuilder cqrsBuilder) {
		this.cqrsBuilder = cqrsBuilder;
	}

	@Override
	public void onApplicationEvent(ApplicationPreparedEvent  e) {
		ConfigurableApplicationContext applicationContext = e.getApplicationContext();
		cqrsBuilder.add(new CQRSWebDomainListener());

		String domainName = "org.axonframework.samples.bank.cqrs.MyBankAccount";
		String typeAxonConfig =domainName + "AxonConfig";
		
		String nameController =domainName + "Controller";

		String nameRepository = domainName + "Repository";
		
//		cqrsBuilder.makeDomainCQRSHelper("org.axonframework.samples.bank.command.BankAccount");
		cqrsBuilder.makeDomainCQRSHelper(domainName);
		
		
		ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
						
		register(beanFactory, nameController);

		register(beanFactory, nameRepository);
		
		register(beanFactory, typeAxonConfig);
//
//		AxonConfiguration axonConfiguration = applicationContext.getBean(AxonConfiguration.class);
//		EventBus eventBus = applicationContext.getBean(EventBus.class);
//		BankAccountCommandHandler bankAccountCommandHandler = new BankAccountCommandHandler(axonConfiguration.repository(BankAccount.class), eventBus);
//		((DefaultListableBeanFactory) beanFactory).registerSingleton(BankAccountCommandHandler.class.getName(), bankAccountCommandHandler);
//		register(beanFactory, "org.axonframework.samples.bank.web.BankTransferController");
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
