package com.nebula.cqrs.axon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.asm.CQRSCommandBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerCallerBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerCtorCallerBuilder;
import com.nebula.cqrs.axon.asm.CQRSDomainAnalyzer;
import com.nebula.cqrs.axon.asm.CQRSDomainBuilder;
import com.nebula.cqrs.axon.asm.CQRSEventAliasBuilder;
import com.nebula.cqrs.axon.asm.CQRSEventRealBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.Event;

public class CQRSBuilder implements CQRSContext {
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
				Type typeHandler = Type.getObjectType(domainDefinition.type.getInternalName() + "CommandHandler");

				for (Command command : domainDefinition.commands) {
					if (command.ctorMethod) {
						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCtorCallerBuilder.dump(domainDefinition.type, typeHandler, command);
						ctx.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
					} else {
						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCallerBuilder.dump(domainDefinition.type, typeHandler, command);
						ctx.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
					}
				}

				byte[] codeHandler = new CQRSCommandHandlerBuilder().dump(domainDefinition.commands, domainDefinition.type, typeHandler);
				ctx.defineClass(typeHandler.getClassName(), codeHandler);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return;
		}
	}

	public void makeDomainCQRSHelper(String srcDomainClassName) {

		try {
			final DomainDefinition domainDefinition;
			byte[] binaryRepresentationAfterRenameToImpl;
			{
				String srcDomainName = srcDomainClassName.substring(srcDomainClassName.lastIndexOf('.') + 1);

				String domainImplClassName = srcDomainClassName + "Impl";
				Type srcDomainType = Type.getObjectType(srcDomainClassName.replace('.', '/'));
				Type implDomainType = Type.getObjectType(domainImplClassName.replace('.', '/'));

				domainDefinition = new DomainDefinition(srcDomainName, implDomainType);

				ClassReader classReaderSource = new ClassReader(srcDomainType.getClassName());
				ClassWriter classWriterImplDomainType = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				RenameClassVisitor renameClassVisitor = new RenameClassVisitor(classWriterImplDomainType, srcDomainType.getInternalName(),
						implDomainType.getInternalName());
				classReaderSource.accept(renameClassVisitor, 0);
				binaryRepresentationAfterRenameToImpl = classWriterImplDomainType.toByteArray();
			}
			CQRSDomainBuilder cqrs;
			{
				ClassReader cr = new ClassReader(binaryRepresentationAfterRenameToImpl);
				CQRSDomainAnalyzer analyzer = new CQRSDomainAnalyzer(Opcodes.ASM5, domainDefinition);
				cr.accept(analyzer, 0);
				analyzer.finished();

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				cqrs = new CQRSDomainBuilder(Opcodes.ASM5, cw, domainDefinition);
				cr.accept(cqrs, 0);
				cqrs.finished();

				byte[] domainRepresentationAfterMakeCqrs = cw.toByteArray();
				classLoader.define(domainDefinition.type.getClassName(), domainRepresentationAfterMakeCqrs);
			}
			{
				for (Event event : domainDefinition.events) {
					if (event.realEvent == null) {
						byte[] eventCode = CQRSEventRealBuilder.dump(event);
						classLoader.define(cqrs.fullnameOf(event.simpleClassName), eventCode);
					}
				}
				for (Event event : domainDefinition.events) {
					if (event.realEvent != null) {
						byte[] eventCode = CQRSEventAliasBuilder.dump(event);
						classLoader.define(cqrs.fullnameOf(event.simpleClassName), eventCode);
					}
				}
				for (Command command : domainDefinition.commands) {
					if (command.ctorMethod) {
						byte[] codeCommand = CQRSCommandBuilder.dump(command);
						classLoader.define(command.type.getClassName(), codeCommand);
					} else {
						byte[] codeCommand = CQRSCommandBuilder.dump(command);
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
