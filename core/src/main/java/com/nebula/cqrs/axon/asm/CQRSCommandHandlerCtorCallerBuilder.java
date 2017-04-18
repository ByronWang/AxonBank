package com.nebula.cqrs.axon.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.Field;

public class CQRSCommandHandlerCtorCallerBuilder extends AsmBuilder {

	public static byte[] dump(Type typeDomain, Type typeHandler, Command command) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		FieldVisitor fv;
		MethodVisitor mv;

		Type typeInner = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);

		Type typeCommand = command.type;

		cw.visit(52, ACC_SUPER, typeInner.getInternalName(), "Ljava/lang/Object;Ljava/util/concurrent/Callable<" + typeDomain.getDescriptor() + ">;",
				"java/lang/Object", new String[] { "java/util/concurrent/Callable" });

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
			mv = cw.visitMethod(ACC_PUBLIC, "call", Type.getMethodDescriptor(typeDomain), null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(65, l0);
			mv.visitTypeInsn(NEW, typeDomain.getInternalName());
			mv.visitInsn(DUP);

			Type[] types = new Type[command.methodParams.length];
			for (int i = 0; i < command.methodParams.length; i++) {
				Field param = command.methodParams[i];
				types[i] = command.methodParams[i].type;

				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, typeInner.getInternalName(), "val$command", typeCommand.getDescriptor());
				mv.visitMethodInsn(INVOKEVIRTUAL, typeCommand.getInternalName(), "get" + CQRSDomainBuilder.toCamelUpper(param.name),
						Type.getMethodDescriptor(param.type), false);
			}

			mv.visitMethodInsn(INVOKESPECIAL, typeDomain.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, types), false);

			printStaticMessage(mv, typeCommand.getInternalName() + " create new object");

			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeInner.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(5, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "call", "()Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, typeInner.getInternalName(), "call", Type.getMethodDescriptor(typeDomain), false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
