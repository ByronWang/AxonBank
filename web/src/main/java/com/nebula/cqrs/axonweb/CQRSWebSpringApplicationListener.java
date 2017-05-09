package com.nebula.cqrs.axonweb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
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
import com.nebula.cqrs.axonweb.asm.query.CQRSWebEventListenerBuilder;
import com.nebula.cqrs.axonweb.asm.query.CQRSWebEventListnerBizLogicClassVisitor;
import com.nebula.cqrs.axonweb.asm.web.CQRSWebControllerBuilder;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.RenameClassVisitor;

public class CQRSWebSpringApplicationListener implements ApplicationListener<ApplicationPreparedEvent>, DomainListener {

	CQRSBuilder cqrsBuilder;

	public CQRSWebSpringApplicationListener(CQRSBuilder cqrsBuilder) {
		this.cqrsBuilder = cqrsBuilder;
	}

	List<Type> beanTypes = new ArrayList<>();

	@Override
	public void define(CQRSContext ctx, DomainDefinition domainDefinition) {
		Type implDomainType = domainDefinition.implDomainType;
		Type typeController = domainDefinition.typeOf("Controller");
		Type typeRepository = domainDefinition.typeOf("Repository");
		Type entryType = domainDefinition.typeOf("Entry");
		Type typeConfig = domainDefinition.typeOf("AxonConfig");
		Type typeCommandHandler = domainDefinition.typeOf("CommandHandler");

		Type eventListenerType = domainDefinition.typeOf("EventListener");
		Type bizLogicType = domainDefinition.typeOf("EventListenerBizLogic");

		try {
			ctx.defineClass(entryType.getClassName(), CQRSWebEntryBuilder.dump(entryType, domainDefinition.fields));

			for (Command command : domainDefinition.commands.values()) {
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
					CQRSWebControllerBuilder.dump(typeController, domainDefinition, entryType, typeRepository, domainDefinition.commands.values()));

			ctx.defineClass(typeRepository.getClassName(), CQRSRepositoryBuilder.dump(typeRepository, entryType, domainDefinition));

			ctx.defineClass(typeConfig.getClassName(),
					CQRSAxonConfigBuilder.dump(typeConfig, domainDefinition.implDomainType, typeRepository, typeCommandHandler));

			makeBizLogic(bizLogicType, ctx, implDomainType, entryType);

			ctx.defineClass(eventListenerType.getClassName(),
					CQRSWebEventListenerBuilder.dump(eventListenerType, typeRepository, bizLogicType, entryType, domainDefinition));

			// beanTypes.add(typeEntry);
			// beanTypes.add(typeConfig);
			// beanTypes.add(typeController);
			// beanTypes.add(typeRepository);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void makeBizLogic(Type bizLogicType, CQRSContext ctx, Type implDomainType, Type entryType) throws IOException {
		{
			ClassReader cr = new ClassReader(implDomainType.getClassName());
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
			CQRSWebEventListnerBizLogicClassVisitor entityEventListener = new CQRSWebEventListnerBizLogicClassVisitor(Opcodes.ASM5, cw, bizLogicType,
					entryType);
			RenameClassVisitor renameClassVisitor = new RenameClassVisitor(entityEventListener, implDomainType.getInternalName(),
					bizLogicType.getInternalName());
			cr.accept(renameClassVisitor, ClassReader.EXPAND_FRAMES);
			byte[] code = cw.toByteArray();
			ctx.defineClass(bizLogicType.getClassName(), code);
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
