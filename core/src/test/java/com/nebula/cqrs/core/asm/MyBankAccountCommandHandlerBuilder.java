package com.nebula.cqrs.core.asm;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
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
		visitDefine_handle_withdraw(cw);
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_handle_withdraw(ClassVisitor cw) {
		MethodVisitor mv;
		AnnotationVisitor av0;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(36, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler", "repository",
			        "Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand", "getAxonBankAccountId",
			        "()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load",
			        "(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerWithdraw");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerWithdraw", "<init>",
			        "(Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand;)V",
			        false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(37, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;", null, l0, l3, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountWithdrawCommand;", null, l0, l3, 1);
			mv.visitLocalVariable("aggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
			        "Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;>;", l1, l3,
			        2);
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
	}

	private static void visitDefine_handle_deposit(ClassBody cw) {
		Type commandHandlerType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;");
		Type commandType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountDepositCommand;");

		cw.publicMethod("handle", Exception.class).annotation(CommandHandler.class).parameter("command", commandType).code(mv -> {
			Type domainType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;");
			Type callerType = Type.getType("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerDeposit");
			mv.localVariable("aggregate", Aggregate.class, domainType);
			
			mv.line(30).get("repository");
			mv.object(1).getProperty("axonBankAccountId", String.class);
			mv.type(Repository.class).invokeInterface(Aggregate.class, "load", String.class);
			mv.store(2);

			mv.load(2);				

			mv.type(callerType).newInstace();
			mv.insn(DUP);
			mv.load(0, 1);
			mv.type(callerType).invokeSpecial("<init>", commandHandlerType, commandType);
			
			mv.type(Aggregate.class).invokeInterface("execute", Consumer.class);
			mv.line(31).returnVoid();
		});

	}

	private static void visitDefine_handle_create(ClassBody cw) {
		Type commandHandlerType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler;");
		Type commandType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCreateCommand;");
		Type callerType = Type.getType("org/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountCommandHandler$InnerCreate");

		cw.publicMethod("handle", Exception.class).annotation(CommandHandler.class).parameter("command", commandType).code(mv -> {
			mv.line(24).get("repository");
			
			mv.type(callerType).newInstace();
			mv.insn(DUP);
			mv.load(0, 1);
			mv.type(callerType).invokeSpecial("<init>", commandHandlerType, commandType);
			
			mv.type(Repository.class).invokeInterface(Aggregate.class, "newInstance", Callable.class);
			mv.insn(POP);
			mv.line(25).returnVoid();
		});
	}

	private static void visitDefine_init(ClassBody cw) {
		Type domainType = Type.getType("Lorg/axonframework/samples/bankcqrssrc/generatedsources/MyBankAccountImpl;");

		cw.publicMethod("<init>").parameter("repository", Repository.class, domainType).parameter("eventBus", EventBus.class).code(mv -> {
			mv.line(15).initObject();
			mv.line(17).loadThis().put(1, "repository");
			mv.loadThis().put(2, "eventBus");
			mv.line(18).returnVoid();

		});

	}
}
