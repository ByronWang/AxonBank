package com.nebula.dropwizard.core;

import java.util.*;
import org.objectweb.asm.*;

import com.nebula.dropwizard.core.CQRSDomainBuilder.Event;
import com.nebula.dropwizard.core.CQRSDomainBuilder.Field;

public class CQRSEventBuilder implements Opcodes {

	public static byte[] dump(Event event) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		Type type = event.type;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, type.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(type.getClassName(), null);

		visitFields(cw, type, event.fields);
		visitGetField(cw, type, event.fields);
		visitinit(cw, type, event.fields);
		return cw.toByteArray();
	}

	public static void visitFields(ClassWriter cw, Type type, List<Field> fields) {
		for (Field field : fields) {
			FieldVisitor fv;
			{
				fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, field.name, field.type.getDescriptor(), null, null);
				fv.visitEnd();
			}
		}
	}

	public static String toGetName(String fieldName) {
		return "get" + toBeanProperties(fieldName);
	}

	static String toBeanProperties(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public static void visitGetField(ClassWriter cw, Type type, List<Field> fields) {
		MethodVisitor mv;

		for (Field field : fields) {
			String methodDescriptor = Type.getMethodDescriptor(field.type, new Type[] {});
			mv = cw.visitMethod(ACC_PUBLIC, toGetName(field.name), methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, type.getInternalName(), field.name, field.type.getDescriptor());
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
	}

	public static void visitinit(ClassWriter cw, Type type, List<Field> fields) {
		MethodVisitor mv;
		AnnotationVisitor av0;
		{
			Type[] params = new Type[fields.size()];

			for (int i = 0; i < fields.size(); i++) {
				params[i] = fields.get(i).type;
			}

			String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, params);

			mv = cw.visitMethod(ACC_PUBLIC, "<init>", methodDescriptor, null, null);
			{
				av0 = mv.visitAnnotation("Ljava/beans/ConstructorProperties;", true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					for (Field field : fields) {
						av1.visit(null, field.name);
					}
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, i + 1);
				mv.visitFieldInsn(PUTFIELD, type.getInternalName(), field.name, field.type.getDescriptor());
			}

			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l1, 0);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l1, i + 1);
			}
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}
		cw.visitEnd();
	}
}
