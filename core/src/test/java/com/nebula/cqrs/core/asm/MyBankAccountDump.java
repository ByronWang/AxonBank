package com.nebula.cqrs.core.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MyBankAccountDump implements Opcodes {

    public static byte[] dump() throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "org/axonframework/samples/bankcqrs/MyBankAccount", null, "java/lang/Object", null);

        cw.visitSource("MyBankAccount.java", null);

        {
            av0 = cw.visitAnnotation("Lorg/axonframework/spring/stereotype/Aggregate;", true);
            av0.visitEnd();
        }
        {
            av0 = cw.visitAnnotation("Lcom/nebula/cqrs/core/CqrsEntity;", false);
            av0.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "axonBankAccountId", "Ljava/lang/String;", null, null);
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
            fv = cw.visitField(ACC_PRIVATE, "balance", "J", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(34, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(35, l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrs/MyBankAccount;", null, l0, l2, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;J)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(38, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(39, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(LLOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bankcqrs/MyBankAccount", "onCreated", "(Ljava/lang/String;J)V", false);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(40, l2);
            mv.visitInsn(RETURN);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrs/MyBankAccount;", null, l0, l3, 0);
            mv.visitLocalVariable("axonBankAccountId", "Ljava/lang/String;", null, l0, l3, 1);
            mv.visitLocalVariable("overdraftLimit", "J", null, l0, l3, 2);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "deposit", "(J)Z", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(44, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(LLOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bankcqrs/MyBankAccount", "onMoneyAdded", "(J)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(45, l1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrs/MyBankAccount;", null, l0, l2, 0);
            mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "withdraw", "(J)Z", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(50, l0);
            mv.visitVarInsn(LLOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "balance", "J");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "overdraftLimit", "J");
            mv.visitInsn(LADD);
            mv.visitInsn(LCMP);
            Label l1 = new Label();
            mv.visitJumpInsn(IFGT, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(51, l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(LLOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bankcqrs/MyBankAccount", "onMoneySubtracted", "(J)V", false);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(52, l3);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l1);
            mv.visitLineNumber(54, l1);
            mv.visitFrame(Opcodes.F_NEW, 2, new Object[] { "org/axonframework/samples/bankcqrs/MyBankAccount", Opcodes.LONG }, 0, new Object[] {});
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrs/MyBankAccount;", null, l0, l4, 0);
            mv.visitLocalVariable("amount", "J", null, l0, l4, 1);
            mv.visitMaxs(6, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE, "onCreated", "(Ljava/lang/String;J)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(97, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "axonBankAccountId", "Ljava/lang/String;");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(98, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(LLOAD, 2);
            mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "overdraftLimit", "J");
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(99, l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(LCONST_0);
            mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "balance", "J");
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(100, l3);
            mv.visitInsn(RETURN);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrs/MyBankAccount;", null, l0, l4, 0);
            mv.visitLocalVariable("axonBankAccountId", "Ljava/lang/String;", null, l0, l4, 1);
            mv.visitLocalVariable("overdraftLimit", "J", null, l0, l4, 2);
            mv.visitMaxs(3, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE, "onMoneyAdded", "(J)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(104, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "balance", "J");
            mv.visitVarInsn(LLOAD, 1);
            mv.visitInsn(LADD);
            mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "balance", "J");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(105, l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrs/MyBankAccount;", null, l0, l2, 0);
            mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
            mv.visitMaxs(5, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE, "onMoneySubtracted", "(J)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(109, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "balance", "J");
            mv.visitVarInsn(LLOAD, 1);
            mv.visitInsn(LSUB);
            mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bankcqrs/MyBankAccount", "balance", "J");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(110, l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrs/MyBankAccount;", null, l0, l2, 0);
            mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
            mv.visitMaxs(5, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
