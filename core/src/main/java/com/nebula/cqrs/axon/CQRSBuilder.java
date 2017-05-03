package com.nebula.cqrs.axon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nebula.cqrs.axon.asm.AnalyzeFieldClassVisitor;
import com.nebula.cqrs.axon.asm.CQRSCommandBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerCallerBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerCtorCallerBuilder;
import com.nebula.cqrs.axon.asm.CQRSDomainBuilder;
import com.nebula.cqrs.axon.asm.CQRSEventAliasBuilder;
import com.nebula.cqrs.axon.asm.CQRSEventRealBuilder;
import com.nebula.cqrs.axon.asm.RemoveCqrsAnnotationClassVisitor;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.core.asm.AnalyzeMethodParamsClassVisitor;
import com.nebula.cqrs.core.asm.Field;
import com.nebula.cqrs.core.asm.RenameClassVisitor;

public class CQRSBuilder implements CQRSContext {
	private final static Logger LOGGER = LoggerFactory.getLogger(CQRSBuilder.class);

	final MyClassLoader classLoader = new MyClassLoader();

	public CQRSBuilder() {
		super();
		// Thread.currentThread().setContextClassLoader(classLoader);
		listeners.add(new CommandHandlerListener());
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	final List<DomainListener> listeners = new ArrayList<>();

	public void add(DomainListener domainListener) {
		this.listeners.add(domainListener);
	}

	static class CommandHandlerListener implements DomainListener {
		@Override
		public void define(CQRSContext ctx, DomainDefinition domainDefinition) {
			try {
				Type typeHandler = domainDefinition.typeOf("CommandHandler");

				for (Command command : domainDefinition.commands) {
					if (command.ctorMethod) {
						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.commandName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCtorCallerBuilder.dump(typeInvoke, domainDefinition.implDomainType, typeHandler,
								command);
						LOGGER.debug("Create command inner class [{}]", typeInvoke.getClassName());
						ctx.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
					} else {
						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.commandName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCallerBuilder.dump(typeInvoke, domainDefinition.implDomainType, typeHandler,
								command);
						LOGGER.debug("Create command inner class [{}]", typeInvoke.getClassName());
						ctx.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
					}
				}

				byte[] codeHandler = new CQRSCommandHandlerBuilder().dump(domainDefinition.commands, domainDefinition.implDomainType, typeHandler);
				LOGGER.debug("Create command handler [{}]", typeHandler.getClassName());
				ctx.defineClass(typeHandler.getClassName(), codeHandler);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return;
		}
	}

	public void makeDomainCQRSHelper(String srcDomainClassName) {
		LOGGER.debug("Start make domain [{}]", srcDomainClassName);

		try {
			final DomainDefinition domainDefinition;
			Type srcDomainType;
			Type implDomainType;
			String domainName = srcDomainClassName.substring(srcDomainClassName.lastIndexOf('.') + 1);
			{
				String domainImplClassName = srcDomainClassName + "Impl";
				srcDomainType = Type.getObjectType(srcDomainClassName.replace('.', '/'));
				implDomainType = Type.getObjectType(domainImplClassName.replace('.', '/'));
			}

			domainDefinition = analyzeDomain(domainName, srcDomainType, implDomainType);

			makeDomainImpl(domainDefinition, srcDomainType, implDomainType);

			listeners.forEach(l -> l.define(CQRSBuilder.this, domainDefinition));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void makeDomainImpl(final DomainDefinition domainDefinition, Type srcDomainType, Type implDomainType) throws IOException {

		ClassReader cr = new ClassReader(srcDomainType.getClassName());
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

		CQRSDomainBuilder cvMakeDomainImpl = new CQRSDomainBuilder(cw, domainDefinition); 
		RenameClassVisitor cvRename = new RenameClassVisitor(cvMakeDomainImpl, srcDomainType.getInternalName(), implDomainType.getInternalName());

		RemoveCqrsAnnotationClassVisitor removeCqrsAnnotation = new RemoveCqrsAnnotationClassVisitor(cvRename);
		cr.accept(removeCqrsAnnotation, 0);

		LOGGER.debug("Rename domain class from {} to impl class [{}]", srcDomainType.getClassName(), implDomainType.getClassName());
		LOGGER.debug("Made cqrs domain class [{}]", implDomainType.getClassName());

		byte[] code = cw.toByteArray();

		classLoader.define(implDomainType.getClassName(), code);

		for (Event event : domainDefinition.events) {
			if (event.realEvent == null) {
				byte[] eventCode = CQRSEventRealBuilder.dump(event);
				LOGGER.debug("Create event [{}]", event.type.getClassName());
				classLoader.define(event.type.getClassName(), eventCode);
			}
		}
		for (Event event : domainDefinition.events) {
			if (event.realEvent != null) {
				byte[] eventCode = CQRSEventAliasBuilder.dump(event);
				LOGGER.debug("Create command handler [{}]", event.type.getClassName());
				classLoader.define(event.type.getClassName(), eventCode);
			}
		}
		for (Command command : domainDefinition.commands) {
			if (command.ctorMethod) {
				byte[] codeCommand = CQRSCommandBuilder.dump(command);
				LOGGER.debug("Create command handler [{}]", command.type.getClassName());
				classLoader.define(command.type.getClassName(), codeCommand);
			} else {
				byte[] codeCommand = CQRSCommandBuilder.dump(command);
				LOGGER.debug("Create command handler [{}]", command.type.getClassName());
				classLoader.define(command.type.getClassName(), codeCommand);
			}
		}
	}

	private DomainDefinition analyzeDomain(String srcDomainName, Type srcDomainType, Type implDomainType) throws IOException {
		final DomainDefinition domainDefinition;
		domainDefinition = new DomainDefinition(srcDomainName, srcDomainType, implDomainType);
		ClassReader cr = new ClassReader(srcDomainType.getClassName());
		AnalyzeMethodParamsClassVisitor analyzeMethodParamsClassVisitor = new AnalyzeMethodParamsClassVisitor();
		AnalyzeFieldClassVisitor analyzeFieldClassVisitor = new AnalyzeFieldClassVisitor(analyzeMethodParamsClassVisitor);
		cr.accept(analyzeFieldClassVisitor, 0);
		domainDefinition.menthods = analyzeMethodParamsClassVisitor.getMethods();
		domainDefinition.fields = analyzeFieldClassVisitor.finished().toArray(new Field[0]);
		for (Field field : domainDefinition.fields) {
			if (field.identifier) {
				domainDefinition.identifierField = field;
			}
		}
		return domainDefinition;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return this.classLoader.loadClass(name);
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> defineClass(String name, byte[] b) {
		return (Class<T>) this.classLoader.define(name, b);
	}

}
