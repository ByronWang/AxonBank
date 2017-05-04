package com.nebula.cqrs.axon.asm;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.core.asm.Field;

public class CQRSCommandHandlerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type handleType, Type domainType, DomainDefinition domainDefinition) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;

		String repositorySignature = "L" + Type.getInternalName(Repository.class) + "<" + domainType.getDescriptor() + ">;";
		Field identifierField = domainDefinition.identifierField;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, handleType.getInternalName(), null, "java/lang/Object", null);

		for (Command command : domainDefinition.commands.values()) {
			Type callerType = Type.getObjectType(handleType.getInternalName() + "$" + "Inner" + command.commandName);
			cw.visitInnerClass(callerType.getInternalName(), handleType.getInternalName(), "Inner" + command.commandName, 0);
		}

		{
			fv = cw.visitField(ACC_PRIVATE, "repository", Type.getDescriptor(Repository.class), repositorySignature, null);
			fv.visitEnd();
		}

		visitDefineField(cw, "eventBus", EventBus.class);

		visitDefine_init(cw, handleType, repositorySignature);

		for (Command command : domainDefinition.commands.values()) {
			Type callerType = Type.getObjectType(handleType.getInternalName() + "$" + "Inner" + command.commandName);
			Type commandType = command.type;
			if (command.ctorMethod) {
				visitDefine_handle_create(cw, handleType, callerType, commandType);
			} else {
				visitDefine_handle_execute(cw, handleType, callerType, commandType, domainType, identifierField);
			}
		}
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_init(ClassWriter cw, Type handleType, String repositorySignature) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Repository.class), Type.getType(EventBus.class)),
					"(" + repositorySignature + Type.getDescriptor(EventBus.class) + ")V", null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(15, l0);
			{
				visitInitObject(mv, 0);

				visitPutField(mv, 0, handleType, 1, "repository", Repository.class);
				visitPutField(mv, 0, handleType, 2, "eventBus", EventBus.class);

				mv.visitInsn(RETURN);
			}
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", handleType.getDescriptor(), null, l0, l4, 0);
			mv.visitLocalVariable("repository", Type.getDescriptor(Repository.class), repositorySignature, l0, l4, 1);
			mv.visitLocalVariable("eventBus", Type.getDescriptor(EventBus.class), null, l0, l4, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
	}

	private static void visitDefine_handle_create(ClassWriter cw, Type handleType, Type callerType, Type commandType) {
		MethodVisitor mv;
		mv = cw.visitMethod(ACC_PUBLIC, "handle", Type.getMethodDescriptor(Type.VOID_TYPE, commandType), null, new String[] { "java/lang/Exception" });
		visitAnnotation(mv, CommandHandler.class);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(22, l0);
		{

			visitGetField(mv, 0, handleType, "repository", Repository.class);// this.repository
			{// new InnerCreate(command)
				visitNewObject(mv, callerType);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				visitInitTypeWithAllFields(mv, callerType, handleType, commandType);
			}
			visitInvokeInterface(mv, Repository.class, Aggregate.class, "newInstance", Callable.class);// this.repository.newInstance(new
																										// InnerCreate(command));
			mv.visitInsn(POP);

			visitReturn(mv);
		}
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", handleType.getDescriptor(), null, l0, l2, 0);
		mv.visitLocalVariable("command", commandType.getDescriptor(), null, l0, l2, 1);
		mv.visitMaxs(5, 2);
		mv.visitEnd();
	}

	private static void visitDefine_handle_execute(ClassWriter cw, Type handleType, Type callerType, Type commandType, Type domainType, Field identifierField) {
		MethodVisitor mv;
		AnnotationVisitor av0;
		mv = cw.visitMethod(ACC_PUBLIC, "handle", Type.getMethodDescriptor(Type.VOID_TYPE, commandType), null, null);
		visitAnnotation(mv, CommandHandler.class);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(27, l0);
		{
			visitPrintObject(mv, 1);

			visitGetField(mv, 0, handleType, "repository", Repository.class);// this.repository
			visitGetProperty(mv, 1, commandType, identifierField);// command.getAxonBankAccountId()
			visitInvokeInterface(mv, Repository.class, Aggregate.class, "load", identifierField.type);// this.repository.load(command.getAxonBankAccountId());
			mv.visitVarInsn(ASTORE, 2);

			mv.visitVarInsn(ALOAD, 2);// bankAccountAggregate
			{// new InnerWithdraw(command)
				visitNewObject(mv, callerType);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				visitInitTypeWithAllFields(mv, callerType, handleType, commandType);
			}
			visitInvokeInterface(mv, Aggregate.class, "execute", Consumer.class);// bankAccountAggregate.execute(new
																					// InnerWithdraw(command));

			visitReturn(mv);
		}
		Label l4 = new Label();
		mv.visitLabel(l4);
		mv.visitLocalVariable("this", handleType.getDescriptor(), null, l0, l4, 0);
		mv.visitLocalVariable("command", commandType.getDescriptor(), null, l0, l4, 1);
		String aggregateSignature = "L" + Type.getInternalName(Aggregate.class) + "<" + domainType.getDescriptor() + ">;";
		mv.visitLocalVariable("aggregate", Type.getDescriptor(Aggregate.class), aggregateSignature, l0, l4, 2);
		mv.visitMaxs(5, 3);
		mv.visitEnd();
	}

	private static void visitDefine_handle_xxx(ClassWriter cw) {
		MethodVisitor mv;
		AnnotationVisitor av0;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "handle", "(Lorg/axonframework/samples/bankx/cqrs/MyBankAccountWithdrawCommand;)V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(34, l0);
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(35, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bankx/cqrs/MyBankAccountCommandHandler", "repository",
					"Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/axonframework/samples/bankx/cqrs/MyBankAccountWithdrawCommand", "getAxonBankAccountId",
					"()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load",
					"(Ljava/lang/String;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitVarInsn(ASTORE, 2);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(36, l2);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(NEW, "org/axonframework/samples/bankx/cqrs/MyBankAccountCommandHandler$InnerWithdraw");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, "org/axonframework/samples/bankx/cqrs/MyBankAccountCommandHandler$InnerWithdraw", "<init>",
					"(Lorg/axonframework/samples/bankx/cqrs/MyBankAccountCommandHandler;Lorg/axonframework/samples/bankx/cqrs/MyBankAccountWithdrawCommand;)V",
					false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(37, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bankx/cqrs/MyBankAccountCommandHandler;", null, l0, l4, 0);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bankx/cqrs/MyBankAccountWithdrawCommand;", null, l0, l4, 1);
			mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
					"Lorg/axonframework/commandhandling/model/Aggregate<Lorg/axonframework/samples/bankx/cqrs/MyBankAccountImpl;>;", l2, l4, 2);
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
	}
}
