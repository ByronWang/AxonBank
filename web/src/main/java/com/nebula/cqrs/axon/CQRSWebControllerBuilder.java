package com.nebula.cqrs.axon;

import org.axonframework.commandhandling.CommandBus;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Command;
import com.nebula.cqrs.axon.CQRSDomainBuilder.Field;

public class CQRSWebControllerBuilder implements Opcodes {

	public static byte[] dump(Type typeDomain, Type typeController, Type typeRepository, Type typeEntry, CQRSDomainBuilder cqrs) throws Exception {
		Type typeCommandBus = Type.getType(CommandBus.class);

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeController.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(CQRSDomainBuilder.toSimpleName(typeController.getClassName()), null);

		{
			av0 = cw.visitAnnotation(Type.getDescriptor(Controller.class), true);
			av0.visitEnd();
		}
		{
			av0 = cw.visitAnnotation(Type.getDescriptor(MessageMapping.class), true);
			{
				AnnotationVisitor av1 = av0.visitArray("value");
				av1.visit(null, "/bank-accounts");
				av1.visitEnd();
			}
			av0.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "commandBus", typeCommandBus.getDescriptor(), null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "repository", typeRepository.getDescriptor(), null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, typeCommandBus, typeRepository), null, null);
			{
				av0 = mv.visitAnnotation(Type.getDescriptor(Autowired.class), true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(45, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(47, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, typeController.getInternalName(), "commandBus", typeCommandBus.getDescriptor());
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(48, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, typeController.getInternalName(), "repository", typeRepository.getDescriptor());
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
		{
			mv = cw.visitMethod(ACC_PUBLIC, "all", "()Ljava/lang/Iterable;", "()Ljava/lang/Iterable<" + typeEntry.getDescriptor() + ">;", null);
			{
				av0 = mv.visitAnnotation(Type.getDescriptor(SubscribeMapping.class), true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(53, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeController.getInternalName(), "repository", typeRepository.getDescriptor());
			mv.visitMethodInsn(INVOKEINTERFACE, typeRepository.getInternalName(), "findAllByOrderByIdAsc", "()Ljava/lang/Iterable;", true);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "get", Type.getMethodDescriptor(typeEntry, Type.getType(String.class)), null, null);
			{
				av0 = mv.visitAnnotation(Type.getDescriptor(SubscribeMapping.class), true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					av1.visit(null, "/{id}");
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			{
				av0 = mv.visitParameterAnnotation(0, Type.getDescriptor(DestinationVariable.class), true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(58, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeController.getInternalName(), "repository", typeRepository.getDescriptor());
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEINTERFACE, typeRepository.getInternalName(), "findOne", "(Ljava/io/Serializable;)Ljava/lang/Object;", true);
			mv.visitTypeInsn(CHECKCAST, typeEntry.getInternalName());
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeController.getDescriptor(), null, l0, l1, 0);
			mv.visitLocalVariable("id", "Ljava/lang/String;", null, l0, l1, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}

		for (Command command : cqrs.commands) {
			if (!command.ctorMethod) continue;

			Type typeDto = Type.getObjectType(typeDomain.getInternalName() + CQRSDomainBuilder.toCamelUpper(command.actionName) + "Dto");

			mv = cw.visitMethod(ACC_PUBLIC, command.actionName, "(Lorg/axonframework/samples/bank/web/dto/BankAccountDto;)V", null, null);
			{
				av0 = mv.visitAnnotation(Type.getDescriptor(SubscribeMapping.class), true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					av1.visit(null, "/" + command.actionName);
					av1.visitEnd();
				}
				av0.visitEnd();
			}
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

			mv.visitVarInsn(ALOAD, 2);
			for (Field field : command.fields) {
				if (!field.idField) AsmBuilder.visitInvoke_getField(mv, 1, typeDto, field);
			}
			AsmBuilder.visitInvoke_init(mv, command.type, command.fields);

			mv.visitVarInsn(ASTORE, 3);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(65, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeController.getInternalName(), "commandBus", "Lorg/axonframework/commandhandling/CommandBus;");
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
			mv.visitLocalVariable("dto", "Lorg/axonframework/samples/bank/web/dto/BankAccountDto;", null, l0, l4, 1);
			mv.visitLocalVariable("id", "Ljava/lang/String;", null, l1, l4, 2);
			mv.visitLocalVariable("command", "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountCreateCommand;", null, l2, l4, 3);
			mv.visitMaxs(5, 4);
			mv.visitEnd();
		}

		for (Command command : cqrs.commands) {
			if (command.ctorMethod) continue;

			Type typeDto = Type.getObjectType(typeDomain.getInternalName() + CQRSDomainBuilder.toCamelUpper(command.actionName) + "Dto");

			mv = cw.visitMethod(ACC_PUBLIC, command.actionName, Type.getMethodDescriptor(Type.VOID_TYPE, typeDto), null, null);
			{
				av0 = mv.visitAnnotation(Type.getDescriptor(SubscribeMapping.class), true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					av1.visit(null, "/" + command.actionName);
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(70, l0);
			mv.visitTypeInsn(NEW, command.type.getInternalName());
			mv.visitInsn(DUP);

			for (Field field : command.fields) {
				AsmBuilder.visitInvoke_getField(mv, 1, typeDto, field);
			}

			AsmBuilder.visitInvoke_init(mv, command.type, command.fields);

			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(71, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeController.getInternalName(), "commandBus", "Lorg/axonframework/commandhandling/CommandBus;");
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
		cw.visitEnd();

		return cw.toByteArray();
	}

}
