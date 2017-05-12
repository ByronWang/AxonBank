package com.nebula.dropwizard.core;

import java.util.*;
import org.objectweb.asm.*;

public class MyBankAccountBankTransferSagaDump2 implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/nebula/dropwizard/core/MyBankAccountBankTransferSaga", null, "java/lang/Object", null);

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
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(5, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/MyBankAccountBankTransferSaga;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "onsourceDebit", "(Lcom/nebula/dropwizard/core/MyBankAccountSourceDebitSucceedEvent;)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(14, l0);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/MyBankAccountBankTransferSaga;", null, l0, l1, 0);
			mv.visitLocalVariable("paramMyBankAccountSourceDebitSucceedEvent", "Lcom/nebula/dropwizard/core/MyBankAccountSourceDebitSucceedEvent;", null, l0,
			        l1, 1);
			mv.visitMaxs(0, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
