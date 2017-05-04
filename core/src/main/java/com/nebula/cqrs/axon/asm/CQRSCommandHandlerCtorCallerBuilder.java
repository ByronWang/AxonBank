package com.nebula.cqrs.axon.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;

public class CQRSCommandHandlerCtorCallerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type handleType, Type objectType, Type domainType, Type commandType, Command command, DomainDefinition domainDefinition)
			throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;

		cw.visit(52, ACC_SUPER, objectType.getInternalName(), "Ljava/lang/Object;Ljava/util/concurrent/Callable<" + domainType.getDescriptor() + ">;",
				"java/lang/Object", new String[] { "java/util/concurrent/Callable" });

		cw.visitInnerClass(objectType.getInternalName(), handleType.getInternalName(), "InnerCreate", 0);

		{
			fv = cw.visitField(0, "command", commandType.getDescriptor(), null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0", handleType.getDescriptor(), null, null);
			fv.visitEnd();
		}
		visitDefine_init(cw, handleType, objectType, commandType);

		visitDefine_call(objectType, domainType, commandType, command, cw);
		
		visitDefine_call_bridge(cw, objectType, domainType);
		
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_call_bridge(ClassWriter cw, Type objectType, Type domainType) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "call", "()Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			{
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKEVIRTUAL, objectType.getInternalName(), "call", Type.getMethodDescriptor(domainType), false);
				mv.visitInsn(ARETURN);
			}
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
	}

	private static void visitDefine_call(Type objectType, Type domainType, Type commandType, Command command, ClassWriter cw) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "call", Type.getMethodDescriptor(domainType), null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(71, l0);
			{
				visitNewObject(mv, domainType);
				mv.visitInsn(DUP);

				for (int i = 0; i < command.methodParams.length; i++) {
					visitGetField(mv, 0, objectType, "command", commandType);
					visitGetProperty(mv, commandType, command.methodParams[i]);
				}
				visitInitTypeWithAllFields(mv, domainType, command.methodParams);

				visitAReturn(mv);
			}
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l2, 0);
			mv.visitMaxs(5, 1);
			mv.visitEnd();
		}
	}

	private static void visitDefine_init(ClassWriter cw, Type handleType, Type objectType, Type commandType) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(0, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, handleType, commandType), null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(66, l0);
			{
				visitPutField(mv, 0, objectType, 1, "this$0", handleType);

				visitInitObject(mv, 0);

				visitPutField(mv, 0, objectType, 2, "command", commandType);

				visitReturn(mv);
			}
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l3, 0);
			mv.visitLocalVariable("command", commandType.getDescriptor(), null, l0, l3, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
	}
}
