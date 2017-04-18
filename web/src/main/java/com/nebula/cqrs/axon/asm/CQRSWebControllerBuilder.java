package com.nebula.cqrs.axon.asm;

import org.axonframework.commandhandling.CommandBus;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.Field;

public class CQRSWebControllerBuilder extends AsmBuilder {

	public static byte[] dump(Type typeDomain, Type typeController, Type typeRepository, Type typeEntry, CQRSDomainBuilder cqrs) throws Exception {
		Type typeCommandBus = Type.getType(CommandBus.class);

		ClassWriter cw = new ClassWriter(0);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeController.getInternalName(), null, "java/lang/Object", null);

		annotations(cw, Controller.class);

		annotation(cw, MessageMapping.class, "/bank-accounts");

		define_field(cw, typeCommandBus, "commandBus");
		define_field(cw, typeRepository, "repository");

		define_init(cw, typeController, typeRepository, typeCommandBus);

		for (Command command : cqrs.commands) {
			if (command.ctorMethod) define_action_create(typeDomain, typeController, cqrs, typeCommandBus, cw, command);
			else
				define_action_other(typeDomain, typeController, typeCommandBus, cw, command);
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void define_action_other(Type typeDomain, Type typeController, Type typeCommandBus, ClassWriter cw, Command command) {
		MethodVisitor mv;
		{
			Type typeDto = Type.getObjectType(typeDomain.getInternalName() + CQRSDomainBuilder.toCamelUpper(command.actionName) + "Dto");

			mv = cw.visitMethod(ACC_PUBLIC, command.actionName, Type.getMethodDescriptor(Type.VOID_TYPE, typeDto), null, null);

			annotation(cw, SubscribeMapping.class, "/" + command.actionName);

			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(70, l0);
			mv.visitTypeInsn(NEW, command.type.getInternalName());
			mv.visitInsn(DUP);

			for (Field field : command.fields) {
				invoke_getField(mv, 1, typeDto, field);
			}

			INVOKE_init_typeWithAllfield(mv, command.type, command.fields);

			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(71, l1);

			GET_FIELD(mv, typeController, 0, CommandBus.class, "commandBus");

			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/GenericCommandMessage", "asCommandMessage",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/CommandMessage;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, typeCommandBus.getInternalName(), "dispatch", "(Lorg/axonframework/commandhandling/CommandMessage;)V", true);
			
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(72, l2);
			
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l3, 0);
			mv.visitLocalVariable("dto", typeDto.getDescriptor(), null, l0, l3, 1);
			mv.visitLocalVariable("command", command.type.getDescriptor(), null, l1, l3, 2);
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
	}

	private static MethodVisitor define_action_create(Type typeDomain, Type typeController, CQRSDomainBuilder cqrs, Type typeCommandBus, ClassWriter cw,
			Command command) {
		MethodVisitor mv;
		{
			Type typeDto = Type.getObjectType(typeDomain.getInternalName() + CQRSDomainBuilder.toCamelUpper(command.actionName) + "Dto");

			mv = cw.visitMethod(ACC_PUBLIC, command.actionName, Type.getMethodDescriptor(Type.VOID_TYPE, typeDto), null, null);

			annotation(cw, SubscribeMapping.class, "/" + command.actionName);

			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(63, l0);
			mv.visitMethodInsn(INVOKESTATIC, "java/util/UUID", "randomUUID", "()Ljava/util/UUID;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/UUID", "toString", "()Ljava/lang/String;", false);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(64, l1);
			mv.visitTypeInsn(NEW, command.type.getInternalName());
			mv.visitInsn(DUP);

			mv.visitVarInsn(ALOAD, 2); // load key

			for (Field field : command.fields) {
				if (!field.idField) invoke_getField(mv, 1, typeDto, field);
			}
			
			INVOKE_init_typeWithAllfield(mv, command.type, command.fields);

			mv.visitVarInsn(ASTORE, 3);
			
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(65, l2);

			GET_FIELD(mv, typeController, 0, CommandBus.class, "commandBus");

			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/GenericCommandMessage", "asCommandMessage",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/CommandMessage;", false);
			mv.visitMethodInsn(INVOKEINTERFACE, typeCommandBus.getInternalName(), "dispatch", "(Lorg/axonframework/commandhandling/CommandMessage;)V", true);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(66, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l4, 0);
			mv.visitLocalVariable("dto", typeDto.getDescriptor(), null, l0, l4, 1);
			mv.visitLocalVariable(cqrs.newfieldID.name, cqrs.newfieldID.type.getDescriptor(), null, l1, l4, 2);
			mv.visitLocalVariable("command", command.type.getDescriptor(), null, l2, l4, 3);
			mv.visitMaxs(5, 4);
			mv.visitEnd();
		}
		return mv;
	}

	private static MethodVisitor define_init(ClassWriter cw, Type typeController, Type typeRepository, Type typeCommandBus) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, typeCommandBus), null, null);
			annotations(cw, Autowired.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(45, l0);

			INVOKE_init_Object(mv,0);

			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(47, l1);

			PUT_FIELD(mv, typeController, 0, typeCommandBus, "commandBus", 1);

			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(49, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l4, 0);
			mv.visitLocalVariable("commandBus", typeCommandBus.getDescriptor(), null, l0, l4, 1);
			mv.visitLocalVariable("repository", typeRepository.getDescriptor(), null, l0, l4, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		return mv;
	}

}
