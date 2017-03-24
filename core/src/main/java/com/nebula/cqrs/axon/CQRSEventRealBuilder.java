package com.nebula.cqrs.axon;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Event;
import com.nebula.cqrs.axon.CQRSDomainBuilder.Field;

public class CQRSEventRealBuilder implements Opcodes {

	public static byte[] dump(Event target) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		Type type = target.type;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, type.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(type.getClassName(), null);

		visit_fields(cw, type, target.fields);
		visit_getField(cw, type, target.fields);
		visit_init(cw, type, target.fields);
		visit_toString(cw, type, target.fields);
		return cw.toByteArray();
	}

	public static void visit_fields(ClassWriter cw, Type type, List<Field> fields) {
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

	public static void visit_getField(ClassWriter cw, Type type, List<Field> fields) {
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
			mv.visitInsn(field.type.getOpcode(IRETURN));
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}

	public static void visit_init(ClassWriter cw, Type type, List<Field> fields) {
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
				mv.visitVarInsn(field.type.getOpcode(ILOAD), i + 1);
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

	public static void visit_toString(ClassWriter cw, Type type, List<Field> fields) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
			mv.visitInsn(DUP);
			mv.visitLdcInsn(CQRSDomainBuilder.toSimpleName(type.getClassName()) + "(");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
			{
				for (int i = 0; i < fields.size(); i++) {
					Field field = fields.get(i);
					if (i != 0) {
						mv.visitLdcInsn(",");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
					}

					mv.visitLdcInsn(field.name + "=");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

					mv.visitVarInsn(ALOAD, 0);
					mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), toGetName(field.name), Type.getMethodDescriptor(field.type), false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
							Type.getMethodDescriptor(Type.getObjectType("java/lang/StringBuilder"), field.type), false);

				}
			}

			mv.visitLdcInsn(")");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(3, 1);
			mv.visitEnd();
		}
	}
}
