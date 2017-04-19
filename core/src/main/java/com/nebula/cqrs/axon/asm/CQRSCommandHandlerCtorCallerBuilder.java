package com.nebula.cqrs.axon.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;

public class CQRSCommandHandlerCtorCallerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type typeDomain, Type typeHandler, Command command) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		FieldVisitor fv;

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

		visitDefine_init(cw, typeInner, typeHandler, typeCommand);
		visitDefine_call(cw, typeInner, typeDomain, command, typeCommand);
		visitDefine_call_bridge(cw, typeInner, typeDomain);

		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_call(ClassWriter cw, Type typeInner, Type typeDomain, Command command, Type typeCommand) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "call", Type.getMethodDescriptor(typeDomain), null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(65, l0);
			{
				visitNewObject(mv, typeDomain);
				mv.visitInsn(DUP);

				for (int i = 0; i < command.methodParams.length; i++) {
					visitGetField(mv, 0, typeInner, "val$command", typeCommand);
					visitGetProperty(mv, typeCommand, command.methodParams[i]);
				}

				visitInitTypeWithAllFields(mv, typeDomain, command.methodParams);

				visitPrintStaticMessage(mv, typeCommand.getInternalName() + " create new object");

				visitAReturn(mv);
			}
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeInner.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(5, 1);
			mv.visitEnd();
		}
	}

	private static void visitDefine_call_bridge(ClassWriter cw, Type typeInner, Type typeDomain) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "call", "()Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(1, l0);
			{
				visitInvokeVirtual(mv, 0, typeInner, typeDomain, "call");
				visitAReturn(mv);
			}
			mv.visitMaxs(1, 1);
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
