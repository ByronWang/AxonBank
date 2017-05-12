package com.nebula.dropwizard.core;

import java.util.*;
import org.objectweb.asm.*;

public class MyBankAccountBankTransferSagaDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/nebula/tinyasm/ana/generatedsources/MyBankAccountBankTransferSaga", null, "java/lang/Object", null);

		cw.visitSource("MyBankAccountBankTransferSaga.java", null);

		{
			fv = cw.visitField(ACC_PRIVATE, "commandBus", "Lorg/axonframework/commandhandling/CommandBus;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "sourceAxonBankAccountId", "Ljava/lang/String;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "destinationAxonBankAccountId", "Ljava/lang/String;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "amount", "J", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "onsourceDebit", "(Lcom/nebula/tinyasm/ana/generatedsources/MyBankAccountSourceDebitSucceedEvent;)V", null, null);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}