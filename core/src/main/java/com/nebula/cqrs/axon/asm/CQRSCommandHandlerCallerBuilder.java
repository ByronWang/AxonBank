package com.nebula.cqrs.axon.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.core.asm.AsmBuilder;
import com.nebula.cqrs.core.asm.Field;

public class CQRSCommandHandlerCallerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type typeInner,Type implDomainType, Type typeHandler, Command command) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		FieldVisitor fv;

		Type typeCommand = command.type;

		cw.visit(52, ACC_SUPER, typeInner.getInternalName(), "Ljava/lang/Object;Ljava/util/function/Consumer<" + implDomainType.getDescriptor() + ">;",
				"java/lang/Object", new String[] { "java/util/function/Consumer" });

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
		visitDefine_init(cw, typeInner, typeHandler, typeCommand);
		visitDefine_accept(cw, typeInner, implDomainType, command, typeCommand);
		visitDefine_accept_bridge(cw, typeInner, implDomainType);
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_accept(ClassWriter cw, Type typeInner, Type typeDomain, Command command, Type typeCommand) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "accept", Type.getMethodDescriptor(Type.VOID_TYPE, typeDomain), null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(65, l0);
			{
				visitPrintObject(mv,"call for domain ", 1);
				
				mv.visitVarInsn(ALOAD, 1);

				Type[] types = new Type[command.methodParams.length];
				for (int i = 0; i < command.methodParams.length; i++) {
					Field field = command.methodParams[i];
					types[i] = command.methodParams[i].type;

					visitGetField(mv, 0, typeInner, "val$command", typeCommand);
					visitGetProperty(mv, typeCommand, field);
				}

				visitInvokeVirtual(mv, typeDomain, command.returnType, command.methodName, types);

				visitReturn(mv);
			}
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", typeInner.getDescriptor(), null, l0, l2, 0);
			mv.visitLocalVariable(AsmBuilder.toCamelLower(toSimpleName(typeDomain.getClassName())), typeDomain.getDescriptor(), null,
					l0, l2, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
	}

	private static void visitDefine_accept_bridge(ClassWriter cw, Type typeInner, Type typeDomain) {
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
				mv.visitTypeInsn(CHECKCAST, typeDomain.getInternalName());

				visitInvokeVirtual(mv, typeInner, "accept", typeDomain);

				visitReturn(mv);
			}
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
	}

	private static void visitDefine_init(ClassWriter cw, Type typeInner, Type typeHandler, Type typeCommand) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(0, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, typeHandler, typeCommand), null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			{
				visitPutField(mv, 0, typeInner, 1, "this$0", typeHandler);
				visitPutField(mv, 0, typeInner, 2, "val$command", typeCommand);
				visitInitObject(mv, 0);
				visitReturn(mv);
			}
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", typeInner.getDescriptor(), null, l0, l2, 0);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
	}
}
