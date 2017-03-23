package com.nebula.dropwizard.core;

import java.util.*;
import org.objectweb.asm.*;

import com.nebula.dropwizard.core.CQRSDomainBuilder.Event;
import com.nebula.dropwizard.core.CQRSDomainBuilder.Field;

public class CQRSEventAliasBuilder implements Opcodes {

	public static byte[] dump(Event event) {
		ClassWriter cw = new ClassWriter(0);
		Type type = event.type;
		Type typeSuper = event.realEvent;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, type.getInternalName(), null, typeSuper.getInternalName(), null);

		cw.visitSource(type.getClassName(), null);

		visitinit(cw, type, typeSuper, event.fields);
		return cw.toByteArray();
	}

	public static String toGetName(String fieldName) {
		return "get" + toBeanProperties(fieldName);
	}

	static String toBeanProperties(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public static void visitinit(ClassWriter cw, Type type, Type typeSuper, List<Field> fields) {
		MethodVisitor mv;
		{
			Type[] params = new Type[fields.size()];

			for (int i = 0; i < fields.size(); i++) {
				params[i] = fields.get(i).type;
			}

			String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, params);
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			mv.visitVarInsn(ALOAD, 0);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitVarInsn(field.type.getOpcode(ILOAD), i + 1);
			}
			mv.visitMethodInsn(INVOKESPECIAL, typeSuper.getInternalName(), "<init>", methodDescriptor, false);

			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(23, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l2, 0);
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l2, i + 1);
			}

			mv.visitMaxs(4, 4);
			mv.visitEnd();

		}
	}
}
