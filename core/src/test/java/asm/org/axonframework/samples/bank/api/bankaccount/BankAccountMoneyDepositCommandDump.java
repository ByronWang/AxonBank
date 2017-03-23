package asm.org.axonframework.samples.bank.api.bankaccount;
import java.util.*;
import org.objectweb.asm.*;
public class BankAccountMoneyDepositCommandDump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
MethodVisitor mv;
AnnotationVisitor av0;

cw.visit(52, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", null, "java/lang/Object", null);

cw.visitSource("BankAccountMoneyDepositCommand.java", null);

{
fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "bankAccountId", "Ljava/lang/String;", null, null);
{
av0 = fv.visitAnnotation("Lorg/axonframework/commandhandling/TargetAggregateIdentifier;", true);
av0.visitEnd();
}
fv.visitEnd();
}
{
fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "amountOfMoney", "J", null, null);
fv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "getBankAccountId", "()Ljava/lang/String;", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(22, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "bankAccountId", "Ljava/lang/String;");
mv.visitInsn(ARETURN);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l0, l1, 0);
mv.visitMaxs(1, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "getAmountOfMoney", "()J", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(22, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "amountOfMoney", "J");
mv.visitInsn(LRETURN);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l0, l1, 0);
mv.visitMaxs(2, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(22, l0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ALOAD, 0);
Label l1 = new Label();
mv.visitJumpInsn(IF_ACMPNE, l1);
mv.visitInsn(ICONST_1);
mv.visitInsn(IRETURN);
mv.visitLabel(l1);
mv.visitFrame(Opcodes.F_NEW, 2, new Object[] {"org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object"}, 0, new Object[] {});
mv.visitVarInsn(ALOAD, 1);
mv.visitTypeInsn(INSTANCEOF, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand");
Label l2 = new Label();
mv.visitJumpInsn(IFNE, l2);
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
mv.visitLabel(l2);
mv.visitFrame(Opcodes.F_NEW, 2, new Object[] {"org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object"}, 0, new Object[] {});
mv.visitVarInsn(ALOAD, 1);
mv.visitTypeInsn(CHECKCAST, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand");
mv.visitVarInsn(ASTORE, 2);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitVarInsn(ASTORE, 3);
Label l4 = new Label();
mv.visitLabel(l4);
mv.visitVarInsn(ALOAD, 2);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitVarInsn(ASTORE, 4);
Label l5 = new Label();
mv.visitLabel(l5);
mv.visitVarInsn(ALOAD, 3);
Label l6 = new Label();
mv.visitJumpInsn(IFNONNULL, l6);
mv.visitVarInsn(ALOAD, 4);
Label l7 = new Label();
mv.visitJumpInsn(IFNULL, l7);
Label l8 = new Label();
mv.visitJumpInsn(GOTO, l8);
mv.visitLabel(l6);
mv.visitFrame(Opcodes.F_NEW, 5, new Object[] {"org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object", "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object", "java/lang/Object"}, 0, new Object[] {});
mv.visitVarInsn(ALOAD, 3);
mv.visitVarInsn(ALOAD, 4);
mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
mv.visitJumpInsn(IFNE, l7);
mv.visitLabel(l8);
mv.visitFrame(Opcodes.F_NEW, 5, new Object[] {"org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object", "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object", "java/lang/Object"}, 0, new Object[] {});
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
mv.visitLabel(l7);
mv.visitFrame(Opcodes.F_NEW, 5, new Object[] {"org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object", "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object", "java/lang/Object"}, 0, new Object[] {});
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getAmountOfMoney", "()J", false);
mv.visitVarInsn(ALOAD, 2);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getAmountOfMoney", "()J", false);
mv.visitInsn(LCMP);
Label l9 = new Label();
mv.visitJumpInsn(IFEQ, l9);
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
mv.visitLabel(l9);
mv.visitFrame(Opcodes.F_NEW, 5, new Object[] {"org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object", "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "java/lang/Object", "java/lang/Object"}, 0, new Object[] {});
mv.visitInsn(ICONST_1);
mv.visitInsn(IRETURN);
Label l10 = new Label();
mv.visitLabel(l10);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l0, l10, 0);
mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l0, l10, 1);
mv.visitLocalVariable("other", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l3, l10, 2);
mv.visitLocalVariable("this$bankAccountId", "Ljava/lang/Object;", null, l4, l10, 3);
mv.visitLocalVariable("other$bankAccountId", "Ljava/lang/Object;", null, l5, l10, 4);
mv.visitMaxs(4, 5);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(22, l0);
mv.visitIntInsn(BIPUSH, 59);
mv.visitVarInsn(ISTORE, 1);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitInsn(ICONST_1);
mv.visitVarInsn(ISTORE, 2);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitVarInsn(ASTORE, 3);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitVarInsn(ILOAD, 2);
mv.visitIntInsn(BIPUSH, 59);
mv.visitInsn(IMUL);
mv.visitVarInsn(ALOAD, 3);
Label l4 = new Label();
mv.visitJumpInsn(IFNONNULL, l4);
mv.visitIntInsn(BIPUSH, 43);
Label l5 = new Label();
mv.visitJumpInsn(GOTO, l5);
mv.visitLabel(l4);
mv.visitFrame(Opcodes.F_NEW, 4, new Object[] {"org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", Opcodes.INTEGER, Opcodes.INTEGER, "java/lang/Object"}, 1, new Object[] {Opcodes.INTEGER});
mv.visitVarInsn(ALOAD, 3);
mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false);
mv.visitLabel(l5);
mv.visitFrame(Opcodes.F_NEW, 4, new Object[] {"org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", Opcodes.INTEGER, Opcodes.INTEGER, "java/lang/Object"}, 2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER});
mv.visitInsn(IADD);
mv.visitVarInsn(ISTORE, 2);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getAmountOfMoney", "()J", false);
mv.visitVarInsn(LSTORE, 4);
Label l6 = new Label();
mv.visitLabel(l6);
mv.visitVarInsn(ILOAD, 2);
mv.visitIntInsn(BIPUSH, 59);
mv.visitInsn(IMUL);
mv.visitVarInsn(LLOAD, 4);
mv.visitVarInsn(LLOAD, 4);
mv.visitIntInsn(BIPUSH, 32);
mv.visitInsn(LUSHR);
mv.visitInsn(LXOR);
mv.visitInsn(L2I);
mv.visitInsn(IADD);
mv.visitVarInsn(ISTORE, 2);
mv.visitVarInsn(ILOAD, 2);
mv.visitInsn(IRETURN);
Label l7 = new Label();
mv.visitLabel(l7);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l0, l7, 0);
mv.visitLocalVariable("PRIME", "I", null, l1, l7, 1);
mv.visitLocalVariable("result", "I", null, l2, l7, 2);
mv.visitLocalVariable("$bankAccountId", "Ljava/lang/Object;", null, l3, l7, 3);
mv.visitLocalVariable("$amountOfMoney", "J", null, l6, l7, 4);
mv.visitMaxs(6, 6);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(22, l0);
mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
mv.visitInsn(DUP);
mv.visitLdcInsn("BankAccountMoneyDepositCommand(bankAccountId=");
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
mv.visitLdcInsn(", amountOfMoney=");
mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getAmountOfMoney", "()J", false);
mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
mv.visitLdcInsn(")");
mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
mv.visitInsn(ARETURN);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l0, l1, 0);
mv.visitMaxs(3, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;J)V", null, null);
{
av0 = mv.visitAnnotation("Ljava/beans/ConstructorProperties;", true);
{
AnnotationVisitor av1 = av0.visitArray("value");
av1.visit(null, "bankAccountId");
av1.visit(null, "amountOfMoney");
av1.visitEnd();
}
av0.visitEnd();
}
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(22, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "bankAccountId", "Ljava/lang/String;");
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(LLOAD, 2);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "amountOfMoney", "J");
mv.visitInsn(RETURN);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l0, l1, 0);
mv.visitLocalVariable("bankAccountId", "Ljava/lang/String;", null, l0, l1, 1);
mv.visitLocalVariable("amountOfMoney", "J", null, l0, l1, 2);
mv.visitMaxs(3, 4);
mv.visitEnd();
}
cw.visitEnd();

return cw.toByteArray();
}
}
