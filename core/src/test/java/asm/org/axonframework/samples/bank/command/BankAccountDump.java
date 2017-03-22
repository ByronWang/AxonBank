package asm.org.axonframework.samples.bank.command;

import java.util.*;
import org.objectweb.asm.*;

public class BankAccountDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "org/axonframework/samples/bank/command/BankAccount", null, "java/lang/Object", null);

		cw.visitSource("BankAccount.java", null);

		{
			av0 = cw.visitAnnotation("Lorg/axonframework/spring/stereotype/Aggregate;", true);
			av0.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "id", "Ljava/lang/String;", null, null);
			{
				av0 = fv.visitAnnotation("Lorg/axonframework/commandhandling/model/AggregateIdentifier;", true);
				av0.visitEnd();
			}
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "overdraftLimit", "J", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "balanceInCents", "J", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PRIVATE, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(43, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(44, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;J)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(46, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(47, l1);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreatedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(LLOAD, 2);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreatedEvent", "<init>", "(Ljava/lang/String;J)V",
					false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(48, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l3, 0);
			mv.visitLocalVariable("bankAccountId", "Ljava/lang/String;", null, l0, l3, 1);
			mv.visitLocalVariable("overdraftLimit", "J", null, l0, l3, 2);
			mv.visitMaxs(5, 4);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "deposit", "(J)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(51, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "id", "Ljava/lang/String;");
			mv.visitVarInsn(LLOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositedEvent", "<init>",
					"(Ljava/lang/String;J)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(52, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 0);
			mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "withdraw", "(J)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(55, l0);
			mv.visitVarInsn(LLOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "balanceInCents", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "overdraftLimit", "J");
			mv.visitInsn(LADD);
			mv.visitInsn(LCMP);
			Label l1 = new Label();
			mv.visitJumpInsn(IFGT, l1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(56, l2);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyWithdrawnEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "id", "Ljava/lang/String;");
			mv.visitVarInsn(LLOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyWithdrawnEvent", "<init>",
					"(Ljava/lang/String;J)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			mv.visitLabel(l1);
			mv.visitLineNumber(58, l1);
			mv.visitFrame(Opcodes.F_NEW, 2, new Object[] { "org/axonframework/samples/bank/command/BankAccount", Opcodes.LONG }, 0, new Object[] {});
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l3, 0);
			mv.visitLocalVariable("amount", "J", null, l0, l3, 1);
			mv.visitMaxs(6, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "debit", "(JLjava/lang/String;)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(61, l0);
			mv.visitVarInsn(LLOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "balanceInCents", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "overdraftLimit", "J");
			mv.visitInsn(LADD);
			mv.visitInsn(LCMP);
			Label l1 = new Label();
			mv.visitJumpInsn(IFGT, l1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(62, l2);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "id", "Ljava/lang/String;");
			mv.visitVarInsn(LLOAD, 1);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitedEvent", "<init>",
					"(Ljava/lang/String;JLjava/lang/String;)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(63, l3);
			Label l4 = new Label();
			mv.visitJumpInsn(GOTO, l4);
			mv.visitLabel(l1);
			mv.visitLineNumber(65, l1);
			mv.visitFrame(Opcodes.F_NEW, 3, new Object[] { "org/axonframework/samples/bank/command/BankAccount", Opcodes.LONG, "java/lang/String" }, 0,
					new Object[] {});
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitRejectedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitRejectedEvent", "<init>",
					"(Ljava/lang/String;)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			mv.visitLabel(l4);
			mv.visitLineNumber(67, l4);
			mv.visitFrame(Opcodes.F_NEW, 3, new Object[] { "org/axonframework/samples/bank/command/BankAccount", Opcodes.LONG, "java/lang/String" }, 0,
					new Object[] {});
			mv.visitInsn(RETURN);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l5, 0);
			mv.visitLocalVariable("amount", "J", null, l0, l5, 1);
			mv.visitLocalVariable("bankTransferId", "Ljava/lang/String;", null, l0, l5, 3);
			mv.visitMaxs(6, 4);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "credit", "(JLjava/lang/String;)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(70, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "id", "Ljava/lang/String;");
			mv.visitVarInsn(LLOAD, 1);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditedEvent", "<init>",
					"(Ljava/lang/String;JLjava/lang/String;)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(71, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 0);
			mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
			mv.visitLocalVariable("bankTransferId", "Ljava/lang/String;", null, l0, l2, 3);
			mv.visitMaxs(6, 4);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "returnMoney", "(J)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(74, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnedMoneyOfFailedEvent");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "id", "Ljava/lang/String;");
			mv.visitVarInsn(LLOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnedMoneyOfFailedEvent", "<init>",
					"(Ljava/lang/String;J)V", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(75, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 0);
			mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "on", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreatedEvent;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/eventhandling/EventHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(79, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreatedEvent", "getId", "()Ljava/lang/String;", false);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccount", "id", "Ljava/lang/String;");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(80, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreatedEvent", "getOverdraftLimit", "()J", false);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccount", "overdraftLimit", "J");
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(81, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(LCONST_0);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccount", "balanceInCents", "J");
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(82, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l4, 0);
			mv.visitLocalVariable("event", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreatedEvent;", null, l0, l4, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "on", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyAddedEvent;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/eventhandling/EventHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(86, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "balanceInCents", "J");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyAddedEvent", "getAmount", "()J", false);
			mv.visitInsn(LADD);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccount", "balanceInCents", "J");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(87, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 0);
			mv.visitLocalVariable("event", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyAddedEvent;", null, l0, l2, 1);
			mv.visitMaxs(5, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "on", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/eventhandling/EventHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(91, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccount", "balanceInCents", "J");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent", "getAmount", "()J", false);
			mv.visitInsn(LSUB);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccount", "balanceInCents", "J");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(92, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 0);
			mv.visitLocalVariable("event", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent;", null, l0, l2, 1);
			mv.visitMaxs(5, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
