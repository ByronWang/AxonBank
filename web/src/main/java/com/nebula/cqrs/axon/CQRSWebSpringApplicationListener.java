package com.nebula.cqrs.axon;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Command;
import com.nebula.cqrs.axon.CQRSDomainBuilder.Field;

public class CQRSWebSpringApplicationListener implements ApplicationListener<ApplicationPreparedEvent>, DomainListener {

	CQRSBuilder cqrsBuilder;

	public CQRSWebSpringApplicationListener(CQRSBuilder cqrsBuilder) {
		this.cqrsBuilder = cqrsBuilder;
	}

	List<Type> beanTypes = new ArrayList<>();

	@Override
	public void define(CQRSContext ctx, Type typeDomain, CQRSDomainBuilder cqrs, ClassReader domainClassReader) {
		// String name =
		// "org.axonframework.samples.bank.web.MyBankAccountController";
		Type typeController = Type.getObjectType(typeDomain.getInternalName() + "Controller");
		Type typeRepository = Type.getObjectType(typeDomain.getInternalName() + "Repository");
		Type typeEntry = Type.getObjectType(typeDomain.getInternalName() + "Entry");
		Type typeConfig = Type.getObjectType(typeDomain.getInternalName() + "AxonConfig");
		Type typeCommandHandler = Type.getObjectType(typeDomain.getInternalName() + "CommandHandler");

		try {
			ctx.defineClass(typeEntry.getClassName(), PojoBuilder.dump(typeEntry, cqrs.domain.fields));

			for (Command command : cqrs.commands) {
				Type typeDto = Type.getObjectType(typeDomain.getInternalName() + CQRSDomainBuilder.toCamelUpper(command.actionName) + "Dto");
				List<Field> fields;
				if (command.ctorMethod) {
					fields = new ArrayList<>();
					for (int i = 0; i < command.fields.size(); i++) {
						if (!command.fields.get(i).idField) {
							fields.add(command.fields.get(i));
						}
					}
				} else {
					fields = command.fields;
				}
				ctx.defineClass(typeDto.getClassName(), PojoBuilder.dump(typeDto, fields));
			}

			ctx.defineClass(typeController.getClassName(), CQRSWebControllerBuilder.dump(typeDomain, typeController, typeRepository, typeEntry, cqrs));

			ctx.defineClass(typeRepository.getClassName(), CQRSRepositoryBuilder.dump(typeRepository, typeEntry));

			ctx.defineClass(typeConfig.getClassName(), CQRSAxonConfigBuilder.dump(typeDomain, typeConfig, typeCommandHandler));

			beanTypes.add(typeConfig);
			beanTypes.add(typeController);

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
