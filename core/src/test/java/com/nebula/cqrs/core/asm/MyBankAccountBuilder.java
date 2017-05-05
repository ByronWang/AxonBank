package com.nebula.cqrs.core.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

public class MyBankAccountBuilder extends AsmBuilderHelper {

    public static byte[] dump() throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        AnnotationVisitor av0;

        Type objectType = Type.getObjectType("org/axonframework/samples/bankcqrs/MyBankAccount");
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
        visitDefine_init(cw, objectType);
        visitDefine_init_withfields(cw, objectType);
        visitDefine_deposit(cw, objectType);
        visitDefine_withdraw(cw, objectType);
        visitDefine_onCreated(cw, objectType);
        visitDefine_onMoneyAdded(cw, objectType);
        visitDefine_onMoneySubtracted(cw, objectType);
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static void visitDefine_init_withfields(ClassWriter cw, Type objectType) {
        {
            MethodCode mc = definePublic(cw, objectType, "<init>").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class)
                    .begin(38);
            mc.thisInitObject();
            mc.line(39).load(0).load(1).load(2).thisInvokeSpecial("onCreated", String.class, long.class);
            mc.line(40).returnVoid().end();
        }
    }

    private static void visitDefine_deposit(ClassWriter cw, Type objectType) {
        {
            MethodCode mv = definePublic(cw, objectType, boolean.class, "deposit").parameter("amount", long.class).begin(44);
            mv.load(0, 1).thisInvokeSpecial("onMoneyAdded", long.class);
            mv.line(45).insn(ICONST_1).returnType(boolean.class).end();
        }
    }

    private static void visitDefine_withdraw(ClassWriter cw, Type objectType) {
        {
            MethodCode mv = definePublic(cw, objectType, boolean.class, "withdraw").parameter("amount", long.class).begin(50);

            mv.load(1);
            mv.thisGetField("balance", long.class);
            mv.thisGetField("overdraftLimit", long.class);
            mv.insn(LADD);
            mv.insn(LCMP);
            Label ifEnd = new Label();
            mv.jumpInsn(IFGT, ifEnd);

            mv.line(51).load(0, 1).thisInvokeSpecial("onMoneySubtracted", long.class);
            mv.line(52).insn(ICONST_1).returnType(boolean.class);

            mv.visit(ifEnd, 54).insn(ICONST_0).returnType(boolean.class);

            mv.end();
        }
    }

    private static void visitDefine_onCreated(ClassWriter cw, Type objectType) {
        {
            MethodCode mc = definePrivate(cw, objectType, "onCreated").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class)
                    .begin(97);

            mc.thisPutField(1, "axonBankAccountId", String.class);
            mc.line(98).thisPutField(2, "overdraftLimit", long.class);
            mc.line(99).load(0).insn(LCONST_0).thisPutField("balance", long.class);
            mc.line(100).returnVoid().end();
        }
    }

    private static void visitDefine_onMoneyAdded(ClassWriter cw, Type objectType) {
        {
            MethodCode mc = definePrivate(cw, objectType, "onMoneyAdded").parameter("amount", long.class).begin(104);
            mc.load(0);
            mc.thisGetField("balance", long.class).load(1).insn(LADD);
            mc.thisPutField("balance", long.class);
            mc.line(105).returnVoid().end();
        }
    }

    private static void visitDefine_onMoneySubtracted(ClassWriter cw, Type objectType) {
        {
            MethodCode mc = definePrivate(cw, objectType, "onMoneySubtracted").parameter("amount", long.class).begin(109);
            mc.load(0);
            mc.thisGetField("balance", long.class).load(1).insn(LSUB);
            mc.thisPutField("balance", Type.getType("J"));
            mc.line(110).returnVoid().end();
        }
    }

    private static void visitDefine_init(ClassWriter cw, Type objectType) {
        {
            MethodCode mc = definePrivate(cw, objectType, "<init>").begin(34);
            mc.thisInitObject();
            mc.line(35).returnVoid().end();
        }
    }
}
