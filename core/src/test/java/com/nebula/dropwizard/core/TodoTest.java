package com.nebula.dropwizard.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import com.nebula.tinyasm.ana.MyBankAccount;

public class TodoTest {
	public static void main(String[] args) throws IOException {
//		ClassReader cr = new ClassReader(new FileInputStream(
//		        "D:\\CQRS\\AxonBank\\core\\target\\classes\\org\\axonframework\\samples\\bankcqrs\\generatedsources\\MyBankAccountBankTransferSaga.class"));
		ClassReader cr = new ClassReader(MyBankAccount.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}
}
