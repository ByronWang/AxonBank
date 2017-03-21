package asm.com.nebula.dropwizard.core;

import java.util.*;
import org.objectweb.asm.*;

public class Todo2Dump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/nebula/dropwizard/core/Todo", null, "java/lang/Object", null);

		cw.visitSource("Todo2.java", null);

		{
			fv = cw.visitField(0, "completed", "Lnebula/define/YesNo;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "title", "Lnebula/define/Title;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "age", "Lnebula/define/Age;", null, null);
			fv.visitEnd();
		}

		{
			mv = cw.visitMethod(ACC_PUBLIC, "getCompleted", "()Lnebula/define/YesNo;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(44, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "com/nebula/dropwizard/core/Todo", "completed", "Lnebula/define/YesNo;");
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/Todo2;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getTitle", "()Lnebula/define/Title;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(48, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "com/nebula/dropwizard/core/Todo", "title", "Lnebula/define/Title;");
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/Todo2;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "setCompleted", "(Lnebula/define/YesNo;)V", null, null);
			mv.visitParameter("completed", 0);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "completed", "Lnebula/define/YesNo;");
			mv.visitInsn(RETURN);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "setTitle", "(Lnebula/define/Title;)V", null, null);
			mv.visitParameter("title", 0);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "title", "Lnebula/define/Title;");
			mv.visitInsn(RETURN);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getAge", "()Lnebula/define/Age;", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "com/nebula/dropwizard/core/Todo", "age", "Lnebula/define/Age;");
			mv.visitInsn(ARETURN);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "setAge", "(Lnebula/define/Age;)V", null, null);
			mv.visitParameter("age", 0);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(20, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "age", "Lnebula/define/Age;");
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(21, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/Todo2;", null, l0, l2, 0);
			mv.visitLocalVariable("age", "Lnebula/define/Age;", null, l0, l2, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lnebula/define/YesNo;Lnebula/define/Title;Lnebula/define/Age;)V", null, null);
			mv.visitParameter("completed", 0);
			mv.visitParameter("title", 0);
			mv.visitParameter("age", 0);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(24, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "completed", "Lnebula/define/YesNo;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "title", "Lnebula/define/Title;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "age", "Lnebula/define/Age;");
			mv.visitInsn(RETURN);
			mv.visitEnd();
		}
		
		{
			mv = cw.visitMethod(ACC_PUBLIC, "changeCompleted", "(Lnebula/define/YesNo;)V", null, null);
			mv.visitParameter("completed", 0);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(31, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/nebula/dropwizard/core/Todo", "setCompleted", "(Lnebula/define/YesNo;)V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/nebula/dropwizard/core/Todo", "getAge", "()Lnebula/define/Age;", false);
			mv.visitInsn(ICONST_1);
			mv.visitMethodInsn(INVOKESTATIC, "nebula/define/R", "of", "(I)Lnebula/define/Long;", true);
			mv.visitTypeInsn(CHECKCAST, "nebula/define/Age");
			mv.visitMethodInsn(INVOKEINTERFACE, "nebula/define/Age", "$plus", "(Lnebula/define/Age;)Lnebula/define/Age;", true);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/nebula/dropwizard/core/Todo", "setAge", "(Lnebula/define/Age;)V", false);
			mv.visitInsn(RETURN);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "changeTitle", "(Lnebula/define/Title;)V", null, null);
			mv.visitParameter("title", 0);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(36, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/nebula/dropwizard/core/Todo", "setTitle", "(Lnebula/define/Title;)V", false);
			mv.visitInsn(RETURN);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "delete", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(41, l0);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lcom/nebula/dropwizard/core/Todo2;", null, l0, l1, 0);
			mv.visitMaxs(0, 1);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
