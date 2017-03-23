package com.nebula.dropwizard.core;

import java.util.*;

import org.axonframework.spring.stereotype.Aggregate;
import org.objectweb.asm.*;

import com.nebula.dropwizard.core.CQRSDomainBuilder.Command;
import com.nebula.dropwizard.core.CQRSDomainBuilder.Field;

public class CQRSCommandHandlerBuilder implements Opcodes {

	public byte[] dump(List<Command> commands, Type typeDomain, Type typeHandler) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		FieldVisitor fv;
		MethodVisitor mv;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeHandler.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(CQRSDomainBuilder.toSimpleName(typeHandler.getClassName()) + ".java", null);

		for (Command command : commands) {
			if (!command.ctorMethod) {
				Type inner = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
				cw.visitInnerClass(inner.getInternalName(), null, null, 0);
			}
		}

		{
			fv = cw.visitField(ACC_PRIVATE, "repository", "Lorg/axonframework/commandhandling/model/Repository;",
					"Lorg/axonframework/commandhandling/model/Repository<" + typeDomain.getDescriptor() + ">;", null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/axonframework/commandhandling/model/Repository;Lorg/axonframework/eventhandling/EventBus;)V",
					"(Lorg/axonframework/commandhandling/model/Repository<" + typeDomain.getDescriptor() + ">;Lorg/axonframework/eventhandling/EventBus;)V",
					null);
			mv.visitParameter("repository", 0);
			mv.visitParameter("eventBus", 0);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(43, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(44, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, typeHandler.getInternalName(), "repository", "Lorg/axonframework/commandhandling/model/Repository;");
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(45, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, typeHandler.getInternalName(), "eventBus", "Lorg/axonframework/eventhandling/EventBus;");
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(46, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", typeHandler.getDescriptor(), null, l0, l4, 0);
			mv.visitLocalVariable("repository", "Lorg/axonframework/commandhandling/model/Repository;",
					"Lorg/axonframework/commandhandling/model/Repository<" + typeDomain.getDescriptor() + ">;", l0, l4, 1);
			mv.visitLocalVariable("eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, l0, l4, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}

		for (Command command : commands) {
			if (command.ctorMethod) {
				dumpCtorMethod(cw, typeDomain, typeHandler, command);
			} else {
				dumpMethod(cw, typeDomain, typeHandler, command);
			}
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	void dumpCtorMethod(ClassWriter cw, Type typeDomain, Type typeHandler, Command command) {
		MethodVisitor mv;
		AnnotationVisitor av0;

		Type typeInner = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
		{

			mv = cw.visitMethod(ACC_PUBLIC, "handle", Type.getMethodDescriptor(Type.VOID_TYPE, command.type), null, new String[] { "java/lang/Exception" });
			mv.visitParameter("command", 0);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(50, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeHandler.getInternalName(), "repository", "Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitTypeInsn(NEW, typeInner.getInternalName());
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, typeInner.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, typeHandler, command.type),
					false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "newInstance",
					"(Ljava/util/concurrent/Callable;)Lorg/axonframework/commandhandling/model/Aggregate;", true);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(57, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", typeHandler.getDescriptor(), null, l0, l2, 0);
			mv.visitLocalVariable("command", command.type.getDescriptor(), null, l0, l2, 1);
			mv.visitMaxs(5, 2);
			mv.visitEnd();
		}
	}

	void dumpMethod(ClassWriter cw, Type typeDomain, Type typeHandler, Command command) {
		MethodVisitor mv;
		AnnotationVisitor av0;
		{

			Type inner = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);

			mv = cw.visitMethod(ACC_PUBLIC, "handle", Type.getMethodDescriptor(Type.VOID_TYPE, command.type), null, null);
			mv.visitParameter("command", 0);
			{
				av0 = mv.visitAnnotation("Lorg/axonframework/commandhandling/CommandHandler;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(61, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeHandler.getInternalName(), "repository", "Lorg/axonframework/commandhandling/model/Repository;");
			mv.visitVarInsn(ALOAD, 1);

			Field idField = command.fields.get(0);
			mv.visitMethodInsn(INVOKEVIRTUAL, command.type.getInternalName(), "get" + CQRSDomainBuilder.toCamelUpper(idField.name), Type.getMethodDescriptor(idField.type),
					false);

			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Repository", "load",
					Type.getMethodDescriptor(Type.getType(Aggregate.class), idField.type), true);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(62, l1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(NEW, inner.getInternalName());
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, inner.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, typeHandler, command.type), false);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/axonframework/commandhandling/model/Aggregate", "execute", "(Ljava/util/function/Consumer;)V", true);

			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(68, l2);
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", typeHandler.getDescriptor(), null, l0, l3, 0);
			mv.visitLocalVariable("command", command.type.getDescriptor(), null, l0, l3, 1);
			mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
					"Lorg/axonframework/commandhandling/model/Aggregate<" + typeDomain.getDescriptor() + ">;", l1, l3, 2);
			mv.visitMaxs(5, 3);
			mv.visitEnd();

		}
	}
}
