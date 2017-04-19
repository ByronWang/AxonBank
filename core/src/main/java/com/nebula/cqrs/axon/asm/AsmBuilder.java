package com.nebula.cqrs.axon.asm;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Field;

public class AsmBuilder implements Opcodes {

	public static void visitPutField(MethodVisitor mv, Type objectType, int objectIndex, Field field, int indexData) {
		visitPutField(mv, objectType, objectIndex, field.type, field.name, indexData);
	}

	public static void visitPutField(MethodVisitor mv, Type objectType, int objectIndex, Class<?> fieldClass, String fieldName, int indexData) {
		visitPutField(mv, objectType, objectIndex, Type.getType(fieldClass), fieldName, indexData);
	}

	public static void visitPutField(MethodVisitor mv, Type objectType, int objectIndex, Type fieldType, String fieldName, int indexData) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(fieldType.getOpcode(ILOAD), indexData);
		mv.visitFieldInsn(PUTFIELD, objectType.getInternalName(), fieldName, fieldType.getDescriptor());
	}

	protected static void visitGetField(MethodVisitor mv, Type objectType, int objectIndex, Class<?> fieldClass, String fieldName) {
		visitGetField(mv, objectType, objectIndex, Type.getType(fieldClass), fieldName);
	}

	private static void visitGetField(MethodVisitor mv, Type objectType, int objectIndex, Type fieldType, String fieldName) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitFieldInsn(GETFIELD, objectType.getInternalName(), fieldName, fieldType.getDescriptor());
	}

	public static void visitDefinePropetyGet(ClassWriter cw, Type objectType, Field field) {
		visitDefinePropetyGet(cw, objectType, field.type, field.name);
	}
	public static void visitDefinePropetyGet(ClassWriter cw, Type objectType,Type fieldType, String fieldName) {
		MethodVisitor mv;

		String methodDescriptor = Type.getMethodDescriptor(fieldType, new Type[] {});
		mv = cw.visitMethod(ACC_PUBLIC, toPropertyGetName(fieldName), methodDescriptor, null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(22, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, objectType.getInternalName(),fieldName, fieldType.getDescriptor());
		mv.visitInsn(fieldType.getOpcode(IRETURN));
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public static void visitDefineField(ClassWriter cw, Field field, Class<?>... annotations) {
		visitDefineField(cw, field.type, field.name, annotations);
	}
	public static void visitDefineField(ClassWriter cw, Class<?> fieldClass, String fieldName, Class<?>... annotations) {
		visitDefineField(cw, Type.getType(fieldClass), fieldName, annotations);
	}

	public static void visitDefineField(ClassWriter cw, Type fieldType, String fieldName, Class<?>... annotations) {
		FieldVisitor fv = cw.visitField(ACC_PRIVATE, fieldName, fieldType.getDescriptor(), null, null);
		visitAnnotation(fv, annotations);
		fv.visitEnd();
	}

	public static void visitAnnotation(ClassWriter cw, Class<?>... annotations) {
		for (Class<?> annotation : annotations) {
			AnnotationVisitor av0 = cw.visitAnnotation(Type.getDescriptor(annotation), true);
			av0.visitEnd();
		}
	}
	
	public static void visitAnnotation(FieldVisitor fv, Class<?>... annotations) {
		for (Class<?> annotation : annotations) {
			AnnotationVisitor av0 = fv.visitAnnotation(Type.getDescriptor(annotation), true);
			av0.visitEnd();
		}
	}

	public static void visitAnnotation(MethodVisitor mv, Class<?>... annotations) {
		for (Class<?> annotation : annotations) {
			AnnotationVisitor av0 = mv.visitAnnotation(Type.getDescriptor(annotation), true);
			av0.visitEnd();
		}
	}
	
	public static void visitAnnotation(ClassWriter cw, Class<?> annotation, String value) {
		AnnotationVisitor av0 = cw.visitAnnotation(Type.getDescriptor(annotation), true);
		{
			AnnotationVisitor av1 = av0.visitArray("value");
			av1.visit(null, value);
			av1.visitEnd();
		}
		av0.visitEnd();
	}

	public static void visitAnnotation(MethodVisitor mv, Class<?> annotation, String value) {
		AnnotationVisitor av0 = mv.visitAnnotation(Type.getDescriptor(annotation), true);
		{
			AnnotationVisitor av1 = av0.visitArray("value");
			av1.visit(null, value);
			av1.visitEnd();
		}
		av0.visitEnd();
	}
	public static void visitDefineInitWithNothing(ClassWriter cw, Type objectType) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(28, l0);

		visitInitObject(mv, 0);

		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	public static void visitDefineInitWithAllFields(ClassWriter cw, Type objectType, List<Field> fields) {
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

			visitInitObject(mv, 0);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(field.type.getOpcode(ILOAD), i + 1);
				mv.visitFieldInsn(PUTFIELD, objectType.getInternalName(), field.name, field.type.getDescriptor());
			}

			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l1, 0);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l1, i + 1);
			}
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}
		cw.visitEnd();
	}

	public static void visitDefineInitWithAllFieldsToSuper(ClassWriter cw, Type objectType, Type superType, List<Field> fields) {
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
			mv.visitMethodInsn(INVOKESPECIAL, superType.getInternalName(), "<init>", methodDescriptor, false);

			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(23, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l2, 0);
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l2, i + 1);
			}

			mv.visitMaxs(4, 4);
			mv.visitEnd();

		}
	}

	public static void visitDefineToStringWithAllFields(ClassWriter cw, Type type, List<Field> fields) {
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
					mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), toPropertyGetName(field.name), Type.getMethodDescriptor(field.type), false);
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

	public static void visitGetProperty(MethodVisitor mv, int index, Type type, Field field) {
		mv.visitVarInsn(ALOAD, index);
		mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), toPropertyGetName(field.name), Type.getMethodDescriptor(field.type), false);
	}

	public static void visitInitObject(MethodVisitor mv, int index) {
		mv.visitVarInsn(ALOAD, index);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Type type, List<Field> fields) {
		Type[] params = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			params[i] = fields.get(i).type;
		}
		visitInitTypeWithAllFields(mv, type, params);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Type type, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInitTypeWithAllFields(mv, type, params);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Type type, Type... params) {
		mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, params), false);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type type, String methodName, Class<?> returnClass, Class<?>... classes) {
		visitInvokeVirtual(mv, type, Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeVirtual(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type type, Type returnType, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeVirtual(mv, type, returnType, methodName, params);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type type, Type returnType, String methodName, Type... params) {
		mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), false);
	}
	

	public static void visitInvokeSpecial(MethodVisitor mv, Type type, String methodName, Class<?> returnClass, Class<?>... classes) {
		visitInvokeSpecial(mv, type, Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeSpecial(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Type type, Type returnType, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeSpecial(mv, type, returnType, methodName, params);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Type type, Type returnType, String methodName, Type... params) {
		mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), false);
	}
	

	public static void visitInvokeStatic(MethodVisitor mv, Type type, String methodName, Class<?> returnClass, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeStatic(mv, type, Type.getType(returnClass), methodName, params);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeStatic(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, params);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Type type, Type returnType, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeStatic(mv, type, returnType, methodName, params);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Type type, Type returnType, String methodName, Type... params) {
		mv.visitMethodInsn(INVOKESTATIC, type.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), false);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type interfaceType, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, interfaceType, Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Class<?> interfaceClass,  String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, Type.getType(interfaceClass), Type.VOID_TYPE, methodName, classes);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Class<?> interfaceClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, Type.getType(interfaceClass), Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type type, Type returnType, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeInterface(mv, type, returnType, methodName, params);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type type, Type returnType, String methodName, Type... params) {
		mv.visitMethodInsn(INVOKEINTERFACE, type.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), true);
	}

	public static void visitPrintStaticMessage(MethodVisitor mv, String staticMessage) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(staticMessage);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	public static void visitPrintObject(MethodVisitor mv, int objectIndex) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
	}

	public static void visitPrintString(MethodVisitor mv, int stringIndex) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(stringIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	private static String toPropertyGetName(String fieldName) {
		return "get" + toBeanProperties(fieldName);
	}

	private static String toBeanProperties(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	static void visitNewObject(MethodVisitor mv, Type typeCommandHandler) {
		mv.visitTypeInsn(NEW, typeCommandHandler.getInternalName());
	}

}
