package com.nebula.dropwizard.core;

import java.io.IOException;
import java.io.PrintWriter;

import org.axonframework.samples.bank.command.BankAccountCommandHandler;
import org.axonframework.samples.bankcqrs.MyBankAccount;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class TodoTest {
	public static void main(String[] args) throws IOException {
		ClassReader cr = new ClassReader(BankAccountCommandHandler.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}
}
