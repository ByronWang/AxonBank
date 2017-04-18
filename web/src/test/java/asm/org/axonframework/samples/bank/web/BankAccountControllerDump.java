package asm.org.axonframework.samples.bank.web;

import java.util.*;
import org.objectweb.asm.*;

public class BankAccountControllerDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "org/axonframework/samples/bank/web/BankAccountController", null, "java/lang/Object", null);

		cw.visitSource("BankAccountController.java", null);

		{
			av0 = cw.visitAnnotation("Lorg/springframework/stereotype/Controller;", true);
			av0.visitEnd();
		}
		{
			av0 = cw.visitAnnotation("Lorg/springframework/messaging/handler/annotation/MessageMapping;", true);
			{
				AnnotationVisitor av1 = av0.visitArray("value");
				av1.visit(null, "/bank-accounts");
				av1.visitEnd();
			}
			av0.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "commandBus", "Lorg/axonframework/commandhandling/CommandBus;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "bankAccountRepository", "Lorg/axonframework/samples/bank/query/bankaccount/BankAccountRepository;", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>",
					"(Lorg/axonframework/commandhandling/CommandBus;Lorg/axonframework/samples/bank/query/bankaccount/BankAccountRepository;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/springframework/beans/factory/annotation/Autowired;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(45, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(47, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/web/BankAccountController", "commandBus",
					"Lorg/axonframework/commandhandling/CommandBus;");
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(48, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/web/BankAccountController", "bankAccountRepository",
					"Lorg/axonframework/samples/bank/query/bankaccount/BankAccountRepository;");
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(49, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/web/BankAccountController;", null, l0, l4, 0);
			mv.visitLocalVariable("commandBus", "Lorg/axonframework/commandhandling/CommandBus;", null, l0, l4, 1);
			mv.visitLocalVariable("bankAccountRepository", "Lorg/axonframework/samples/bank/query/bankaccount/BankAccountRepository;", null, l0, l4, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "all", "()Ljava/lang/Iterable;",
					"()Ljava/lang/Iterable<Lorg/axonframework/samples/bank/query/bankaccount/BankAccountEntry;>;", null);
			{
				av0 = mv.visitAnnotation("Lorg/springframework/messaging/simp/annotation/SubscribeMapping;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(53, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/web/BankAccountController", "bankAccountRepository",
					"Lorg/axonframework/samples/bank/query/bankaccount/BankAccountRepository;");
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/samples/bank/query/bankaccount/BankAccountRepository", "findAllByOrderByIdAsc",
					"()Ljava/lang/Iterable;", true);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/web/BankAccountController;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/String;)Lorg/axonframework/samples/bank/query/bankaccount/BankAccountEntry;", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/springframework/messaging/simp/annotation/SubscribeMapping;", true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					av1.visit(null, "/{id}");
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			{
				av0 = mv.visitParameterAnnotation(0, "Lorg/springframework/messaging/handler/annotation/DestinationVariable;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(58, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/web/BankAccountController", "bankAccountRepository",
					"Lorg/axonframework/samples/bank/query/bankaccount/BankAccountRepository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/samples/bank/query/bankaccount/BankAccountRepository", "findOne",
					"(Ljava/io/Serializable;)Ljava/lang/Object;", true);
			mv.visitTypeInsn(CHECKCAST, "org/axonframework/samples/bank/query/bankaccount/BankAccountEntry");
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/web/BankAccountController;", null, l0, l1, 0);
			mv.visitLocalVariable("id", "Ljava/lang/String;", null, l0, l1, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "create", "(Lorg/axonframework/samples/bank/web/dto/BankAccountDto;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/springframework/messaging/handler/annotation/MessageMapping;", true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					av1.visit(null, "/create");
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(63, l0);
			mv.visitMethodInsn(INVOKESTATIC, "java/util/UUID", "randomUUID", "()Ljava/util/UUID;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/UUID", "toString", "()Ljava/lang/String;", false);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(64, l1);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/web/dto/BankAccountDto", "getOverdraftLimit", "()J", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand", "<init>", "(Ljava/lang/String;J)V",
					false);
			mv.visitVarInsn(ASTORE, 3);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(65, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/web/BankAccountController", "commandBus",
					"Lorg/axonframework/commandhandling/CommandBus;");
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/GenericCommandMessage", "asCommandMessage",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/CommandMessage;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/CommandBus", "dispatch",
					"(Lorg/axonframework/commandhandling/CommandMessage;)V", true);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(66, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/web/BankAccountController;", null, l0, l4, 0);
			mv.visitLocalVariable("bankAccountDto", "Lorg/axonframework/samples/bank/web/dto/BankAccountDto;", null, l0, l4, 1);
			mv.visitLocalVariable("id", "Ljava/lang/String;", null, l1, l4, 2);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;", null, l2, l4, 3);
			mv.visitMaxs(5, 4);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "withdraw", "(Lorg/axonframework/samples/bank/web/dto/WithdrawalDto;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/springframework/messaging/handler/annotation/MessageMapping;", true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					av1.visit(null, "/withdraw");
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(70, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/web/dto/WithdrawalDto", "getBankAccountId", "()Ljava/lang/String;", false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/web/dto/WithdrawalDto", "getAmount", "()J", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand", "<init>",
					"(Ljava/lang/String;J)V", false);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(71, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/web/BankAccountController", "commandBus",
					"Lorg/axonframework/commandhandling/CommandBus;");
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/GenericCommandMessage", "asCommandMessage",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/CommandMessage;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/CommandBus", "dispatch",
					"(Lorg/axonframework/commandhandling/CommandMessage;)V", true);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(72, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/web/BankAccountController;", null, l0, l3, 0);
			mv.visitLocalVariable("depositDto", "Lorg/axonframework/samples/bank/web/dto/WithdrawalDto;", null, l0, l3, 1);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand;", null, l1, l3, 2);
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "deposit", "(Lorg/axonframework/samples/bank/web/dto/DepositDto;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/springframework/messaging/handler/annotation/MessageMapping;", true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					av1.visit(null, "/deposit");
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(76, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/web/dto/DepositDto", "getBankAccountId", "()Ljava/lang/String;", false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/web/dto/DepositDto", "getAmount", "()J", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "<init>",
					"(Ljava/lang/String;J)V", false);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(77, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/web/BankAccountController", "commandBus",
					"Lorg/axonframework/commandhandling/CommandBus;");
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/GenericCommandMessage", "asCommandMessage",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/CommandMessage;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/CommandBus", "dispatch",
					"(Lorg/axonframework/commandhandling/CommandMessage;)V", true);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(78, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/web/BankAccountController;", null, l0, l3, 0);
			mv.visitLocalVariable("depositDto", "Lorg/axonframework/samples/bank/web/dto/DepositDto;", null, l0, l3, 1);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l1, l3, 2);
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
