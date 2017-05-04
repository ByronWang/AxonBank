package com.nebula.cqrs.axon.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;

public class CQRSCommandHandlerCallerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type handleType, Type objectType, Type domainType, Type commandType, Command command, DomainDefinition domainDefinition)
			throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;

		cw.visit(52, ACC_SUPER, objectType.getInternalName(), "Ljava/lang/Object;Ljava/util/function/Consumer<" + domainType.getDescriptor() + ">;",
				"java/lang/Object", new String[] { "java/util/function/Consumer" });

		cw.visitInnerClass(objectType.getInternalName(), handleType.getInternalName(), "Inner" + command.commandName, 0);

		{
			fv = cw.visitField(0, "command", commandType.getDescriptor(), null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0", handleType.getDescriptor(), null, null);
			fv.visitEnd();
		}
		visitDefine_init(cw, handleType, objectType, commandType);
		visitDefine_accept(cw, handleType, objectType, domainType, command, commandType);
		visitDefine_acceptbridge(cw, objectType, domainType);
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_acceptbridge(ClassWriter cw, Type objectType, Type domainType) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "accept", "(Ljava/lang/Object;)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			{
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, domainType.getInternalName());
				mv.visitMethodInsn(INVOKEVIRTUAL, objectType.getInternalName(), "accept", Type.getMethodDescriptor(Type.VOID_TYPE, domainType), false);
				mv.visitInsn(RETURN);
			}
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
	}

	private static void visitDefine_accept(ClassWriter cw, Type handleType, Type objectType, Type domainType, Command command, Type commandType) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "accept", Type.getMethodDescriptor(Type.VOID_TYPE, domainType), null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(59, l0);
			{

				mv.visitVarInsn(ALOAD, 1);

				for (int i = 0; i < command.methodParams.length; i++) {
					visitGetField(mv, 0, objectType, "command", commandType);
					visitGetProperty(mv, commandType, command.methodParams[i]);
				}

				visitInvokeVirtual(mv, domainType, command.returnType, command.methodName, command.methodParams);

				mv.visitInsn(POP);
				mv.visitInsn(RETURN);
			}
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l2, 0);
			mv.visitLocalVariable("domain", domainType.getDescriptor(), null, l0, l2, 1);
			mv.visitMaxs(3, 2);
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
			mv.visitLineNumber(54, l0);
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
