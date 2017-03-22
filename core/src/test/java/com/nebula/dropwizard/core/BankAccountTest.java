package com.nebula.dropwizard.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.axonframework.samples.bank.simple.instanceCommand.BankAccount;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import com.nebula.dropwizard.core.CQRSBuilder.Command;
import com.nebula.dropwizard.core.CQRSBuilder.Event;
import com.nebula.dropwizard.core.CQRSBuilder.Field;

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

	private void writeTo(File folder, String name, byte[] code) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, name + ".class"));

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
	public void testMakeDomainTypeMaker() throws IOException, InstantiationException, IllegalAccessException {
		File folder = new File("target/generated-auto-classes/" + BankAccount.class.getPackage().getName().replace('.', '/'));
		if (!folder.exists()) {
			folder.mkdirs();
		}

		String packageName = BankAccount.class.getPackage().getName();

		ClassReader cr = new ClassReader(BankAccount.class.getName());
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor traceClassVisitorToConsole = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		CQRSBuilder domainMaker = new CQRSBuilder(Opcodes.ASM5, traceClassVisitorToConsole);
		cr.accept(domainMaker, 0);

		byte[] code = cw.toByteArray();
		writeTo(folder, BankAccount.class.getSimpleName(), code);
			
		for (Event event : domainMaker.events) {
			if (event.superName != null) {				
				byte[] eventCode = AliasEventBuilder.dump(packageName, event);
				writeTo(folder, event.name, eventCode);
			} else {
				byte[] eventCode = EventBuilder.dump(packageName, event);
				writeTo(folder, event.name, eventCode);
			}
		}
		for (Command command : domainMaker.commands) {
			byte[] eventCode = CommandBuilder.dump(packageName, command);
			writeTo(folder, command.name, eventCode);
		}

		MyClassLoader cl = new MyClassLoader();
		Class<BankAccount> clzDomain = new MyClassLoader().defineClass(BankAccount.class.getName(), code);
		cl.doResolveClass(clzDomain);

		try {
			Constructor<BankAccount> con31 = clzDomain.getDeclaredConstructor();
			con31.setAccessible(true);
			Object f31 = (Object) con31.newInstance();
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

		System.out.println(domainMaker.toString());
	}

	@Test
	public void testMakeDomain() throws IOException, InstantiationException, IllegalAccessException {
		File folder = new File("target/generated-auto-classes/" + BankAccount.class.getPackage().getName().replace('.', '/'));
		if (!folder.exists()) {
			folder.mkdirs();
		}

		FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, BankAccount.class.getSimpleName() + ".class"));

		ClassReader cr = new ClassReader(BankAccount.class.getName());
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor traceClassVisitorToConsole = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		DDOBuilder domainMaker = new DDOBuilder(Opcodes.ASM5, traceClassVisitorToConsole);
		cr.accept(domainMaker, 0);

		byte[] code = cw.toByteArray();

		fileOutputStream.write(code);;

		MyClassLoader cl = new MyClassLoader();
		Class<BankAccount> clzDomain = new MyClassLoader().defineClass(BankAccount.class.getName(), code);
		cl.doResolveClass(clzDomain);

		try {
			Constructor<BankAccount> con31 = clzDomain.getDeclaredConstructor();
			con31.setAccessible(true);
			Object f31 = (Object) con31.newInstance();
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

		System.out.println(domainMaker.toString());
	}

	static class MyClassLoader extends ClassLoader {
		@SuppressWarnings("unchecked")
		public <T> Class<T> defineClass(String name, byte[] b) {
			return (Class<T>) defineClass(name, b, 0, b.length);
		}

		public void doResolveClass(Class<?> clz) {
			super.resolveClass(clz);
		}
	}
}
