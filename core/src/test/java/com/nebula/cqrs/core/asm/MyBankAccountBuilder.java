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

		ClassBody cw = new SimpleClassVisitor(classWriter, objectType);
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
		cw.publicMethod("<init>").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class).code(mc -> {
			mc.line(38).initObject();
			mc.line(39).load("this", "axonBankAccountId", "overdraftLimit");
			mc.thisType().invokeSpecial("onCreated", String.class, long.class);
			mc.line(40).returnVoid();
		});
	}

	private static void visitDefine_deposit(ClassBody cw) {
		cw.publicMethod(boolean.class, "deposit").parameter("amount", long.class).code(mv -> {
			mv.line(44).load("this", "amount");
			mv.thisType().invokeSpecial("onMoneyAdded", long.class);
			mv.line(45).insn(ICONST_1).returnType(boolean.class);
		});
	}

	private static void visitDefine_withdraw(ClassBody cw) {
		{
			cw.publicMethod(boolean.class, "withdraw").parameter("amount", long.class).code(mc -> {
				mc.line(50).load("amount");
				mc.block(v -> {
				    v.get("balance");
				    v.get("overdraftLimit");
				    v.insn(LADD);
			    });
				mc.insn(LCMP);
				Label ifEnd = mc.defineLabel();
				mc.jumpInsn(IFGT, ifEnd);

				mc.line(51).load("this", "amount");
				mc.thisType().invokeSpecial("onMoneySubtracted", long.class);
				mc.line(52).insn(ICONST_1).returnType(boolean.class);

				mc.accessLabel(ifEnd, 54);
				mc.insn(ICONST_0).returnType(boolean.class);
			});
		}
	}

	private static void visitDefine_onCreated(ClassBody cw) {
		cw.privateMethod("onCreated").parameter("axonBankAccountId", String.class).parameter("overdraftLimit", long.class).code(mc -> {
			mc.line(97).put("axonBankAccountId", "axonBankAccountId");
			mc.line(98).put("overdraftLimit", "overdraftLimit");
			mc.line(99).loadThis();
			mc.insn(LCONST_0);
			mc.putTopTo("balance");
			mc.line(100).returnVoid();
		});
	}

	private static void visitDefine_onMoneyAdded(ClassBody cw) {
		cw.privateMethod("onMoneyAdded").parameter("amount", long.class).code(mc -> {
			mc.line(104).loadThis();
			mc.get("balance");
			mc.load("amount");
			mc.insn(LADD);
			mc.putTopTo("balance");
			mc.line(105).returnVoid();
		});
	}

	private static void visitDefine_onMoneySubtracted(ClassBody cw) {
		cw.privateMethod("onMoneySubtracted").parameter("amount", long.class).code(mc -> {
			mc.line(109).loadThis();
			mc.get("balance");
			mc.load(1);
			mc.insn(LSUB);
			mc.putTopTo("balance");
			mc.line(110).returnVoid();
		});

	}

	private static void visitDefine_init(ClassBody cw) {
		cw.privateMethod("<init>").code(mc -> {
			mc.line(34).initObject();
			mc.line(35).returnVoid();
		});
	}
}
