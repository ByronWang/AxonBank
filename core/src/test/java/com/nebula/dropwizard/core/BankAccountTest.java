package com.nebula.dropwizard.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.samples.bank.command.BankAccountCommandHandler;
import org.axonframework.samples.bank.command.BankAccountCommandHandler2;
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

	// @Test
	// public void testBankAccount() throws IOException {
	// ClassReader cr = new ClassReader(BankAccount.class.getName());
	// ClassVisitor typemaker = new TypeMaker(Opcodes.ASM5);
	// cr.accept(typemaker, ClassReader.SKIP_DEBUG);
	//
	// ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new
	// PrintWriter(System.out));
	// cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	//
	// System.out.println(typemaker.toString());
	// }

	// @Test
	// public void testPrintBankAccount() throws IOException {
	// ClassReader cr = new ClassReader(BankAccount.class.getName());
	// ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new
	// PrintWriter(System.out));
	// cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	// }
	//
	// @Test
	// public void testPrintBankAccount2() throws IOException {
	// ClassReader cr = new ClassReader(BankAccount.class.getName());
	// ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new
	// PrintWriter(System.out));
	// cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	// }
	//

	// private void writeTo(File folder, String name, byte[] code) {
	// try {
	// FileOutputStream fileOutputStream = new FileOutputStream(new File(folder,
	// name + ".class"));
	//
	// fileOutputStream.write(code);
	// fileOutputStream.close();
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testMakeDomainTypeMaker() throws Exception {
		File root = new File("target/generated-auto-classes/");
		// File folder = new File(root,
		// BankAccount.class.getPackage().getName().replace('.', '/'));
		// if (!folder.exists()) {
		// folder.mkdirs();
		// }

		MyClassLoader classLoader = new MyClassLoader();

		String packageName = BankAccount.class.getPackage().getName();

		String handlerClassName = BankAccount.class.getPackage().getName() + "." + BankAccount.class.getSimpleName() + "CommandHandler";
		Type typeHandler = Type.getObjectType(handlerClassName.replace('.', '/'));
		Type typeDomain = Type.getType(BankAccount.class);

		ClassReader cr = new ClassReader(BankAccount.class.getName());
		CQRSDomainAnalyzer analyzer = new CQRSDomainAnalyzer(Opcodes.ASM5);
		cr.accept(analyzer, 0);
		System.out.println(analyzer.methods);

		// ClassReader cr = new ClassReader(BankAccount.class.getName());
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor traceClassVisitorToConsole = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		CQRSDomainBuilder cqrs = new CQRSDomainBuilder(Opcodes.ASM5, traceClassVisitorToConsole, analyzer.methods);
		cr.accept(cqrs, 0);

		byte[] code = cw.toByteArray();
		writeToWithPackage(root, BankAccount.class.getName(), code);

		for (Event event : cqrs.events) {
			if (event.superName==null) {
				byte[] eventCode = CQRSEventBuilder.dump(packageName, event);
				writeToWithPackage(root, cqrs.fullnameOf(event.simpleClassName), eventCode);
				classLoader.defineClass(cqrs.fullnameOf(event.simpleClassName), eventCode);
			}
		}
		for (Event event : cqrs.events) {
			if (event.superName!=null) {
				byte[] eventCode = CQRSEventAliasBuilder.dump(packageName, event);
				writeToWithPackage(root,cqrs.fullnameOf(event.simpleClassName), eventCode);
				classLoader.defineClass(cqrs.fullnameOf(event.simpleClassName), eventCode);
			}
		}
		for (Command command : cqrs.commands) {
			if (command.ctorMethod) {
				byte[] codeCommand = CQRSCommandBuilder.dump(packageName, command);
				writeToWithPackage(root, command.type.getClassName(), codeCommand);
				Class<?> clzCommand = classLoader.defineClass(command.type.getClassName(), codeCommand);
				classLoader.doResolveClass(clzCommand);

				Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
				byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCtorCallerBuilder.dump(typeDomain, typeHandler, command);
				writeToWithPackage(root, typeInvoke.getClassName(), codeCommandHandlerInvoke);
				Class<?> clzCommandHandlerInvoke = classLoader.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
				classLoader.doResolveClass(clzCommandHandlerInvoke);
			} else {
				byte[] codeCommand = CQRSCommandBuilder.dump(packageName, command);
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

		Class<BankAccount> clzDomain = classLoader.defineClass(BankAccount.class.getName(), code);
		classLoader.doResolveClass(clzDomain);

		try {
			Constructor<BankAccount> con31 = clzDomain.getDeclaredConstructor();
			con31.setAccessible(true);
			Object f31 = (Object) con31.newInstance();

			byte[] codeHandler = new CQRSCommandHandlerBuilder().dump(cqrs.commands, typeDomain, typeHandler);
			writeToWithPackage(root, handlerClassName, codeHandler);
			Class<?> clzHandle = classLoader.defineClass(handlerClassName, codeHandler);
			classLoader.doResolveClass(clzHandle);

			Repository<?> repos = Mockito.mock(Repository.class);
			EventBus eventBus = Mockito.mock(EventBus.class);

			Constructor<?> c = clzHandle.getConstructor(Repository.class, EventBus.class);
			c.newInstance(repos, eventBus);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(cqrs.toString());
	}

	// @Test
	// public void testMakeDomain() throws IOException, InstantiationException,
	// IllegalAccessException {
	// File folder = new File("target/generated-auto-classes/" +
	// BankAccount.class.getPackage().getName().replace('.', '/'));
	// if (!folder.exists()) {
	// folder.mkdirs();
	// }
	//
	// FileOutputStream fileOutputStream = new FileOutputStream(new File(folder,
	// BankAccount.class.getSimpleName() + ".class"));
	//
	// ClassReader cr = new ClassReader(BankAccount.class.getName());
	// ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS |
	// ClassWriter.COMPUTE_FRAMES);
	// TraceClassVisitor traceClassVisitorToConsole = new TraceClassVisitor(cw,
	// new ASMifier(), new PrintWriter(System.out));
	// DDOBuilder domainMaker = new DDOBuilder(Opcodes.ASM5,
	// traceClassVisitorToConsole);
	// cr.accept(domainMaker, 0);
	//
	// byte[] code = cw.toByteArray();
	//
	// fileOutputStream.write(code);;
	//
	// MyClassLoader cl = new MyClassLoader();
	// Class<BankAccount> clzDomain = new
	// MyClassLoader().defineClass(BankAccount.class.getName(), code);
	// cl.doResolveClass(clzDomain);
	//
	// try {
	// Constructor<BankAccount> con31 = clzDomain.getDeclaredConstructor();
	// con31.setAccessible(true);
	// Object f31 = (Object) con31.newInstance();
	// } catch (NoSuchMethodException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (SecurityException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InvocationTargetException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// System.out.println(domainMaker.toString());
	// }

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
