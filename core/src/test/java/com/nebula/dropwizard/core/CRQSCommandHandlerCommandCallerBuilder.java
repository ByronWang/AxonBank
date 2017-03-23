package com.nebula.dropwizard.core;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.dropwizard.core.CQRSBuilder.Command;
import com.nebula.dropwizard.core.CQRSBuilder.Field;

public class CRQSCommandHandlerCommandCallerBuilder implements Opcodes {

	public static byte[] dump(Type typeDomain, Type typeHandler, Command command) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;

		Type typeInner = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);

		Type typeCommand = command.type;

		cw.visit(52, ACC_SUPER, typeInner.getInternalName(), "Ljava/lang/Object;Ljava/util/function/Consumer<" + typeDomain.getDescriptor() + ">;",
				"java/lang/Object", new String[] { "java/util/function/Consumer" });

		cw.visitSource(CQRSBuilder.toSimpleName(typeHandler.getClassName()) + ".java", null);

		cw.visitOuterClass(typeHandler.getInternalName(), "handle", Type.getMethodDescriptor(Type.VOID_TYPE, typeCommand));

		cw.visitInnerClass(typeInner.getInternalName(), null, null, 0);

		{
			fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0", typeHandler.getDescriptor(), null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC, "val$command", typeCommand.getDescriptor(), null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, typeHandler, typeCommand), null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, typeInner.getInternalName(), "this$0", typeHandler.getDescriptor());
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, typeInner.getInternalName(), "val$command", typeCommand.getDescriptor());
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(62, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", typeInner.getDescriptor(), null, l0, l2, 0);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "accept", Type.getMethodDescriptor(Type.VOID_TYPE, typeDomain), null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(65, l0);
			mv.visitVarInsn(ALOAD, 1);

			Type[] types = new Type[command.methodParams.length];
			for (int i = 0; i < command.methodParams.length; i++) {
				Field param = command.methodParams[i];
				types[i] = command.methodParams[i].type;

				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, typeInner.getInternalName(), "val$command", typeCommand.getDescriptor());
				mv.visitMethodInsn(INVOKEVIRTUAL, typeCommand.getInternalName(), "get" + CQRSBuilder.toCamelUpper(param.name),
						Type.getMethodDescriptor(param.type), false);
			}

			mv.visitMethodInsn(INVOKEVIRTUAL, typeDomain.getInternalName(), command.methodName, Type.getMethodDescriptor(Type.VOID_TYPE, types), false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(66, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", typeInner.getDescriptor(), null, l0, l2, 0);
			mv.visitLocalVariable(CQRSBuilder.toCamelLower(CQRSBuilder.toSimpleName(typeDomain.getClassName())), typeDomain.getDescriptor(), null, l0, l2, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "accept", "(Ljava/lang/Object;)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, typeDomain.getInternalName());
			mv.visitMethodInsn(INVOKEVIRTUAL, typeInner.getInternalName(), "accept", Type.getMethodDescriptor(Type.VOID_TYPE, typeDomain), false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
