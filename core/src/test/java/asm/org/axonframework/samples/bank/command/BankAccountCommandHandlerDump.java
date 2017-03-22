package asm.org.axonframework.samples.bank.command;

import java.util.*;
import org.objectweb.asm.*;

public class BankAccountCommandHandlerDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "org/axonframework/samples/bank/command/BankAccountCommandHandler", null, "java/lang/Object", null);

		cw.visitSource("BankAccountCommandHandler.java", null);

		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

		{
			fv = cw.visitField(ACC_PRIVATE, "repository", "Lorg/axonframework/commandhandling/model/Repository;",
					"Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bank/command/BankAccount;>;", null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/axonframework/commandhandling/model/Repository;Lorg/axonframework/eventhandling/EventBus;)V",
					"(Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bank/command/BankAccount;>;Lorg/axonframework/eventhandling/EventBus;)V",
					null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(40, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(41, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "repository",
					"Lorg/axonframework/commandhandling/model/Repository;");
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(42, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "eventBus",
					"Lorg/axonframework/eventhandling/EventBus;");
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(43, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler;", null, l0, l4, 0);
			mv.visitLocalVariable("repository", "Lorg/axonframework/commandhandling/model/Repository;",
					"Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bank/command/BankAccount;>;", l0, l4, 1);
			mv.visitLocalVariable("eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, l0, l4, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;)V", null,
					new String[] { "java/lang/Exception" });
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(47, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "repository",
					"Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInvokeDynamicInsn("call", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;)Ljava/util/concurrent/Callable;",
					new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[] { Type.getType("()Ljava/lang/Object;"),
							new Handle(Opcodes.H_INVOKESTATIC, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "lambda$0",
									"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;)Lorg/axonframework/samples/bank/command/BankAccount;"),
							Type.getType("()Lorg/axonframework/samples/bank/command/BankAccount;") });
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "newInstance",
					"(Ljava/util/concurrent/Callable;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(48, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler;", null, l0, l2, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;", null, l0, l2, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(52, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "repository",
					"Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getBankAccountId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load",
					"(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(53, l1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInvokeDynamicInsn("accept",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;)Ljava/util/function/Consumer;",
					new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[] { Type.getType("(Ljava/lang/Object;)V"),
							new Handle(Opcodes.H_INVOKESTATIC, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "lambda$1",
									"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V"),
							Type.getType("(Lorg/axonframework/samples/bank/command/BankAccount;)V") });
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(54, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler;", null, l0, l3, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l0, l3, 1);
			mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
					"Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l1, l3, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(58, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "repository",
					"Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand", "getBankAccountId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load",
					"(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(59, l1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInvokeDynamicInsn("accept",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand;)Ljava/util/function/Consumer;",
					new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[] { Type.getType("(Ljava/lang/Object;)V"),
							new Handle(Opcodes.H_INVOKESTATIC, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "lambda$2",
									"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V"),
							Type.getType("(Lorg/axonframework/samples/bank/command/BankAccount;)V") });
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(60, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler;", null, l0, l3, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand;", null, l0, l3, 1);
			mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
					"Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l1, l3, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitTryCatchBlock(l0, l1, l2, "org/axonframework/commandhandling/model/AggregateNotFoundException");
			mv.visitLabel(l0);
			mv.visitLineNumber(65, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "repository",
					"Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand", "getBankAccountId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load",
					"(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitVarInsn(ASTORE, 2);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(66, l3);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInvokeDynamicInsn("accept",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand;)Ljava/util/function/Consumer;",
					new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[] { Type.getType("(Ljava/lang/Object;)V"),
							new Handle(Opcodes.H_INVOKESTATIC, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "lambda$3",
									"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V"),
							Type.getType("(Lorg/axonframework/samples/bank/command/BankAccount;)V") });
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
			mv.visitLabel(l1);
			mv.visitLineNumber(68, l1);
			Label l4 = new Label();
			mv.visitJumpInsn(GOTO, l4);
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_NEW, 2,
					new Object[] { "org/axonframework/samples/bank/command/BankAccountCommandHandler",
							"org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand" },
					1, new Object[] { "org/axonframework/commandhandling/model/AggregateNotFoundException" });
			mv.visitVarInsn(ASTORE, 2);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLineNumber(69, l5);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "eventBus",
					"Lorg/axonframework/eventhandling/EventBus;");
			mv.visitInsn(ICONST_1);
			mv.visitTypeInsn(ANEWARRAY, "org/axonframework/eventhandling/EventMessage");
			mv.visitInsn(DUP);
			mv.visitInsn(ICONST_0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceNotFoundEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand", "getBankTransferId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceNotFoundEvent", "<init>",
					"(Ljava/lang/String;)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/eventhandling/GenericEventMessage", "asEventMessage",
					"(Ljava/lang/Object;)Lorg/axonframework/eventhandling/EventMessage;", false);
			mv.visitInsn(AASTORE);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/eventhandling/EventBus", "publish", "([Lorg/axonframework/eventhandling/EventMessage;)V",
					true);
			mv.visitLabel(l4);
			mv.visitLineNumber(71, l4);
			mv.visitFrame(Opcodes.F_NEW, 2, new Object[] { "org/axonframework/samples/bank/command/BankAccountCommandHandler",
					"org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand" }, 0, new Object[] {});
			mv.visitInsn(RETURN);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler;", null, l0, l6, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand;", null, l0, l6, 1);
			mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
					"Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l3, l1, 2);
			mv.visitLocalVariable("exception", "Lorg/axonframework/commandhandling/model/AggregateNotFoundException;", null, l5, l4, 2);
			mv.visitMaxs(7, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitTryCatchBlock(l0, l1, l2, "org/axonframework/commandhandling/model/AggregateNotFoundException");
			mv.visitLabel(l0);
			mv.visitLineNumber(76, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "repository",
					"Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand", "getBankAccountId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load",
					"(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitVarInsn(ASTORE, 2);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(77, l3);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInvokeDynamicInsn("accept",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand;)Ljava/util/function/Consumer;",
					new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[] { Type.getType("(Ljava/lang/Object;)V"),
							new Handle(Opcodes.H_INVOKESTATIC, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "lambda$4",
									"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V"),
							Type.getType("(Lorg/axonframework/samples/bank/command/BankAccount;)V") });
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
			mv.visitLabel(l1);
			mv.visitLineNumber(80, l1);
			Label l4 = new Label();
			mv.visitJumpInsn(GOTO, l4);
			mv.visitLabel(l2);
			mv.visitLineNumber(81, l2);
			mv.visitFrame(Opcodes.F_NEW, 2,
					new Object[] { "org/axonframework/samples/bank/command/BankAccountCommandHandler",
							"org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand" },
					1, new Object[] { "org/axonframework/commandhandling/model/AggregateNotFoundException" });
			mv.visitVarInsn(ASTORE, 2);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLineNumber(82, l5);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "eventBus",
					"Lorg/axonframework/eventhandling/EventBus;");
			mv.visitInsn(ICONST_1);
			mv.visitTypeInsn(ANEWARRAY, "org/axonframework/eventhandling/EventMessage");
			mv.visitInsn(DUP);
			mv.visitInsn(ICONST_0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationNotFoundEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand", "getBankTransferId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationNotFoundEvent", "<init>",
					"(Ljava/lang/String;)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/eventhandling/GenericEventMessage", "asEventMessage",
					"(Ljava/lang/Object;)Lorg/axonframework/eventhandling/EventMessage;", false);
			mv.visitInsn(AASTORE);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/eventhandling/EventBus", "publish", "([Lorg/axonframework/eventhandling/EventMessage;)V",
					true);
			mv.visitLabel(l4);
			mv.visitLineNumber(84, l4);
			mv.visitFrame(Opcodes.F_NEW, 2, new Object[] { "org/axonframework/samples/bank/command/BankAccountCommandHandler",
					"org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand" }, 0, new Object[] {});
			mv.visitInsn(RETURN);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler;", null, l0, l6, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand;", null, l0, l6, 1);
			mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
					"Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l3, l1, 2);
			mv.visitLocalVariable("exception", "Lorg/axonframework/commandhandling/model/AggregateNotFoundException;", null, l5, l4, 2);
			mv.visitMaxs(7, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(88, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "repository",
					"Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand", "getBankAccountId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load",
					"(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(89, l1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInvokeDynamicInsn("accept",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand;)Ljava/util/function/Consumer;",
					new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[] { Type.getType("(Ljava/lang/Object;)V"),
							new Handle(Opcodes.H_INVOKESTATIC, "org/axonframework/samples/bank/command/BankAccountCommandHandler", "lambda$5",
									"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V"),
							Type.getType("(Lorg/axonframework/samples/bank/command/BankAccount;)V") });
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(90, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler;", null, l0, l3, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand;", null, l0, l3, 1);
			mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
					"Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l1, l3, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$0",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;)Lorg/axonframework/samples/bank/command/BankAccount;", null,
					new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(47, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/command/BankAccount");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand", "getBankAccountId",
					"()Ljava/lang/String;", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand", "getOverdraftLimit", "()J", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/command/BankAccount", "<init>", "(Ljava/lang/String;J)V", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(5, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$1",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V",
					null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(53, l0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getAmountOfMoney", "()J",
					false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/command/BankAccount", "deposit", "(J)V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("bankAccount", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l1, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$2",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V",
					null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(59, l0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand", "getAmountOfMoney", "()J",
					false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/command/BankAccount", "withdraw", "(J)V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("bankAccount", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l1, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$3",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V",
					null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(66, l0);
			mv.visitVarInsn(ALOAD, 1);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(67, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand", "getAmount", "()J", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand", "getBankTransferId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/command/BankAccount", "debit", "(JLjava/lang/String;)V", false);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("bankAccount", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 1);
			mv.visitMaxs(4, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$4",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V",
					null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(77, l0);
			mv.visitVarInsn(ALOAD, 1);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(78, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand", "getAmount", "()J", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand", "getBankTransferId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/command/BankAccount", "credit", "(JLjava/lang/String;)V", false);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("bankAccount", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 1);
			mv.visitMaxs(4, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$5",
					"(Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand;Lorg/axonframework/samples/bank/command/BankAccount;)V",
					null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(89, l0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand", "getAmount", "()J", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/command/BankAccount", "returnMoney", "(J)V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("bankAccount", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l1, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
