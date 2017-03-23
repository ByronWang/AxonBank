package asm.org.axonframework.samples.bank.command;
import java.util.*;
import org.objectweb.asm.*;
public class BankAccountCommandHandler2$2OlderDump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
MethodVisitor mv;
AnnotationVisitor av0;

cw.visit(52, ACC_SUPER, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$2", "Ljava/lang/Object;Ljava/util/function/Consumer<Lorg/axonframework/samples/bank/command/BankAccount;>;", "java/lang/Object", new String[] { "java/util/function/Consumer" });

cw.visitSource("BankAccountCommandHandler2.java", null);

cw.visitOuterClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2", "handle", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;)V");

cw.visitInnerClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2$2", null, null, 0);

{
fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, null);
fv.visitEnd();
}
{
fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC, "val$command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, null);
fv.visitEnd();
}
{
mv = cw.visitMethod(0, "<init>", "(Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;)V", null, null);
mv.visitParameter("this$0", ACC_FINAL + ACC_MANDATED);
mv.visitParameter("val$command", ACC_FINAL + ACC_SYNTHETIC);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(1, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$2", "this$0", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;");
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 2);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$2", "val$command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;");
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(62, l1);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2$2;", null, l0, l2, 0);
mv.visitMaxs(2, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "accept", "(Lorg/axonframework/samples/bank/command/BankAccount;)V", null, null);
mv.visitParameter("bankAccount", 0);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(65, l0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$2", "val$command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;");
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getAmountOfMoney", "()J", false);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/command/BankAccount", "deposit", "(J)V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(66, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2$2;", null, l0, l2, 0);
mv.visitLocalVariable("bankAccount", "Lorg/axonframework/samples/bank/command/BankAccount;", null, l0, l2, 1);
mv.visitMaxs(3, 2);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "accept", "(Ljava/lang/Object;)V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(1, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitTypeInsn(CHECKCAST, "org/axonframework/samples/bank/command/BankAccount");
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$2", "accept", "(Lorg/axonframework/samples/bank/command/BankAccount;)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(2, 2);
mv.visitEnd();
}
cw.visitEnd();

return cw.toByteArray();
}
}
