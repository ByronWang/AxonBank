package com.nebula.cqrs.axon.asm;

import java.util.List;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.spring.config.AxonConfiguration;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Field;

public class AsmBuilder implements Opcodes {

	public static void PUT_FIELD(MethodVisitor mv, Type objectType, int objectIndex, Field field, int indexData) {
		PUT_FIELD(mv, objectType, objectIndex, field.type, field.name, indexData);
	}

	public static void PUT_FIELD(MethodVisitor mv, Type objectType, int objectIndex, Class<?> fieldClass, String fieldName, int indexData) {
		PUT_FIELD(mv, objectType, objectIndex, Type.getType(fieldClass), fieldName, indexData);
	}

	public static void PUT_FIELD(MethodVisitor mv, Type objectType, int objectIndex, Type fieldType, String fieldName, int indexData) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(fieldType.getOpcode(ILOAD), indexData);
		mv.visitFieldInsn(PUTFIELD, objectType.getInternalName(), fieldName, fieldType.getDescriptor());
	}

	protected static void GET_FIELD(MethodVisitor mv, Type objectType, int objectIndex, Class<?> fieldClass, String fieldName) {
		GET_FIELD(mv, objectType, objectIndex, Type.getType(fieldClass), fieldName);
	}

	private static void GET_FIELD(MethodVisitor mv, Type objectType, int objectIndex, Type fieldType, String fieldName) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitFieldInsn(GETFIELD, objectType.getInternalName(), fieldName, fieldType.getDescriptor());
	}

	public static void define_getField(ClassWriter cw, Type type, Field field) {
		MethodVisitor mv;

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

	public static void define_field(ClassWriter cw, Field field, Class<?>... annotations) {
		FieldVisitor fv = cw.visitField(ACC_PRIVATE, field.name, field.type.getDescriptor(), null, null);
		{
			for (Class<?> annotation : annotations) {
				AnnotationVisitor av0 = fv.visitAnnotation(Type.getDescriptor(annotation), true);
				av0.visitEnd();
			}
		}
		fv.visitEnd();
	}

	public static void define_field(ClassWriter cw, Type fieldType, String name, Class<?>... annotations) {
		FieldVisitor fv = cw.visitField(ACC_PRIVATE, name, fieldType.getDescriptor(), null, null);
		{
			for (Class<?> annotation : annotations) {
				AnnotationVisitor av0 = fv.visitAnnotation(Type.getDescriptor(annotation), true);
				av0.visitEnd();
			}
		}
		fv.visitEnd();
	}

	public static void define_field(ClassWriter cw, Class<?> fieldClass, String name, Class<?>... annotations) {
		FieldVisitor fv = cw.visitField(ACC_PRIVATE, name, Type.getDescriptor(fieldClass), null, null);
		annotations(fv, annotations);
		fv.visitEnd();
	}

	public static void annotations(FieldVisitor fv, Class<?>... annotations) {
		for (Class<?> annotation : annotations) {
			AnnotationVisitor av0 = fv.visitAnnotation(Type.getDescriptor(annotation), true);
			av0.visitEnd();
		}
	}

	public static void annotations(MethodVisitor mv, Class<?>... annotations) {
		for (Class<?> annotation : annotations) {
			AnnotationVisitor av0 = mv.visitAnnotation(Type.getDescriptor(annotation), true);
			av0.visitEnd();
		}
	}

	public static void annotations(ClassWriter cw, Class<?>... annotations) {
		for (Class<?> annotation : annotations) {
			AnnotationVisitor av0 = cw.visitAnnotation(Type.getDescriptor(annotation), true);
			av0.visitEnd();
		}
	}

	public static void annotation(ClassWriter cw, Class<?> annotation, String value) {
		AnnotationVisitor av0 = cw.visitAnnotation(Type.getDescriptor(annotation), true);
		{
			AnnotationVisitor av1 = av0.visitArray("value");
			av1.visit(null, value);
			av1.visitEnd();
		}
		av0.visitEnd();
	}

	public static void define_init_nothing(ClassWriter cw, Type type) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(28, l0);

		INVOKE_init_Object(mv, 0);

		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	public static void define_init_allfield(ClassWriter cw, Type type, List<Field> fields) {
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

			INVOKE_init_Object(mv, 0);

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

	public static void define_init_allfieldToSuper(ClassWriter cw, Type type, Type typeSuper, List<Field> fields) {
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

	public static void define_toString_allfield(ClassWriter cw, Type type, List<Field> fields) {
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

	public static void invoke_getField(MethodVisitor mv, int index, Type type, Field field) {
		mv.visitVarInsn(ALOAD, index);
		mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), toGetName(field.name), Type.getMethodDescriptor(field.type), false);
	}

	public static void INVOKE_init_Object(MethodVisitor mv, int index) {
		mv.visitVarInsn(ALOAD, index);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
	}

	public static void INVOKE_init_typeWithAllfield(MethodVisitor mv, Type type, List<Field> fields) {
		Type[] params = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			params[i] = fields.get(i).type;
		}
		invoke_init_typeWithAllfield(mv, type, params);
	}

	public static void INVOKE_init_typeWithAllfield(MethodVisitor mv, Type type, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		invoke_init_typeWithAllfield(mv, type, params);
	}

	public static void invoke_init_typeWithAllfield(MethodVisitor mv, Type type, Type... params) {
		mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, params), false);
	}

	public static void INVOKE_VIRTUAL(MethodVisitor mv, Type type, String methodName, Class<?> returnClass, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		INVOKE_VIRTUAL(mv, type, methodName, Type.getType(returnClass), params);
	}	

	public static void INVOKE_VIRTUAL(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		INVOKE_VIRTUAL(mv, Type.getType(objectClass), methodName, Type.getType(returnClass), params);
	}

	public static void INVOKE_VIRTUAL(MethodVisitor mv, Type type, String methodName, Type returnType, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		INVOKE_VIRTUAL(mv, type, methodName, returnType, params);
	}

	public static void INVOKE_VIRTUAL(MethodVisitor mv, Type type, String methodName, Type returnType, Type... params) {
		mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), false);
	}

	public static void printStaticMessage(MethodVisitor mv, String staticMessage) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(staticMessage);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	public static void printObject(MethodVisitor mv, int objectIndex) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
	}

	public static void printString(MethodVisitor mv, int stringIndex) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(stringIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	public static String toGetName(String fieldName) {
		return "get" + toBeanProperties(fieldName);
	}

	static String toBeanProperties(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	static void NEW_type(MethodVisitor mv, Type typeCommandHandler) {
		mv.visitTypeInsn(NEW, typeCommandHandler.getInternalName());
	}

}
