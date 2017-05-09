package com.nebula.cqrs.core.asm;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import com.nebula.cqrs.core.CqrsEntity;
import com.nebula.cqrs.core.asm.wrap.ClassBody;
import com.nebula.cqrs.core.asm.wrap.SimpleClassVisitor;

public class MyBankAccountBuilder extends AsmBuilderHelper {

	public static byte[] dump() throws Exception {
		Type objectType = Type.getObjectType("com/nebula/cqrs/core/asm/MyBankAccount");

		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

		ClassBody cw = new SimpleClassVisitor(classWriter, objectType);
		cw.annotation(CqrsEntity.class);
		cw.annotation(Aggregate.class);

		cw.field(AggregateIdentifier.class, "axonBankAccountId", String.class);
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
		cw.publicMethod("<init>").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class).code(mc -> {
			mc.line(38).initObject();
			mc.line(39).use("this", "axonBankAccountId", "overdraftLimit").invokeSpecial("onCreated", String.class, long.class);
			mc.line(40).returnVoid();
		});
	}

	private static void visitDefine_deposit(ClassBody cw) {
		cw.publicMethod(boolean.class, "deposit").parameter("amount", long.class).code(mv -> {
			mv.line(44).use("this", "amount").invokeSpecial("onMoneyAdded", long.class);
			mv.line(45).insn(ICONST_1).returnTop(boolean.class);
		});
	}

	private static void visitDefine_withdraw(ClassBody cw) {
		{
			cw.publicMethod(boolean.class, "withdraw").parameter("amount", long.class).code(mc -> {
				mc.line(50).load("amount");

				mc.loadThis().get("balance");
				mc.loadThis().get("overdraftLimit");
				mc.insn(LADD);

				mc.insn(LCMP);
				Label ifEnd = mc.defineLabel();
				mc.jumpInsn(IFGT, ifEnd);

				mc.line(51).use("this", "amount").invokeSpecial("onMoneySubtracted", long.class);
				mc.line(52).insn(ICONST_1).returnTop(boolean.class);

				mc.accessLabel(ifEnd, 54);
				mc.insn(ICONST_0).returnTop(boolean.class);
			});
		}
	}

	private static void visitDefine_onCreated(ClassBody cw) {
		cw.privateMethod("onCreated").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class).code(mc -> {
			mc.line(100).loadThis().put("axonBankAccountId", "axonBankAccountId");
			mc.line(101).loadThis().put("overdraftLimit", "overdraftLimit");
			mc.line(102).use("this").with(e -> e.insn(LCONST_0)).putTo("balance");
			mc.line(103).returnVoid();
		});
	}

	private static void visitDefine_onMoneyAdded(ClassBody cw) {
		cw.privateMethod("onMoneyAdded").parameter("amount", long.class).code(mc -> {
			mc.def("newbalance", mc.fieldOf("balance").type);
			mc.line(107).loadThis().get("balance");
			mc.load("amount");
			mc.insn(LADD);
			mc.storeTop("newbalance");
			mc.line(108).loadThis().put("newbalance", "balance");
			mc.line(109).returnVoid();
		});
	}

	private static void visitDefine_onMoneySubtracted(ClassBody cw) {
		cw.privateMethod("onMoneySubtracted").parameter("amount", long.class).code(mc -> {
			mc.line(113).load("this");
			mc.insn(DUP);
			mc.useTopThis().get("balance");
			mc.load("amount");
			mc.insn(LSUB);
			mc.useTopThis().putTo("balance");
			mc.line(114).returnVoid();
		});
	}

	private static void visitDefine_init(ClassBody cw) {
		cw.privateMethod("<init>").code(mc -> {
			mc.line(34).initObject();
			mc.line(35).returnVoid();
		});
	}
}
