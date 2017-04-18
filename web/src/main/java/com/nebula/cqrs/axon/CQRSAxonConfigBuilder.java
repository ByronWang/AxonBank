package com.nebula.cqrs.axon;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.spring.config.AxonConfiguration;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class CQRSAxonConfigBuilder implements Opcodes {

	public static byte[] dump(Type typeDomain, Type typeConfig, Type typeCommandHandler) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeConfig.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource("AxonConfig.java", null);

		{
			av0 = cw.visitAnnotation(Type.getDescriptor(Configuration.class), true);
			av0.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "axonConfiguration", Type.getDescriptor(AxonConfiguration.class), null, null);
			{
				av0 = fv.visitAnnotation(Type.getDescriptor(Autowired.class), true);
				av0.visitEnd();
			}
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "eventBus", Type.getDescriptor(EventBus.class), null, null);
			{
				av0 = fv.visitAnnotation(Type.getDescriptor(Autowired.class), true);
				av0.visitEnd();
			}
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(28, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeConfig.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "bankAccountCommandHandler", Type.getMethodDescriptor(typeCommandHandler), null, null);
			{
				av0 = mv.visitAnnotation(Type.getDescriptor(Bean.class), true);
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(37, l0);
			mv.visitTypeInsn(NEW, typeCommandHandler.getInternalName());
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeConfig.getInternalName(), "axonConfiguration", Type.getDescriptor(AxonConfiguration.class));
			mv.visitLdcInsn(typeDomain);
			mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(AxonConfiguration.class), "repository",
					Type.getMethodDescriptor(Type.getType(Repository.class), Type.getType(Class.class)), false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeConfig.getInternalName(), "eventBus", Type.getDescriptor(EventBus.class));
			mv.visitMethodInsn(INVOKESPECIAL, typeCommandHandler.getInternalName(), "<init>",
					Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Repository.class), Type.getType(EventBus.class)), false);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeConfig.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(4, 1);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
