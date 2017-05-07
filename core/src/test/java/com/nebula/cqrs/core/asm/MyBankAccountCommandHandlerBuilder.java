package com.nebula.cqrs.core.asm;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MyBankAccountCommandHandlerBuilder implements Opcodes {

	public static byte[] dump() throws Exception {
		Type objectType = Type.getObjectType("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler");

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

		ClassBody cb = new SimpleClassVisitor(cw, objectType);

		cw.visitInnerClass("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerCreate",
		        "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "InnerCreate", 0);

		cw.visitInnerClass("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerDeposit",
		        "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "InnerDeposit", 0);

		cw.visitInnerClass("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerWithdraw",
		        "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "InnerWithdraw", 0);

		cb.field("repository", Repository.class,
		        "Lorg/axonframework/commandhandling/model/Repository<Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;>;");
		cb.field("eventBus", EventBus.class);

		visitDefine_init(cb);
		visitDefine_handle_create(cb);
		visitDefine_handle_deposit(cb);
		visitDefine_handle_withdraw(cb);
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_handle_withdraw(ClassBody cw) {
		Type commandHandlerType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;");
		Type commandType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand;");

		cw.publicMethod("handle").annotation(CommandHandler.class).parameter("command", commandType).code(mv -> {
			Type domainType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;");
			Type callerType = Type.getType("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerWithdraw");
			mv.localVariable("aggregate", Aggregate.class, domainType);

			mv.line(33).get("repository");
			mv.object(1).getProperty("axonBankAccountId", String.class);
			mv.type(Repository.class).invokeInterface(Aggregate.class, "load", String.class);
			mv.store("aggregate");

			mv.line(34).load("aggregate");

			mv.type(callerType).newInstace();
			mv.insn(DUP);
			mv.load(0, 1);
			mv.type(callerType).invokeSpecial("<init>", commandHandlerType, commandType);

			mv.type(Aggregate.class).invokeInterface("execute", Consumer.class);
			mv.line(35).returnVoid();
		});
	}

	private static void visitDefine_handle_deposit(ClassBody cw) {
		Type commandHandlerType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;");
		Type commandType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountDepositCommand;");

		cw.publicMethod("handle").annotation(CommandHandler.class).parameter("command", commandType).code(mv -> {
			Type domainType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;");
			Type callerType = Type.getType("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerDeposit");
			mv.localVariable("aggregate", Aggregate.class, domainType);

			mv.line(27).get("repository");
			mv.object(1).getProperty("axonBankAccountId", String.class);
			mv.type(Repository.class).invokeInterface(Aggregate.class, "load", String.class);
			mv.store("aggregate");

			mv.line(28).load("aggregate");

			mv.type(callerType).newInstace();
			mv.insn(DUP);
			mv.load(0, 1);
			mv.type(callerType).invokeSpecial("<init>", commandHandlerType, commandType);

			mv.type(Aggregate.class).invokeInterface("execute", Consumer.class);
			mv.line(29).returnVoid();
		});

	}

	private static void visitDefine_handle_create(ClassBody cw) {
		Type commandHandlerType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;");
		Type commandType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCreateCommand;");
		Type callerType = Type.getType("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerCreate");

		cw.publicMethod("handle", Exception.class).annotation(CommandHandler.class).parameter("command", commandType).code(mv -> {
			mv.line(22).get("repository");

			mv.type(callerType).newInstace();
			mv.insn(DUP);
			mv.load(0, 1);
			mv.type(callerType).invokeSpecial("<init>", commandHandlerType, commandType);

			mv.type(Repository.class).invokeInterface(Aggregate.class, "newInstance", Callable.class);
			mv.insn(POP);
			mv.line(23).returnVoid();
		});
	}

	private static void visitDefine_init(ClassBody cw) {
		Type domainType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;");

		cw.publicMethod("<init>").parameter("repository", Repository.class, domainType).parameter("eventBus", EventBus.class).code(mv -> {
			mv.line(15).initObject();
			mv.line(16).loadThis().put(1, "repository");
			mv.line(17).loadThis().put(2, "eventBus");
			mv.line(18).returnVoid();
		});
	}
}
