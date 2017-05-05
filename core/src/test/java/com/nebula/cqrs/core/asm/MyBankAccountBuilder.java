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

		visitDefine_init(cw, objectType);
		visitDefine_init_withfields(cw, objectType);
		visitDefine_deposit(cw, objectType);
		visitDefine_withdraw(cw, objectType);
		visitDefine_onCreated(cw, objectType);
		visitDefine_onMoneyAdded(cw, objectType);
		visitDefine_onMoneySubtracted(cw, objectType);

		cw.end();

		return classWriter.toByteArray();
	}

	private static void visitDefine_init_withfields(ClassBody cw, Type objectType) {
		{
			MethodCode mc = cw.publicMethod("<init>").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class).begin(38);
			mc.thisInitObject();
			mc.line(39).load(0).load(1).load(2).thisInvokeSpecial("onCreated", String.class, long.class);
			mc.line(40).returnVoid().end();
		}
	}

	private static void visitDefine_deposit(ClassBody cw, Type objectType) {
		{
			MethodCode mv = cw.publicMethod(boolean.class, "deposit").parameter("amount", long.class).begin(44);
			mv.load(0, 1).thisInvokeSpecial("onMoneyAdded", long.class);
			mv.line(45).insn(ICONST_1).returnType(boolean.class).end();
		}
	}

	private static void visitDefine_withdraw(ClassBody cw, Type objectType) {
		{
			MethodCode mv = cw.publicMethod(boolean.class, "withdraw").parameter("amount", long.class).begin(50);

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

	private static void visitDefine_onCreated(ClassBody cw, Type objectType) {
		{
			MethodCode mc = cw.privateMethod("onCreated").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class).begin(97);

			mc.thisPutField(1, "axonBankAccountId", String.class);
			mc.line(98).thisPutField(2, "overdraftLimit", long.class);
			mc.line(99).load(0).insn(LCONST_0).thisPutField("balance", long.class);
			mc.line(100).returnVoid().end();
		}
	}

	private static void visitDefine_onMoneyAdded(ClassBody cw, Type objectType) {
		{
			MethodCode mc = cw.privateMethod("onMoneyAdded").parameter("amount", long.class).begin(104);
			mc.load(0);
			mc.thisGetField("balance", long.class).load(1).insn(LADD);
			mc.thisPutField("balance", long.class);
			mc.line(105).returnVoid().end();
		}
	}

	private static void visitDefine_onMoneySubtracted(ClassBody cw, Type objectType) {
		{
			MethodCode mc = cw.privateMethod("onMoneySubtracted").parameter("amount", long.class).begin(109);
			mc.load(0);
			mc.thisGetField("balance", long.class).load(1).insn(LSUB);
			mc.thisPutField("balance", Type.getType("J"));
			mc.line(110).returnVoid().end();
		}
	}

	private static void visitDefine_init(ClassBody cw, Type objectType) {
		{
			MethodCode mc = cw.privateMethod("<init>").begin(34);
			mc.thisInitObject();
			mc.line(35).returnVoid().end();
		}
	}
}
