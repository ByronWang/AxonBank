package com.nebula.cqrs.core.asm;
import java.util.*;
import org.objectweb.asm.*;
public class MyBankAccountCommandHandlerDump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
MethodVisitor mv;
AnnotationVisitor av0;

cw.visit(52, ACC_PUBLIC + ACC_SUPER, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", null, "java/lang/Object", null);

cw.visitSource("MyBankAccountCommandHandler.java", null);

cw.visitInnerClass("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerCreate", "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "InnerCreate", 0);

cw.visitInnerClass("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerDeposit", "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "InnerDeposit", 0);

cw.visitInnerClass("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerWithdraw", "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "InnerWithdraw", 0);

{
fv = cw.visitField(ACC_PRIVATE, "repository", "Lorg/axonframework/commandhandling/model/Repository;", "Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;>;", null);
fv.visitEnd();
}
{
fv = cw.visitField(ACC_PRIVATE, "eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, null);
fv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/axonframework/commandhandling/model/Repository;Lorg/axonframework/eventhandling/EventBus;)V", "(Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;>;Lorg/axonframework/eventhandling/EventBus;)V", null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(15, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(17, l1);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 2);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "eventBus", "Lorg/axonframework/eventhandling/EventBus;");
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(18, l2);
mv.visitInsn(RETURN);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;", null, l0, l3, 0);
mv.visitLocalVariable("repository", "Lorg/axonframework/commandhandling/model/Repository;", "Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;>;", l0, l3, 1);
mv.visitLocalVariable("eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, l0, l3, 2);
mv.visitMaxs(2, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCreateCommand;)V", null, new String[] { "java/lang/Exception" });
{
av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
av0.visitEnd();
}
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(24, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitTypeInsn(NEW, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerCreate");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerCreate", "<init>", "(Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCreateCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "newInstance", "(Ljava/util/concurrent/Callable;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitInsn(POP);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(25, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;", null, l0, l2, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCreateCommand;", null, l0, l2, 1);
mv.visitMaxs(5, 2);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountDepositCommand;)V", null, null);
{
av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
av0.visitEnd();
}
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(30, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountDepositCommand", "getAxonBankAccountId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load", "(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitVarInsn(ASTORE, 2);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitVarInsn(ALOAD, 2);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerDeposit");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerDeposit", "<init>", "(Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountDepositCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(31, l2);
mv.visitInsn(RETURN);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;", null, l0, l3, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountDepositCommand;", null, l0, l3, 1);
mv.visitLocalVariable("aggregate", "Lorg/axonframework/commandhandling/model/Aggregate;", "Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;>;", l1, l3, 2);
mv.visitMaxs(5, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand;)V", null, null);
{
av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
av0.visitEnd();
}
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(36, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand", "getAxonBankAccountId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load", "(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitVarInsn(ASTORE, 2);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitVarInsn(ALOAD, 2);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerWithdraw");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerWithdraw", "<init>", "(Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(37, l2);
mv.visitInsn(RETURN);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;", null, l0, l3, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand;", null, l0, l3, 1);
mv.visitLocalVariable("aggregate", "Lorg/axonframework/commandhandling/model/Aggregate;", "Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;>;", l1, l3, 2);
mv.visitMaxs(5, 3);
mv.visitEnd();
}
cw.visitEnd();

return cw.toByteArray();
}
}
