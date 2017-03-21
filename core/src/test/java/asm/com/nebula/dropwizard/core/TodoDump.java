package asm.com.nebula.dropwizard.core;

import java.util.*;
import org.objectweb.asm.*;

public class TodoDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/nebula/dropwizard/core/Todo", null, "java/lang/Object", null);

		cw.visitSource("Todo.java", null);

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
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lnebula/define/YesNo;Lnebula/define/Title;Lnebula/define/Age;)V", null, null);
			mv.visitParameter("completed", 0);
			mv.visitParameter("title", 0);
			mv.visitParameter("age", 0);
			mv.visitCode();
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
			mv = cw.visitMethod(ACC_PUBLIC, "changeTitle", "(Lnebula/define/Title;)V", null, null);
			mv.visitParameter("title", 0);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "title", "Lnebula/define/Title;");
			mv.visitInsn(RETURN);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "changeCompleted", "(Lnebula/define/YesNo;)V", null, null);
			mv.visitParameter("completed", 0);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "completed", "Lnebula/define/YesNo;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "com/nebula/dropwizard/core/Todo", "age", "Lnebula/define/Age;");
			mv.visitInsn(ICONST_1);
			mv.visitMethodInsn(INVOKESTATIC, "nebula/define/R", "of", "(I)Lnebula/define/Long;", true);
			mv.visitTypeInsn(CHECKCAST, "nebula/define/Age");
			mv.visitMethodInsn(INVOKEINTERFACE, "nebula/define/Age", "$plus", "(Lnebula/define/Age;)Lnebula/define/Age;", true);
			mv.visitFieldInsn(PUTFIELD, "com/nebula/dropwizard/core/Todo", "age", "Lnebula/define/Age;");
			mv.visitInsn(RETURN);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "delete", "()V", null, null);
			mv.visitCode();
			mv.visitInsn(RETURN);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
