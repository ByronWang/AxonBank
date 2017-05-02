package com.nebula.cqrs.core.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AsmBuilder implements Opcodes {

	protected static String toPropertyGetName(String fieldName) {
		return "get" + toPropertyName(fieldName);
	}

	protected static String toPropertyName(String fieldName) {
		return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}

	protected static String toPropertySetName(String fieldName) {
		return "set" + toPropertyName(fieldName);
	}

	public static void visitAnnotation(ClassVisitor cw, Class<?>... annotations) {
		for (Class<?> annotation : annotations) {
			AnnotationVisitor av0 = cw.visitAnnotation(Type.getDescriptor(annotation), true);
			av0.visitEnd();
		}
	}

	public static void visitAnnotation(ClassVisitor cw, Class<?> annotation, String value) {
		AnnotationVisitor av0 = cw.visitAnnotation(Type.getDescriptor(annotation), true);
		{
			AnnotationVisitor av1 = av0.visitArray("value");
			av1.visit(null, value);
			av1.visitEnd();
		}
		av0.visitEnd();
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

	public static void visitAnnotation(MethodVisitor mv, Class<?> annotation, String value) {
		AnnotationVisitor av0 = mv.visitAnnotation(Type.getDescriptor(annotation), true);
		{
			AnnotationVisitor av1 = av0.visitArray("value");
			av1.visit(null, value);
			av1.visitEnd();
		}
		av0.visitEnd();
	}

	public static void visitAReturn(MethodVisitor mv) {
		mv.visitInsn(ARETURN);
	}

	public static void visitDefine_init_withNothing(ClassVisitor cw, Type objectType) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		{
			visitInitObject(mv, 0);

			mv.visitInsn(RETURN);
		}
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	public static void visitDefineField(ClassVisitor cw, String fieldName, Class<?> fieldClass, Class<?>... annotations) {
		visitDefineField(cw, fieldName, Type.getType(fieldClass), annotations);
	}

	public static void visitDefineField(ClassVisitor cw, String fieldName, Type fieldType, Class<?>... annotations) {
		FieldVisitor fv = cw.visitField(ACC_PRIVATE, fieldName, fieldType.getDescriptor(), null, null);
		visitAnnotation(fv, annotations);
		fv.visitEnd();
	}

	public static void visitDefinePropertyGet(ClassVisitor cw, Type objectType, String fieldName, Type fieldType) {
		MethodVisitor mv;
		mv = cw.visitMethod(ACC_PUBLIC, toPropertyGetName(fieldName), Type.getMethodDescriptor(fieldType), null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(22, l0);
		{
			visitGetField(mv, 0, objectType, fieldName, fieldType);
			visitReturn(mv, fieldType);
		}
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public static void visitDefinePropertySet(ClassVisitor cw, Type objectType, String fieldName, Type fieldType) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, toPropertySetName(fieldName), Type.getMethodDescriptor(Type.VOID_TYPE, fieldType), null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(56, l0);
			{
				visitPutField(mv, 0, objectType, 1, fieldName, fieldType);
				visitReturn(mv);
			}
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l2, 0);
			mv.visitLocalVariable(fieldName, fieldType.getDescriptor(), null, l0, l2, 1);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
	}

	public static void visitGetField(MethodVisitor mv, int objectIndex, Type objectType, String fieldName, Class<?> fieldClass) {
		visitGetField(mv, objectIndex, objectType, fieldName, Type.getType(fieldClass));
	}

	public static void visitGetField(MethodVisitor mv, int objectIndex, Type objectType, String fieldName, Type fieldType) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitFieldInsn(GETFIELD, objectType.getInternalName(), fieldName, fieldType.getDescriptor());
	}

	public static void visitGetProperty(MethodVisitor mv, int objectIndex, Type objectType, String fieldName, Type fieldType) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, objectType.getInternalName(), toPropertyGetName(fieldName), Type.getMethodDescriptor(fieldType), false);
	}

	public static void visitGetProperty(MethodVisitor mv, Type objectType, String fieldName, Type fieldType) {
		mv.visitMethodInsn(INVOKEVIRTUAL, objectType.getInternalName(), toPropertyGetName(fieldName), Type.getMethodDescriptor(fieldType), false);
	}

	public static void visitInitObject(MethodVisitor mv, int index) {
		mv.visitVarInsn(ALOAD, index);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Class<?> objectClass, Class<?>... classes) {
		visitInitTypeWithAllFields(mv, Type.getType(objectClass), classes);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Type objectType, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInitTypeWithAllFields(mv, objectType, params);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Type objectType, Type... params) {
		mv.visitMethodInsn(INVOKESPECIAL, objectType.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, params), false);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Class<?> interfaceClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, Type.getType(interfaceClass), Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Type... params) {
		visitInvokeInterface(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, params);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Class<?> interfaceClass, String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, Type.getType(interfaceClass), Type.VOID_TYPE, methodName, classes);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type interfaceType, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, interfaceType, Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type interfaceType, Type returnType, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeInterface(mv, interfaceType, returnType, methodName, params);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type type, Type returnType, String methodName, Type... params) {
		mv.visitMethodInsn(INVOKEINTERFACE, type.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), true);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeSpecial(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Type objectType, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeSpecial(mv, objectType, Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Type objectType, Type returnType, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeSpecial(mv, objectType, returnType, methodName, params);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Type objectType, Type returnType, String methodName, Type... params) {
		mv.visitMethodInsn(INVOKESPECIAL, objectType.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), false);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeStatic(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, params);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Type objectType, Class<?> returnClass, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeStatic(mv, objectType, Type.getType(returnClass), methodName, params);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Type objectType, Type returnType, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeStatic(mv, objectType, returnType, methodName, params);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Type objectType, Type returnType, String methodName, Type... params) {
		mv.visitMethodInsn(INVOKESTATIC, objectType.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), false);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeVirtual(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Class<?> objectClass, Type returnType, String methodName, Class<?>... classes) {
		visitInvokeVirtual(mv, Type.getType(objectClass), returnType, methodName, classes);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, int objectIndex, Type objectType, String methodName, Type... params) {
		visitInvokeVirtual(mv, objectIndex, objectType, Type.VOID_TYPE, methodName, params);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, int objectIndex, Type objectType, Type returnType, String methodName, Type... params) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, objectType.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), false);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type objectType, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeVirtual(mv, objectType, Type.getType(returnClass), methodName, classes);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type objectType, String methodName, Type... params) {
		visitInvokeVirtual(mv, objectType, Type.VOID_TYPE, methodName, params);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type objectType, Type returnType, String methodName, Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		visitInvokeVirtual(mv, objectType, returnType, methodName, params);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type objectType, Type returnType, String methodName, Type... params) {
		mv.visitMethodInsn(INVOKEVIRTUAL, objectType.getInternalName(), methodName, Type.getMethodDescriptor(returnType, params), false);
	}

	public static void visitNewObject(MethodVisitor mv, Class<?> objectClass) {
		visitNewObject(mv, Type.getType(objectClass));
	}

	public static void visitNewObject(MethodVisitor mv, Type objectType) {
		mv.visitTypeInsn(NEW, objectType.getInternalName());
	}

	public static void visitPrintObject(MethodVisitor mv, int objectIndex) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
	}

	public static void visitPrintStaticMessage(MethodVisitor mv, String staticMessage) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(staticMessage);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	public static void visitPrintString(MethodVisitor mv, int stringIndex) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(stringIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	public static void visitPutField(MethodVisitor mv, int objectIndex, Type objectType, int dataIndex, String fieldName, Class<?> fieldClass) {
		visitPutField(mv, objectIndex, objectType, dataIndex, fieldName, Type.getType(fieldClass));
	}

	public static void visitPutField(MethodVisitor mv, int objectIndex, Type objectType, int dataIndex, String fieldName, Type fieldType) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(fieldType.getOpcode(ILOAD), dataIndex);
		mv.visitFieldInsn(PUTFIELD, objectType.getInternalName(), fieldName, fieldType.getDescriptor());
	}

	public static void visitReturn(MethodVisitor mv) {
		mv.visitInsn(RETURN);
	}

	public static void visitReturn(MethodVisitor mv, Type fieldType) {
		mv.visitInsn(fieldType.getOpcode(IRETURN));
	}

	public static String toSimpleName(String name) {
		int index = name.lastIndexOf('.');
		if (index < 0) index = name.lastIndexOf('/');

		return name.substring(index + 1);
	}
	
	public static int[] computerLocals(Type objectType, Type... types) {
		int[] locals = new int[types.length + 1];
		int cntLocal = 0;
		locals[0] = 0;
		cntLocal += objectType.getSize();
		for (int i = 0; i < types.length; i++) {
			locals[i + 1] = cntLocal;
			cntLocal += types[i].getSize();
		}
		return locals;
	}

}
