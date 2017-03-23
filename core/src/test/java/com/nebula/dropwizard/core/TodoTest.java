package com.nebula.dropwizard.core;

import java.io.IOException;
import java.io.PrintWriter;

import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyDepositCommand;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyDepositedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneySubtractedEvent;
import org.axonframework.samples.bank.command.BankAccountCommandHandler;
import org.axonframework.samples.bank.command.BankAccountCommandHandler2;
import org.axonframework.samples.bank.simple.instanceCommand.BankAccount;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class TodoTest {

//	@Test
//	public void testTodo() throws IOException {
//		ClassReader cr = new ClassReader(Todo.class.getName());
//		ClassVisitor typemaker = new CQRSBuilder(Opcodes.ASM5);
//		cr.accept(typemaker, ClassReader.SKIP_DEBUG);
//
//		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
//		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
//
//		System.out.println(typemaker.toString());
//	}
	@Test
	public void testPrintTodo() throws IOException {
		ClassReader cr = new ClassReader(Todo.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}

	@Test
	public void testPrintBankAccount() throws IOException {
		ClassReader cr = new ClassReader(BankAccount.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}
	
	
	@Test
	public void testPrintBankAccountCommandHandler() throws IOException {
		ClassReader cr = new ClassReader(BankAccountCommandHandler2.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}
	
	@Test
	public void testPrintBankAccountCommandHandler2() throws IOException {
		ClassReader cr = new ClassReader(BankAccountCommandHandler2.class.getName()+"$1");
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}
	
	
	
	@Test
	public void testPrintcommand_BankAccount() throws IOException {
		ClassReader cr = new ClassReader(org.axonframework.samples.bank.command.BankAccount.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}
	
	
	@Test
	public void testPrintBankAccountMoneyDepositCommand() throws IOException {
		ClassReader cr = new ClassReader(BankAccountMoneyDepositCommand.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}

	@Test
	public void BankAccountMoneyDepositedEvent() throws IOException {
		ClassReader cr = new ClassReader(BankAccountMoneyDepositedEvent.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}
	
	@Test
	public void testPrintBankAccountMoneySubtractedEvent() throws IOException {
		ClassReader cr = new ClassReader(BankAccountMoneySubtractedEvent.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}
	
	@Test
	public void testPrintBankAccountMoneyDepositedEvent() throws IOException {
		ClassReader cr = new ClassReader(BankAccountMoneyDepositedEvent.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}


	@Test
	public void testPrintTodo2() throws IOException {
		ClassReader cr = new ClassReader(Todo2.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}

	@Test
	public void testMakeDomain() throws IOException, InstantiationException, IllegalAccessException {
		ClassReader cr = new ClassReader(Todo.class.getName());
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		DDOBuilder domainMaker = new DDOBuilder(Opcodes.ASM5, traceClassVisitor);
		cr.accept(domainMaker, ClassReader.EXPAND_FRAMES);

		byte[] code = cw.toByteArray();

		MyClassLoader cl = new MyClassLoader();
		Class<?> clzDomain = new MyClassLoader().defineClass(Todo.class.getName(), code);
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
