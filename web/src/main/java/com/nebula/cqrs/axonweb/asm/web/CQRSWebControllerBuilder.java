package com.nebula.cqrs.axonweb.asm.web;

import java.io.Serializable;
import java.util.Collection;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.core.asm.AsmBuilderHelper;
import com.nebula.cqrs.core.asm.Field;

public class CQRSWebControllerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type typeController, DomainDefinition domainDefinition, Type typeEntry, Type repoType, Collection<Command> commands)
			throws Exception {
		ClassWriter cw = new ClassWriter(0);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeController.getInternalName(), null, "java/lang/Object", null);

		visitAnnotation(cw, Controller.class);
		visitAnnotation(cw, MessageMapping.class, "/bank-accounts");

		visitDefineField(cw, "commandBus", CommandBus.class);
		visitDefineField(cw, "bankAccountRepository", repoType);

		define_init(cw, repoType, typeController);

		for (Command command : commands) {
			Type typeDto = domainDefinition.typeOf(AsmBuilderHelper.toCamelUpper(command.actionName) + "Dto");
			if (command.ctorMethod) {
				define_action_create(cw, typeController, typeDto, command);
			} else {
				define_action_other(cw, typeController, typeDto, command);
			}
		}
		visitDefine_all(cw, typeController, typeEntry, repoType);
		visitDefine_findOne(cw, typeController, typeEntry, repoType);
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_findOne(ClassWriter cw, Type typeController, Type typeEntry, Type repoType) {
		{
			MethodVisitor mv;
			mv = cw.visitMethod(ACC_PUBLIC, "get", Type.getMethodDescriptor(typeEntry, Type.getType(String.class)), null, null);
			visitAnnotation(mv, SubscribeMapping.class, "/{id}");
			visitParameterAnnotation(mv, 0, DestinationVariable.class);

			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(60, l0);
			{
				visitGetField(mv, 0, typeController, "bankAccountRepository", repoType);

				mv.visitVarInsn(ALOAD, 1);
				visitInvokeInterface(mv, repoType, Object.class, "findOne", Serializable.class);
				mv.visitTypeInsn(CHECKCAST, typeEntry.getInternalName());
				mv.visitInsn(ARETURN);
			}
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l1, 0);
			mv.visitLocalVariable("id", "Ljava/lang/String;", null, l0, l1, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
	}

	private static void visitDefine_all(ClassWriter cw, Type typeController, Type typeEntry, Type repoType) {
		{
			MethodVisitor mv;
			mv = cw.visitMethod(ACC_PUBLIC, "all", "()Ljava/lang/Iterable;", "()Ljava/lang/Iterable<" + typeEntry.getDescriptor() + ">;", null);
			visitAnnotation(mv, SubscribeMapping.class);

			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(55, l0);
			{
				visitGetField(mv, 0, typeController, "bankAccountRepository", repoType);
				visitInvokeInterface(mv, repoType, Iterable.class, "findAllByOrderByIdAsc");
				visitReturnObject(mv);
			}
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
	}

	private static MethodVisitor define_init(ClassWriter cw, Type repoType, Type typeController) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(CommandBus.class), repoType), null, null);
			visitAnnotation(mv, Autowired.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(45, l0);
			{
				visitInitObject(mv, 0);
				visitPutField(mv, 0, typeController, 1, "commandBus", CommandBus.class);
				visitPutField(mv, 0, typeController, 2, "bankAccountRepository", repoType);
				visitReturn(mv);
			}
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l4, 0);
			mv.visitLocalVariable("commandBus", Type.getDescriptor(CommandBus.class), null, l0, l4, 1);
			mv.visitLocalVariable("bankAccountRepository", repoType.getDescriptor(), null, l0, l4, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		return mv;
	}

	private static void define_action_other(ClassWriter cw, Type typeController, Type typeDto, Command command) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, command.actionName, Type.getMethodDescriptor(Type.VOID_TYPE, typeDto), null, null);

			visitAnnotation(mv, SubscribeMapping.class, "/" + command.actionName);

			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(70, l0);

			{
				visitNewObject(mv, command.type);
				mv.visitInsn(DUP);
				for (Field field : command.fields) {
					visitGetProperty(mv, 1, typeDto, field);
				}
				visitInitTypeWithAllFields(mv, command.type, command.fields);
				mv.visitVarInsn(ASTORE, 2);

				visitGetField(mv, 0, typeController, "commandBus", CommandBus.class);
				mv.visitVarInsn(ALOAD, 2);
				visitInvokeStatic(mv, GenericCommandMessage.class, CommandMessage.class, "asCommandMessage", Object.class);

				visitInvokeInterface(mv, CommandBus.class, "dispatch", CommandMessage.class);
				visitReturn(mv);
			}
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l3, 0);
			mv.visitLocalVariable("dto", typeDto.getDescriptor(), null, l0, l3, 1);
			mv.visitLocalVariable("command", command.type.getDescriptor(), null, l0, l3, 2);
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
	}

	private static MethodVisitor define_action_create(ClassWriter cw, Type typeController, Type typeDto, Command command) {
		MethodVisitor mv;
		{
			Field idField = command.fields[0];
			mv = cw.visitMethod(ACC_PUBLIC, command.actionName, Type.getMethodDescriptor(Type.VOID_TYPE, typeDto), null, null);
			visitAnnotation(mv, SubscribeMapping.class, "/" + command.actionName);

			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(63, l0);

			{
				mv.visitMethodInsn(INVOKESTATIC, "java/util/UUID", "randomUUID", "()Ljava/util/UUID;", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/UUID", "toString", "()Ljava/lang/String;", false);
				mv.visitVarInsn(ASTORE, 2);

				visitNewObject(mv, command.type);

				mv.visitInsn(DUP);

				mv.visitVarInsn(ALOAD, 2); // load key

				for (Field field : command.fields) {
					if (!field.identifier) visitGetProperty(mv, 1, typeDto, field);
				}

				visitInitTypeWithAllFields(mv, command.type, command.fields);

				mv.visitVarInsn(ASTORE, 3);

				visitGetField(mv, 0, typeController, "commandBus", CommandBus.class);

				mv.visitVarInsn(ALOAD, 3);

				visitInvokeStatic(mv, GenericCommandMessage.class, CommandMessage.class, "asCommandMessage", Object.class);
				visitInvokeInterface(mv, CommandBus.class, "dispatch", CommandMessage.class);

				mv.visitInsn(RETURN);
			}
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l4, 0);
			mv.visitLocalVariable("dto", typeDto.getDescriptor(), null, l0, l4, 1);
			mv.visitLocalVariable(idField.name, idField.type.getDescriptor(), null, l0, l4, 2);
			mv.visitLocalVariable("command", command.type.getDescriptor(), null, l0, l4, 3);
			mv.visitMaxs(5, 4);
			mv.visitEnd();
		}
		return mv;
	}

}
