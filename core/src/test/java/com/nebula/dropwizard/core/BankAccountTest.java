package com.nebula.dropwizard.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.samples.bank.simple.instanceCommand.BankAccount;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import com.nebula.dropwizard.core.CQRSDomainBuilder.Command;
import com.nebula.dropwizard.core.CQRSDomainBuilder.Event;

public class BankAccountTest {

	private void writeToWithPackage(File root, String name, byte[] code) {
		try {
			int i = name.lastIndexOf(".");
			String packageName = name.substring(0, i).replace('.', '/');
			String simpleCassName = name.substring(i + 1);

			File folder = new File(root, packageName);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, simpleCassName + ".class"));

			fileOutputStream.write(code);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMakeDomainTypeMaker() throws Exception {
		File root = new File("target/generated-auto-classes/");

		MyClassLoader classLoader = new MyClassLoader();
		Class<?> clzHandle = null;
		{
			String handlerClassName = BankAccount.class.getPackage().getName() + "." + BankAccount.class.getSimpleName() + "CommandHandler";
			Type typeHandler = Type.getObjectType(handlerClassName.replace('.', '/'));
			Type typeDomain = Type.getType(BankAccount.class);

			ClassReader cr = new ClassReader(BankAccount.class.getName());
			CQRSDomainAnalyzer analyzer = new CQRSDomainAnalyzer(Opcodes.ASM5);
			cr.accept(analyzer, 0);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			TraceClassVisitor traceClassVisitorToConsole = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
			CQRSDomainBuilder cqrs = new CQRSDomainBuilder(Opcodes.ASM5, traceClassVisitorToConsole, analyzer.methods);
			cr.accept(cqrs, 0);

			System.out.println(cqrs.toString());

			byte[] code = cw.toByteArray();
			writeToWithPackage(root, BankAccount.class.getName(), code);

			{
				for (Event event : cqrs.events) {
					if (event.realEvent == null) {
						byte[] eventCode = CQRSEventBuilder.dump(event);
						writeToWithPackage(root, cqrs.fullnameOf(event.simpleClassName), eventCode);
						classLoader.defineClass(cqrs.fullnameOf(event.simpleClassName), eventCode);
					}
				}
				for (Event event : cqrs.events) {
					if (event.realEvent != null) {
						byte[] eventCode = CQRSEventAliasBuilder.dump(event);
						writeToWithPackage(root, cqrs.fullnameOf(event.simpleClassName), eventCode);
						classLoader.defineClass(cqrs.fullnameOf(event.simpleClassName), eventCode);
					}
				}
				for (Command command : cqrs.commands) {
					if (command.ctorMethod) {
						byte[] codeCommand = CQRSCommandBuilder.dump(command);
						writeToWithPackage(root, command.type.getClassName(), codeCommand);
						Class<?> clzCommand = classLoader.defineClass(command.type.getClassName(), codeCommand);
						classLoader.doResolveClass(clzCommand);

						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCtorCallerBuilder.dump(typeDomain, typeHandler, command);
						writeToWithPackage(root, typeInvoke.getClassName(), codeCommandHandlerInvoke);
						Class<?> clzCommandHandlerInvoke = classLoader.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
						classLoader.doResolveClass(clzCommandHandlerInvoke);
					} else {
						byte[] codeCommand = CQRSCommandBuilder.dump(command);
						writeToWithPackage(root, command.type.getClassName(), codeCommand);
						Class<?> clzCommand = classLoader.defineClass(command.type.getClassName(), codeCommand);
						classLoader.doResolveClass(clzCommand);

						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCallerBuilder.dump(typeDomain, typeHandler, command);
						writeToWithPackage(root, typeInvoke.getClassName(), codeCommandHandlerInvoke);
						Class<?> clzCommandHandlerInvoke = classLoader.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
						classLoader.doResolveClass(clzCommandHandlerInvoke);
					}
				}
			}

			Class<BankAccount> clzDomain = classLoader.defineClass(BankAccount.class.getName(), code);
			classLoader.doResolveClass(clzDomain);

			Constructor<BankAccount> con31 = clzDomain.getDeclaredConstructor();
			con31.setAccessible(true);
			con31.newInstance();

			byte[] codeHandler = new CQRSCommandHandlerBuilder().dump(cqrs.commands, typeDomain, typeHandler);
			writeToWithPackage(root, handlerClassName, codeHandler);
			clzHandle = classLoader.defineClass(handlerClassName, codeHandler);
			classLoader.doResolveClass(clzHandle);
		}

		Repository<?> repos = Mockito.mock(Repository.class);
		EventBus eventBus = Mockito.mock(EventBus.class);

		Constructor<?> c = clzHandle.getConstructor(Repository.class, EventBus.class);
		c.newInstance(repos, eventBus);

	}

	static class MyClassLoader extends ClassLoader {
		@SuppressWarnings("unchecked")
		public <T> Class<T> defineClass(String name, byte[] b) {
			return (Class<T>) defineClass(name, b, 0, b.length);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			System.out.println("loadClass >>>   " + name);
			return super.loadClass(name);
		}

		public void doResolveClass(Class<?> clz) {
			super.resolveClass(clz);
		}

	}
}
