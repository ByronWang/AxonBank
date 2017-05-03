package com.nebula.cqrs.axonweb;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import com.nebula.cqrs.axon.CQRSBuilder;
import com.nebula.cqrs.axon.CQRSContext;
import com.nebula.cqrs.axon.DomainListener;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.PojoBuilder;
import com.nebula.cqrs.axonweb.asm.config.CQRSAxonConfigBuilder;
import com.nebula.cqrs.axonweb.asm.query.CQRSRepositoryBuilder;
import com.nebula.cqrs.axonweb.asm.query.CQRSWebEntryBuilder;
import com.nebula.cqrs.axonweb.asm.web.CQRSWebControllerBuilder;
import com.nebula.cqrs.core.asm.Field;

public class CQRSWebSpringApplicationListener implements ApplicationListener<ApplicationPreparedEvent>, DomainListener {

	CQRSBuilder cqrsBuilder;

	public CQRSWebSpringApplicationListener(CQRSBuilder cqrsBuilder) {
		this.cqrsBuilder = cqrsBuilder;
	}

	List<Type> beanTypes = new ArrayList<>();

	@Override
	public void define(CQRSContext ctx, DomainDefinition domainDefinition) {
		Type typeController = domainDefinition.typeOf("Controller");
		Type typeRepository = domainDefinition.typeOf("Repository");
		Type typeEntry = domainDefinition.typeOf("Entry");
		Type typeConfig = domainDefinition.typeOf("AxonConfig");
		Type typeCommandHandler = domainDefinition.typeOf("CommandHandler");

		try {
			ctx.defineClass(typeEntry.getClassName(), CQRSWebEntryBuilder.dump(typeEntry, domainDefinition.fields));

			for (Command command : domainDefinition.commands) {
				Type typeDto = domainDefinition.typeOf(DomainDefinition.toCamelUpper(command.actionName) + "Dto");
				Field[] fields;
				if (command.ctorMethod) {
					List<Field> ctorFields = new ArrayList<>();
					for (int i = 0; i < command.fields.length; i++) {
						if (!command.fields[i].identifier) {
							ctorFields.add(command.fields[i]);
						}
					}
					fields = ctorFields.toArray(new Field[0]);
				} else {
					fields = command.fields;
				}
				ctx.defineClass(typeDto.getClassName(), PojoBuilder.dump(typeDto, fields));
			}

			ctx.defineClass(typeController.getClassName(),
					CQRSWebControllerBuilder.dump(typeController, domainDefinition, typeEntry, domainDefinition.commands));

			ctx.defineClass(typeRepository.getClassName(), CQRSRepositoryBuilder.dump(typeRepository, typeEntry));

			ctx.defineClass(typeConfig.getClassName(), CQRSAxonConfigBuilder.dump(typeConfig, domainDefinition.implDomainType, typeRepository, typeCommandHandler));

//			beanTypes.add(typeEntry);
//			beanTypes.add(typeConfig);
//			beanTypes.add(typeController);
			// beanTypes.add(typeRepository);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onApplicationEvent(ApplicationPreparedEvent e) {
		ConfigurableApplicationContext applicationContext = e.getApplicationContext();
		// cqrsBuilder.add(this);
		// String domainName =
		// "org.axonframework.samples.bank.cqrs.MyBankAccount";
		//
		//// PathMatchingResourcePatternResolver
		// pathMatchingResourcePatternResolver = new
		// PathMatchingResourcePatternResolver();
		//// Resource[] resources =
		// pathMatchingResourcePatternResolver.getResources("classpath*:org/axonframework/samples/**/**.class");
		//
		// cqrsBuilder.makeDomainCQRSHelper(domainName);

		ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

		for (Type type : beanTypes) {
			register(beanFactory, type.getClassName());
		}

		// RepositoryFactorySupport factorySupport = new
		// RepositoryFactorySupport() {

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
