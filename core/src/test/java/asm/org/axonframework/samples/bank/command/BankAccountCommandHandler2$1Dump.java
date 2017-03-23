package asm.org.axonframework.samples.bank.command;

import java.util.*;
import org.objectweb.asm.*;

public class BankAccountCommandHandler2$1Dump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_SUPER, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$1",
				"Ljava/lang/Object;Ljava/util/concurrent/Callable<Lorg/axonframework/samples/bank/command/BankAccount;>;", "java/lang/Object",
				new String[] { "java/util/concurrent/Callable" });

		cw.visitSource("BankAccountCommandHandler2.java", null);

		cw.visitOuterClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2", "handle",
				"(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;)V");

		cw.visitInnerClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2$1", null, null, 0);

		{
			fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC, "val$command",
					"Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "<init>",
					"(Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;)V",
					null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$1", "this$0",
					"Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$1", "val$command",
					"Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(50, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2$1;", null, l0, l2, 0);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "call", "()Lorg/axonframework/samples/bank/command/BankAccount;", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(54, l0);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/command/BankAccount");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$1", "val$command",
					"Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand", "getBankAccountId",
					"()Ljava/lang/String;", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$1", "val$command",
					"Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand", "getOverdraftLimit", "()J", false);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/command/BankAccount", "<init>", "(Ljava/lang/String;J)V", false);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2$1;", null, l0, l1, 0);
			mv.visitMaxs(5, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "call", "()Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$1", "call",
					"()Lorg/axonframework/samples/bank/command/BankAccount;", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
