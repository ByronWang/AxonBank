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
import com.nebula.cqrs.axon.pojo.Field;
import com.nebula.cqrs.axon.pojo.PojoBuilder;
import com.nebula.cqrs.axonweb.asm.CQRSAxonConfigBuilder;
import com.nebula.cqrs.axonweb.asm.CQRSRepositoryBuilder;
import com.nebula.cqrs.axonweb.asm.CQRSWebControllerBuilder;
import com.nebula.cqrs.axonweb.asm.CQRSWebEntryBuilder;

public class CQRSWebSpringApplicationListener implements ApplicationListener<ApplicationPreparedEvent>, DomainListener {

	CQRSBuilder cqrsBuilder;

	public CQRSWebSpringApplicationListener(CQRSBuilder cqrsBuilder) {
		this.cqrsBuilder = cqrsBuilder;
	}

	List<Type> beanTypes = new ArrayList<>();

	@Override
	public void define(CQRSContext ctx, DomainDefinition domainDefinition) {
		Type typeController = Type.getObjectType(domainDefinition.type.getInternalName() + "Controller");
		Type typeRepository = Type.getObjectType(domainDefinition.type.getInternalName() + "Repository");
		Type typeEntry = Type.getObjectType(domainDefinition.type.getInternalName() + "Entry");
		Type typeConfig = Type.getObjectType(domainDefinition.type.getInternalName() + "AxonConfig");
		Type typeCommandHandler = Type.getObjectType(domainDefinition.type.getInternalName() + "CommandHandler");

		try {
			ctx.defineClass(typeEntry.getClassName(), CQRSWebEntryBuilder.dump(typeEntry, domainDefinition.fields));

			for (Command command : domainDefinition.commands) {
				Type typeDto = Type.getObjectType(domainDefinition.type.getInternalName() + DomainDefinition.toCamelUpper(command.actionName) + "Dto");
				Field[] fields;
				if (command.ctorMethod) {
					List<Field> ctorFields = new ArrayList<>();
					for (int i = 0; i < command.fields.length; i++) {
						if (!command.fields[i].idField) {
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
					CQRSWebControllerBuilder.dump(typeController, domainDefinition.type, typeEntry, domainDefinition.commands));

			ctx.defineClass(typeRepository.getClassName(), CQRSRepositoryBuilder.dump(typeRepository, typeEntry));

			ctx.defineClass(typeConfig.getClassName(), CQRSAxonConfigBuilder.dump(typeConfig, domainDefinition.type, typeRepository, typeCommandHandler));

			beanTypes.add(typeEntry);
			beanTypes.add(typeConfig);
			beanTypes.add(typeController);
			// beanTypes.add(typeRepository);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onApplicationEvent(ApplicationPreparedEvent e) {
		ConfigurableApplicationContext applicationContext = e.getApplicationContext();
		cqrsBuilder.add(this);
		String domainName = "org.axonframework.samples.bank.cqrs.MyBankAccount";

		cqrsBuilder.makeDomainCQRSHelper(domainName);

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
