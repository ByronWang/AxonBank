package asm.org.axonframework.samples.bank.command;
import java.util.*;
import org.objectweb.asm.*;
public class BankAccountCommandHandler2Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
MethodVisitor mv;
AnnotationVisitor av0;

cw.visit(52, ACC_PUBLIC + ACC_SUPER, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", null, "java/lang/Object", null);

cw.visitSource("BankAccountCommandHandler2.java", null);

cw.visitInnerClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2$1", null, null, 0);

cw.visitInnerClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2$2", null, null, 0);

cw.visitInnerClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2$3", null, null, 0);

cw.visitInnerClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2$4", null, null, 0);

cw.visitInnerClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2$5", null, null, 0);

cw.visitInnerClass("org/axonframework/samples/bank/command/BankAccountCommandHandler2$6", null, null, 0);

{
fv = cw.visitField(ACC_PRIVATE, "repository", "Lorg/axonframework/commandhandling/model/Repository;", "Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bank/command/BankAccount;>;", null);
fv.visitEnd();
}
{
fv = cw.visitField(ACC_PRIVATE, "eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, null);
fv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/axonframework/commandhandling/model/Repository;Lorg/axonframework/eventhandling/EventBus;)V", "(Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bank/command/BankAccount;>;Lorg/axonframework/eventhandling/EventBus;)V", null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(43, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(44, l1);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(45, l2);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 2);
mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "eventBus", "Lorg/axonframework/eventhandling/EventBus;");
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLineNumber(46, l3);
mv.visitInsn(RETURN);
Label l4 = new Label();
mv.visitLabel(l4);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, l0, l4, 0);
mv.visitLocalVariable("repository", "Lorg/axonframework/commandhandling/model/Repository;", "Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bank/command/BankAccount;>;", l0, l4, 1);
mv.visitLocalVariable("eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, l0, l4, 2);
mv.visitMaxs(2, 3);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;)V", null, new String[] { "java/lang/Exception" });
{
av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
av0.visitEnd();
}
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(50, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$1");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$1", "<init>", "(Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "newInstance", "(Ljava/util/concurrent/Callable;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitInsn(POP);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(57, l1);
mv.visitInsn(RETURN);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, l0, l2, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;", null, l0, l2, 1);
mv.visitMaxs(5, 2);
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
mv.visitLineNumber(61, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load", "(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitVarInsn(ASTORE, 2);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(62, l1);
mv.visitVarInsn(ALOAD, 2);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$2");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$2", "<init>", "(Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(68, l2);
mv.visitInsn(RETURN);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, l0, l3, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneyDepositCommand;", null, l0, l3, 1);
mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;", "Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l1, l3, 2);
mv.visitMaxs(5, 3);
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
mv.visitLineNumber(72, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load", "(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitVarInsn(ASTORE, 2);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(73, l1);
mv.visitVarInsn(ALOAD, 2);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$3");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$3", "<init>", "(Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;Lorg/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(79, l2);
mv.visitInsn(RETURN);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, l0, l3, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountWithdrawMoneyCommand;", null, l0, l3, 1);
mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;", "Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l1, l3, 2);
mv.visitMaxs(5, 3);
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
mv.visitLineNumber(84, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load", "(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitVarInsn(ASTORE, 2);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLineNumber(85, l3);
mv.visitVarInsn(ALOAD, 2);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$4");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$4", "<init>", "(Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
mv.visitLabel(l1);
mv.visitLineNumber(92, l1);
Label l4 = new Label();
mv.visitJumpInsn(GOTO, l4);
mv.visitLabel(l2);
mv.visitFrame(Opcodes.F_NEW, 2, new Object[] {"org/axonframework/samples/bank/command/BankAccountCommandHandler2", "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand"}, 1, new Object[] {"org/axonframework/commandhandling/model/AggregateNotFoundException"});
mv.visitVarInsn(ASTORE, 2);
Label l5 = new Label();
mv.visitLabel(l5);
mv.visitLineNumber(93, l5);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "eventBus", "Lorg/axonframework/eventhandling/EventBus;");
mv.visitInsn(ICONST_1);
mv.visitTypeInsn(ANEWARRAY, "org/axonframework/eventhandling/EventMessage");
mv.visitInsn(DUP);
mv.visitInsn(ICONST_0);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceNotFoundEvent");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand", "getBankTransferId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceNotFoundEvent", "<init>", "(Ljava/lang/String;)V", false);
mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/eventhandling/GenericEventMessage", "asEventMessage", "(Ljava/lang/Object;)Lorg/axonframework/eventhandling/EventMessage;", false);
mv.visitInsn(AASTORE);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/eventhandling/EventBus", "publish", "([Lorg/axonframework/eventhandling/EventMessage;)V", true);
mv.visitLabel(l4);
mv.visitLineNumber(95, l4);
mv.visitFrame(Opcodes.F_NEW, 2, new Object[] {"org/axonframework/samples/bank/command/BankAccountCommandHandler2", "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand"}, 0, new Object[] {});
mv.visitInsn(RETURN);
Label l6 = new Label();
mv.visitLabel(l6);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, l0, l6, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceDebitCommand;", null, l0, l6, 1);
mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;", "Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l3, l1, 2);
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
mv.visitLineNumber(100, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load", "(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitVarInsn(ASTORE, 2);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLineNumber(102, l3);
mv.visitVarInsn(ALOAD, 2);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$5");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$5", "<init>", "(Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;Lorg/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
mv.visitLabel(l1);
mv.visitLineNumber(109, l1);
Label l4 = new Label();
mv.visitJumpInsn(GOTO, l4);
mv.visitLabel(l2);
mv.visitFrame(Opcodes.F_NEW, 2, new Object[] {"org/axonframework/samples/bank/command/BankAccountCommandHandler2", "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand"}, 1, new Object[] {"org/axonframework/commandhandling/model/AggregateNotFoundException"});
mv.visitVarInsn(ASTORE, 2);
Label l5 = new Label();
mv.visitLabel(l5);
mv.visitLineNumber(110, l5);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "eventBus", "Lorg/axonframework/eventhandling/EventBus;");
mv.visitInsn(ICONST_1);
mv.visitTypeInsn(ANEWARRAY, "org/axonframework/eventhandling/EventMessage");
mv.visitInsn(DUP);
mv.visitInsn(ICONST_0);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationNotFoundEvent");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand", "getBankTransferId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationNotFoundEvent", "<init>", "(Ljava/lang/String;)V", false);
mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/eventhandling/GenericEventMessage", "asEventMessage", "(Ljava/lang/Object;)Lorg/axonframework/eventhandling/EventMessage;", false);
mv.visitInsn(AASTORE);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/eventhandling/EventBus", "publish", "([Lorg/axonframework/eventhandling/EventMessage;)V", true);
mv.visitLabel(l4);
mv.visitLineNumber(112, l4);
mv.visitFrame(Opcodes.F_NEW, 2, new Object[] {"org/axonframework/samples/bank/command/BankAccountCommandHandler2", "org/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand"}, 0, new Object[] {});
mv.visitInsn(RETURN);
Label l6 = new Label();
mv.visitLabel(l6);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, l0, l6, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankTransferDestinationCreditCommand;", null, l0, l6, 1);
mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;", "Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l3, l1, 2);
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
mv.visitLineNumber(116, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/command/BankAccountCommandHandler2", "repository", "Lorg/axonframework/commandhandling/model/Repository;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand", "getBankAccountId", "()Ljava/lang/String;", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load", "(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
mv.visitVarInsn(ASTORE, 2);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLineNumber(117, l1);
mv.visitVarInsn(ALOAD, 2);
mv.visitTypeInsn(NEW, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$6");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bank/command/BankAccountCommandHandler2$6", "<init>", "(Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand;)V", false);
mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(124, l2);
mv.visitInsn(RETURN);
Label l3 = new Label();
mv.visitLabel(l3);
mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/command/BankAccountCommandHandler2;", null, l0, l3, 0);
mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankTransferSourceReturnMoneyCommand;", null, l0, l3, 1);
mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;", "Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bank/command/BankAccount;>;", l1, l3, 2);
mv.visitMaxs(5, 3);
mv.visitEnd();
}
cw.visitEnd();

return cw.toByteArray();
}
}
