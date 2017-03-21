package com.nebula.dropwizard.core;

import java.io.IOException;
import java.io.PrintWriter;

import org.axonframework.samples.bank.command.BankAccount;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class BankAccountTest {

//	@Test
//	public void testBankAccount() throws IOException {
//		ClassReader cr = new ClassReader(BankAccount.class.getName());
//		ClassVisitor typemaker = new TypeMaker(Opcodes.ASM5);
//		cr.accept(typemaker, ClassReader.SKIP_DEBUG);
//
//		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
//		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
//
//		System.out.println(typemaker.toString());
//	}

//	@Test
//	public void testPrintBankAccount() throws IOException {
//		ClassReader cr = new ClassReader(BankAccount.class.getName());
//		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
//		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
//	}
//
//	@Test
//	public void testPrintBankAccount2() throws IOException {
//		ClassReader cr = new ClassReader(BankAccount.class.getName());
//		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
//		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
//	}
//
	@Test
	public void testMakeDomain() throws IOException, InstantiationException, IllegalAccessException {
		ClassReader cr = new ClassReader(BankAccount.class.getName());
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		DomainMaker domainMaker = new DomainMaker(Opcodes.ASM5, traceClassVisitor);
		cr.accept(domainMaker, ClassReader.EXPAND_FRAMES);

		byte[] code = cw.toByteArray();

		MyClassLoader cl = new MyClassLoader();
		Class<?> clzDomain = new MyClassLoader().defineClass(BankAccount.class.getName(), code);
		cl.doResolveClass(clzDomain);
		
		Object o =  clzDomain.newInstance();
		

		System.out.println(domainMaker.toString());
	}

	static class MyClassLoader extends ClassLoader {
		public Class<?> defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}
		
		public void doResolveClass(Class<?> clz){
			super.resolveClass(clz);
		}
	}
}
