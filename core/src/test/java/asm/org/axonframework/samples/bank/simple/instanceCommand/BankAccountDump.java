package asm.org.axonframework.samples.bank.simple.instanceCommand;
import java.util.*;
import org.objectweb.asm.*;
public class BankAccountDump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
MethodVisitor mv;
AnnotationVisitor av0;

cw.visit(52, ACC_PUBLIC + ACC_SUPER, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", null, "java/lang/Object", null);

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
mv = cw.visitMethod(ACC_PUBLIC, "getOverdraftLimit", "()J", null, null);
mv.visitCode();
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "completed", "J");
mv.visitInsn(LRETURN);
mv.visitMaxs(0, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "setOverdraftLimit", "(J)V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(31, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "setId", "(Ljava/lang/String;)V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(32, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitLocalVariable("overdraftLimit", "J", null, l0, l2, 1);
mv.visitMaxs(2, 2);
mv.visitEnd();
}
{
fv = cw.visitField(ACC_PRIVATE, "balanceInCents", "J", null, null);
fv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "getBalanceInCents", "()J", null, null);
mv.visitCode();
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "completed", "J");
mv.visitInsn(LRETURN);
mv.visitMaxs(0, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "setBalanceInCents", "(J)V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(31, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "setId", "(Ljava/lang/String;)V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(32, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitLocalVariable("balanceInCents", "J", null, l0, l2, 1);
mv.visitMaxs(2, 2);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "setId", "(Ljava/lang/String;)V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(31, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "id", "Ljava/lang/String;");
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(32, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitLocalVariable("id", "Ljava/lang/String;", null, l0, l2, 1);
mv.visitMaxs(2, 2);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "getId", "()Ljava/lang/String;", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(35, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "id", "Ljava/lang/String;");
mv.visitInsn(ARETURN);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l1, 0);
mv.visitMaxs(1, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PRIVATE, "<init>", "()V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(47, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(48, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitMaxs(1, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;J)V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(50, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(51, l1);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(LLOAD, 2);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "onCreated", "(Ljava/lang/String;J)V", false);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(52, l2);
mv.visitInsn(RETURN);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l3, 0);
mv.visitLocalVariable("bankAccountId", "Ljava/lang/String;", null, l0, l3, 1);
mv.visitLocalVariable("overdraftLimit", "J", null, l0, l3, 2);
mv.visitMaxs(4, 4);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "create", "(Ljava/lang/String;J)Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(56, l0);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(LLOAD, 2);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "<init>", "(Ljava/lang/String;J)V", false);
mv.visitInsn(ARETURN);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l1, 0);
mv.visitLocalVariable("bankAccountId", "Ljava/lang/String;", null, l0, l1, 1);
mv.visitLocalVariable("overdraftLimit", "J", null, l0, l1, 2);
mv.visitMaxs(5, 4);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "deposit", "(J)Z", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(61, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "onMoneyAdded", "(J)V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(62, l1);
mv.visitInsn(ICONST_1);
mv.visitInsn(IRETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
mv.visitMaxs(3, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "withdraw", "(J)Z", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(67, l0);
mv.visitVarInsn(LLOAD, 1);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "balanceInCents", "J");
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "overdraftLimit", "J");
mv.visitInsn(LADD);
mv.visitInsn(LCMP);
Label l1 = new Label();
mv.visitJumpInsn(IFGT, l1);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(68, l2);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "onMoneySubtracted", "(J)V", false);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLineNumber(69, l3);
mv.visitInsn(ICONST_1);
mv.visitInsn(IRETURN);
mv.visitLabel(l1);
mv.visitLineNumber(71, l1);
mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
Label l4 = new Label();
mv.visitLabel(l4);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l4, 0);
mv.visitLocalVariable("amount", "J", null, l0, l4, 1);
mv.visitMaxs(6, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "bankTransfer", "(Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;J)Z", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(76, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 2);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "debit", "(J)Z", false);
mv.visitVarInsn(ISTORE, 4);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(77, l1);
mv.visitVarInsn(ILOAD, 4);
Label l2 = new Label();
mv.visitJumpInsn(IFEQ, l2);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLineNumber(78, l3);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(LLOAD, 2);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "credit", "(J)Z", false);
mv.visitVarInsn(ISTORE, 5);
Label l4 = new Label();
mv.visitLabel(l4);
mv.visitLineNumber(79, l4);
mv.visitVarInsn(ILOAD, 5);
Label l5 = new Label();
mv.visitJumpInsn(IFEQ, l5);
Label l6 = new Label();
mv.visitLabel(l6);
mv.visitLineNumber(80, l6);
mv.visitInsn(ICONST_1);
mv.visitInsn(IRETURN);
mv.visitLabel(l5);
mv.visitLineNumber(82, l5);
mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 2);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "returnMoney", "(J)Z", false);
mv.visitInsn(POP);
Label l7 = new Label();
mv.visitLabel(l7);
mv.visitLineNumber(83, l7);
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
mv.visitLabel(l2);
mv.visitLineNumber(86, l2);
mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
Label l8 = new Label();
mv.visitLabel(l8);
mv.visitLocalVariable("source", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l8, 0);
mv.visitLocalVariable("destination", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l8, 1);
mv.visitLocalVariable("amount", "J", null, l0, l8, 2);
mv.visitLocalVariable("sourceDebitSucceed", "Z", null, l1, l8, 4);
mv.visitLocalVariable("destinationCreditSucceed", "Z", null, l4, l2, 5);
mv.visitMaxs(3, 6);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "debit", "(J)Z", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(92, l0);
mv.visitVarInsn(LLOAD, 1);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "balanceInCents", "J");
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "overdraftLimit", "J");
mv.visitInsn(LADD);
mv.visitInsn(LCMP);
Label l1 = new Label();
mv.visitJumpInsn(IFGT, l1);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(93, l2);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "onMoneySubtracted", "(J)V", false);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLineNumber(94, l3);
mv.visitInsn(ICONST_1);
mv.visitInsn(IRETURN);
mv.visitLabel(l1);
mv.visitLineNumber(96, l1);
mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
Label l4 = new Label();
mv.visitLabel(l4);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l4, 0);
mv.visitLocalVariable("amount", "J", null, l0, l4, 1);
mv.visitMaxs(6, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "credit", "(J)Z", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(102, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "onMoneyAdded", "(J)V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(103, l1);
mv.visitInsn(ICONST_1);
mv.visitInsn(IRETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
mv.visitMaxs(3, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "returnMoney", "(J)Z", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(108, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "onMoneyAdded", "(J)V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(109, l1);
mv.visitInsn(ICONST_1);
mv.visitInsn(IRETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
mv.visitMaxs(3, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(0, "onCreated", "(Ljava/lang/String;J)V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(114, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "id", "Ljava/lang/String;");
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(115, l1);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 2);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "overdraftLimit", "J");
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(116, l2);
mv.visitVarInsn(ALOAD, 0);
mv.visitInsn(LCONST_0);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "balanceInCents", "J");
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLineNumber(117, l3);
mv.visitInsn(RETURN);
Label l4 = new Label();
mv.visitLabel(l4);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l4, 0);
mv.visitLocalVariable("id", "Ljava/lang/String;", null, l0, l4, 1);
mv.visitLocalVariable("overdraftLimit", "J", null, l0, l4, 2);
mv.visitMaxs(3, 4);
mv.visitEnd();
}
{
mv = cw.visitMethod(0, "onMoneyAdded", "(J)V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(121, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitInsn(DUP);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "balanceInCents", "J");
mv.visitVarInsn(LLOAD, 1);
mv.visitInsn(LADD);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "balanceInCents", "J");
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(122, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
mv.visitMaxs(5, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(0, "onMoneySubtracted", "(J)V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(126, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitInsn(DUP);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "balanceInCents", "J");
mv.visitVarInsn(LLOAD, 1);
mv.visitInsn(LSUB);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/BankAccount", "balanceInCents", "J");
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(127, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/BankAccount;", null, l0, l2, 0);
mv.visitLocalVariable("amount", "J", null, l0, l2, 1);
mv.visitMaxs(5, 3);
mv.visitEnd();
}
cw.visitEnd();

return cw.toByteArray();
}
}
