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

import com.nebula.cqrs.axon.asm.CQRSCommandBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerCallerBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerCtorCallerBuilder;
import com.nebula.cqrs.axon.asm.CQRSDomainAnalyzer;
import com.nebula.cqrs.axon.asm.CQRSDomainBuilder;
import com.nebula.cqrs.axon.asm.CQRSEventAliasBuilder;
import com.nebula.cqrs.axon.asm.CQRSEventRealBuilder;
import com.nebula.cqrs.axon.asm.ClassUtils;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.Event;

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
			byte[] binaryRepresentationAfterRenameToImpl;
			Type implDomainType;
			{
				String srcDomainName = srcDomainClassName.substring(srcDomainClassName.lastIndexOf('.') + 1);

				String domainImplClassName = srcDomainClassName + "Impl";
				Type srcDomainType = Type.getObjectType(srcDomainClassName.replace('.', '/'));
				implDomainType = Type.getObjectType(domainImplClassName.replace('.', '/'));

				domainDefinition = new DomainDefinition(srcDomainName, srcDomainType, implDomainType);

				ClassReader classReaderSource = new ClassReader(srcDomainType.getClassName());
				ClassWriter classWriterImplDomainType = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				RenameClassVisitor renameClassVisitor = new RenameClassVisitor(classWriterImplDomainType, srcDomainType.getInternalName(),
						implDomainType.getInternalName());
				classReaderSource.accept(renameClassVisitor, 0);
				binaryRepresentationAfterRenameToImpl = classWriterImplDomainType.toByteArray();

				LOGGER.debug("Rename domain class from {} to impl class [{}]", srcDomainType.getClassName(), implDomainType.getClassName());
			}
			{
				ClassReader cr = new ClassReader(binaryRepresentationAfterRenameToImpl);
				{
					CQRSDomainAnalyzer analyzer = new CQRSDomainAnalyzer();
					cr.accept(analyzer, 0);
					domainDefinition.menthods = analyzer.getMethods();
				}

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				{
					CQRSDomainBuilder cqrs = new CQRSDomainBuilder(Opcodes.ASM5, cw, domainDefinition);
					cr.accept(cqrs, 0);
				}

				byte[] domainRepresentationAfterMakeCqrs = cw.toByteArray();
				LOGGER.debug("Made cqrs domain class [{}]", implDomainType.getClassName());
				classLoader.define(implDomainType.getClassName(), domainRepresentationAfterMakeCqrs);
			}
			{
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

			listeners.forEach(l -> l.define(CQRSBuilder.this, domainDefinition));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return this.classLoader.loadClass(name);
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> defineClass(String name, byte[] b) {
		return (Class<T>) this.classLoader.define(name, b);
	}

}
