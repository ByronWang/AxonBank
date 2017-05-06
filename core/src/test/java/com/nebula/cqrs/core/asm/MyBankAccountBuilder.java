package com.nebula.cqrs.core.asm;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import com.nebula.cqrs.core.CqrsEntity;

public class MyBankAccountBuilder extends AsmBuilderHelper {

	public static byte[] dump() throws Exception {
		Type objectType = Type.getObjectType("org/axonframework/samples/bankcqrs/MyBankAccount");

		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

		ClassBody cw = new WeClassVisitor(classWriter, objectType);
		cw.annotation(Aggregate.class);
		cw.annotation(CqrsEntity.class);

		cw.field("axonBankAccountId", String.class, AggregateIdentifier.class);
		cw.field("overdraftLimit", long.class);
		cw.field("balance", long.class);

		visitDefine_init(cw);
		visitDefine_init_withfields(cw);
		visitDefine_deposit(cw);
		visitDefine_withdraw(cw);
		visitDefine_onCreated(cw);
		visitDefine_onMoneyAdded(cw);
		visitDefine_onMoneySubtracted(cw);

		cw.end();

		return classWriter.toByteArray();
	}

	private static void visitDefine_init_withfields(ClassBody cw) {
		cw.publicMethod("<init>").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class).code(38, mc -> {

			mc.thisInitObject();
			mc.line(39).load(0, 1, 2).thisInvokeSpecial("onCreated", String.class, long.class);
			mc.line(40).returnVoid();
		});
	}

	private static void visitDefine_deposit(ClassBody cw) {
		cw.publicMethod(boolean.class, "deposit").parameter("amount", long.class).code(44, mv -> {
			mv.load(0, 1).thisInvokeSpecial("onMoneyAdded", long.class);
			mv.line(45).insn(ICONST_1).returnType(boolean.class);
		});
	}

	private static void visitDefine_withdraw(ClassBody cw) {
		{
			cw.publicMethod(boolean.class, "withdraw").parameter("amount", long.class).code(50, mv -> {
				mv.load(1);
				mv.visit(mc -> mc.thisGetField("balance", long.class).thisGetField("overdraftLimit", long.class).insn(LADD));
				mv.insn(LCMP);
				Label ifEnd = new Label();
				mv.jumpInsn(IFGT, ifEnd);

				mv.line(51).load(0, 1).thisInvokeSpecial("onMoneySubtracted", long.class);
				mv.line(52).insn(ICONST_1).returnType(boolean.class);

				mv.visit(ifEnd, 54).insn(ICONST_0).returnType(boolean.class);
			});

		}
	}

	private static void visitDefine_onCreated(ClassBody cw) {
		cw.privateMethod("onCreated").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class).code(97, mc -> {
			mc.thisPutField(1, "axonBankAccountId", String.class);
			mc.line(98).thisPutField(2, "overdraftLimit", long.class);
			mc.line(99).load(0).insn(LCONST_0).thisPutField("balance", long.class);
			mc.line(100).returnVoid();
		});
	}

	private static void visitDefine_onMoneyAdded(ClassBody cw) {
		cw.privateMethod("onMoneyAdded").parameter("amount", long.class).code(104, mc -> {
			mc.load(0);
			mc.thisGetField("balance", long.class).load(1).insn(LADD);
			mc.thisPutField("balance", long.class);
			mc.line(105).returnVoid();
		});
	}

	private static void visitDefine_onMoneySubtracted(ClassBody cw) {
		cw.privateMethod("onMoneySubtracted").parameter("amount", long.class).code(109, mc -> {
			mc.load(0);
			mc.thisGetField("balance", long.class).load(1).insn(LSUB);
			mc.thisPutField("balance", long.class);
			mc.line(110).returnVoid();
		});

	}

	private static void visitDefine_init(ClassBody cw) {
		cw.privateMethod("<init>").code(34, mc -> {
			mc.thisInitObject();
			mc.line(35).returnVoid();
		});
	}
}
