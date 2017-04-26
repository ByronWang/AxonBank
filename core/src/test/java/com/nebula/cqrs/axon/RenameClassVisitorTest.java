package com.nebula.cqrs.axon;

import java.io.IOException;
import java.io.PrintWriter;

import org.axonframework.samples.bank.cqrs.MyBankAccount;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class RenameClassVisitorTest {

	@Test
	public void testRenameClassVisitor() throws IOException {
		ClassReader cr = new ClassReader(MyBankAccount.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		RenameClassVisitor renameClassVisitor = new RenameClassVisitor(visitor,MyBankAccount.class.getName().replace('.', '/'), MyBankAccount.class.getName().replace('.', '/') + "Impl");
		cr.accept(renameClassVisitor, ClassReader.EXPAND_FRAMES);
	}
}
