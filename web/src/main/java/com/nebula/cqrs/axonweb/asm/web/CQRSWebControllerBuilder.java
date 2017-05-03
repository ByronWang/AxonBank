package com.nebula.cqrs.axonweb.asm.web;

import java.util.Collection;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.nebula.cqrs.axon.asm.CQRSMakeDomainImplClassVisitor;
import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.core.asm.AsmBuilder;
import com.nebula.cqrs.core.asm.Field;

public class CQRSWebControllerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type typeController, DomainDefinition domainDefinition, Type typeEntry, Collection<Command> commands) throws Exception {
		ClassWriter cw = new ClassWriter(0);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeController.getInternalName(), null, "java/lang/Object", null);

		visitAnnotation(cw, Controller.class);
		visitAnnotation(cw, MessageMapping.class, "/bank-accounts");

		visitDefineField(cw, "commandBus", CommandBus.class);

		define_init(cw, typeController);

		for (Command command : commands) {
			Type typeDto = domainDefinition.typeOf(AsmBuilder.toCamelUpper(command.actionName) + "Dto");
			if (command.ctorMethod) {
				define_action_create(cw, typeController, typeDto, command);
			} else {
				define_action_other(cw, typeController, typeDto, command);
			}
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	private static MethodVisitor define_init(ClassWriter cw, Type typeController) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(CommandBus.class)), null, null);
			visitAnnotation(mv, Autowired.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(45, l0);
			{
				visitInitObject(mv, 0);
				visitPutField(mv, 0, typeController, 1, "commandBus", CommandBus.class);
				visitReturn(mv);
			}
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l4, 0);
			mv.visitLocalVariable("commandBus", Type.getDescriptor(CommandBus.class), null, l0, l4, 1);
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
