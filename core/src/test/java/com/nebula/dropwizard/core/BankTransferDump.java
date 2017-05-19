package com.nebula.dropwizard.core;

import java.util.*;
import org.objectweb.asm.*;

public class BankTransferDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/nebula/dropwizard/core/BankTransfer", null, "java/lang/Object", null);

		cw.visitSource("BankTransfer.java", null);

		{
			av0 = cw.visitAnnotation("Lorg/axonframework/spring/stereotype/Aggregate;", true);
			av0.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "BankTransferId", "Ljava/lang/String;", null, null);
			{
				av0 = fv.visitAnnotation("Lorg/axonframework/commandhandling/model/AggregateIdentifier;", true);
				av0.visitEnd();
			}
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "sourceBankAccountId", "Ljava/lang/String;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "destinationBankAccountId", "Ljava/lang/String;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "amount", "J", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "status", "Lcom/nebula/dropwizard/core/Status;", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PROTECTED, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(44, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(45, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/BankTransfer;", null, l0, l2, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/axonframework/samples/bank/api/banktransfer/BankTransferCreateCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(48, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(49, l1);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreatedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreateCommand", "getBankTransferId",
			        "()Ljava/lang/String;", false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreateCommand", "getSourceBankAccountId",
			        "()Ljava/lang/String;", false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreateCommand", "getDestinationBankAccountId",
			        "()Ljava/lang/String;", false);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(50, l2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreateCommand", "getAmount", "()J", false);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(49, l3);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreatedEvent", "<init>",
			        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
			        "(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLineNumber(51, l4);
			mv.visitInsn(RETURN);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/BankTransfer;", null, l0, l5, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/banktransfer/BankTransferCreateCommand;", null, l0, l5, 1);
			mv.visitMaxs(7, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "on", "(Lorg/axonframework/samples/bank/api/banktransfer/BankTransferCreatedEvent;)V", null,
			        new String[] { "java/lang/Exception" });
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/eventhandling/EventHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(55, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreatedEvent", "getBankTransferId",
			        "()Ljava/lang/String;", false);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/BankTransfer", "BankTransferId", "Ljava/lang/String;");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(56, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreatedEvent", "getSourceBankAccountId",
			        "()Ljava/lang/String;", false);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/BankTransfer", "sourceBankAccountId", "Ljava/lang/String;");
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(57, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreatedEvent", "getDestinationBankAccountId",
			        "()Ljava/lang/String;", false);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/BankTransfer", "destinationBankAccountId", "Ljava/lang/String;");
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(58, l3);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCreatedEvent", "getAmount", "()J", false);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/BankTransfer", "amount", "J");
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLineNumber(59, l4);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETSTATIC, "com/nebula/dropwizard/core/Status", "STARTED", "Lcom/nebula/dropwizard/core/Status;");
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/BankTransfer", "status", "Lcom/nebula/dropwizard/core/Status;");
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLineNumber(60, l5);
			mv.visitInsn(RETURN);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/BankTransfer;", null, l0, l6, 0);
			mv.visitLocalVariable("event", "Lorg/axonframework/samples/bank/api/banktransfer/BankTransferCreatedEvent;", null, l0, l6, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/banktransfer/BankTransferMarkCompletedCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(64, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/banktransfer/BankTransferCompletedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferMarkCompletedCommand", "getBankTransferId",
			        "()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferCompletedEvent", "<init>", "(Ljava/lang/String;)V",
			        false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
			        "(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(65, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/BankTransfer;", null, l0, l2, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/banktransfer/BankTransferMarkCompletedCommand;", null, l0, l2, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "on", "(Lorg/axonframework/samples/bank/api/banktransfer/BankTransferCompletedEvent;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/eventhandling/EventHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(69, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETSTATIC, "com/nebula/dropwizard/core/Status", "COMPLETED", "Lcom/nebula/dropwizard/core/Status;");
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/BankTransfer", "status", "Lcom/nebula/dropwizard/core/Status;");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(70, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/BankTransfer;", null, l0, l2, 0);
			mv.visitLocalVariable("event", "Lorg/axonframework/samples/bank/api/banktransfer/BankTransferCompletedEvent;", null, l0, l2, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/banktransfer/BankTransferMarkFailedCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(74, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/banktransfer/BankTransferFailedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferMarkFailedCommand", "getBankTransferId",
			        "()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/banktransfer/BankTransferFailedEvent", "<init>", "(Ljava/lang/String;)V",
			        false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
			        "(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(75, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/BankTransfer;", null, l0, l2, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/banktransfer/BankTransferMarkFailedCommand;", null, l0, l2, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "on", "(Lorg/axonframework/samples/bank/api/banktransfer/BankTransferFailedEvent;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/eventhandling/EventHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(79, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETSTATIC, "com/nebula/dropwizard/core/Status", "FAILED", "Lcom/nebula/dropwizard/core/Status;");
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/BankTransfer", "status", "Lcom/nebula/dropwizard/core/Status;");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(80, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/BankTransfer;", null, l0, l2, 0);
			mv.visitLocalVariable("event", "Lorg/axonframework/samples/bank/api/banktransfer/BankTransferFailedEvent;", null, l0, l2, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
