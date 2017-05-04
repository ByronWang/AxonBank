package com.nebula.cqrs.core.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AsmBuilderHelper extends ASMBuilder implements Opcodes {

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

	protected static Type toType(Class<?> clz) {
		return Type.getType(clz);
	}

	protected static Type[] toTypes(Class<?>... classes) {
		Type[] params = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			params[i] = Type.getType(classes[i]);
		}
		return params;
	}

	public static void visitDefineField(ClassVisitor cw, String fieldName, Class<?> fieldClass, Class<?>... annotations) {
		visitDefineField(cw, fieldName, Type.getType(fieldClass), annotations);
	}

	public static void visitGetField(MethodVisitor mv, int objectIndex, Type objectType, String fieldName, Class<?> fieldClass) {
		visitGetField(mv, objectIndex, objectType, fieldName, Type.getType(fieldClass));
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Class<?> objectClass, Class<?>... classes) {
		visitInitTypeWithAllFields(mv, Type.getType(objectClass), classes);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Type objectType, Class<?>... params) {
		visitInitTypeWithAllFields(mv, objectType, toTypes(params));
	}


	public static void visitInvokeInterface(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName) {
		visitInvokeInterface(mv, Type.getType(objectClass), Type.getType(returnClass), methodName);
	}
	
	public static void visitInvokeInterface(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, toTypes(classes));
	}

	public static void visitInvokeInterface(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Type... params) {
		visitInvokeInterface(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, params);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Class<?> objectClass, String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, Type.getType(objectClass), Type.VOID_TYPE, methodName, toTypes(classes));
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type objectType, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeInterface(mv, objectType, Type.getType(returnClass), methodName, toTypes(classes));
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type objectType, String methodName, Type... params) {
		visitInvokeInterface(mv, objectType, Type.VOID_TYPE, methodName, params);
	}

	public static void visitInvokeInterface(MethodVisitor mv, Type objectType, Type returnType, String methodName, Class<?>... params) {
		visitInvokeInterface(mv, objectType, returnType, methodName, toTypes(params));
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeSpecial(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, toTypes(classes));
	}

	public static void visitInvokeSpecial(MethodVisitor mv, int objectIndex, Type objectType, String methodName) {
		visitInvokeSpecial(mv, objectIndex, objectType, Type.VOID_TYPE, methodName);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Type objectType, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeSpecial(mv, objectType, Type.getType(returnClass), methodName, toTypes(classes));
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Type objectType, String methodName, Type... params) {
		visitInvokeSpecial(mv, objectType, Type.VOID_TYPE, methodName, params);
	}

	public static void visitInvokeSpecial(MethodVisitor mv, Type objectType, Type returnType, String methodName, Class<?>... params) {
		visitInvokeSpecial(mv, objectType, returnType, methodName, toTypes(params));
	}

	public static void visitInvokeStatic(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeStatic(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, toTypes(classes));
	}

	public static void visitInvokeStatic(MethodVisitor mv, int objectIndex, Type objectType, String methodName) {
		visitInvokeStatic(mv, objectIndex, objectType, Type.VOID_TYPE, methodName);
	}

	public static void visitInvokeStatic(MethodVisitor mv, int objectIndex, Type objectType, Type returnType, String methodName) {
		visitInvokeStatic(mv, objectIndex, objectType, returnType, methodName);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Type objectType, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeStatic(mv, objectType, Type.getType(returnClass), methodName, toTypes(classes));
	}

	public static void visitInvokeStatic(MethodVisitor mv, Type objectType, String methodName, Type... params) {
		visitInvokeStatic(mv, objectType, Type.VOID_TYPE, methodName, params);
	}

	public static void visitInvokeStatic(MethodVisitor mv, Type objectType, Type returnType, String methodName, Class<?>... params) {
		visitInvokeStatic(mv, objectType, returnType, methodName, toTypes(params));
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Class<?> objectClass, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeVirtual(mv, Type.getType(objectClass), Type.getType(returnClass), methodName, toTypes(classes));
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Class<?> objectClass, Type returnType, String methodName, Class<?>... classes) {
		visitInvokeVirtual(mv, Type.getType(objectClass), returnType, methodName, toTypes(classes));
	}

	public static void visitInvokeVirtual(MethodVisitor mv, int objectIndex, Type objectType, String methodName, Type... params) {
		visitInvokeVirtual(mv, objectIndex, objectType, Type.VOID_TYPE, methodName, params);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type objectType, Class<?> returnClass, String methodName, Class<?>... classes) {
		visitInvokeVirtual(mv, objectType, Type.getType(returnClass), methodName, toTypes(classes));
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type objectType, String methodName, Type... params) {
		visitInvokeVirtual(mv, objectType, Type.VOID_TYPE, methodName, params);
	}

	public static void visitInvokeVirtual(MethodVisitor mv, Type objectType, Type returnType, String methodName, Class<?>... params) {
		visitInvokeVirtual(mv, objectType, returnType, methodName, toTypes(params));
	}

	public static void visitNewObject(MethodVisitor mv, Class<?> objectClass) {
		visitNewObject(mv, Type.getType(objectClass));
	}

	public static void visitPutField(MethodVisitor mv, int objectIndex, Type objectType, int dataIndex, String fieldName, Class<?> fieldClass) {
		visitPutField(mv, objectIndex, objectType, dataIndex, fieldName, Type.getType(fieldClass));
	}

	// public static void print(byte[] code){
	// ClassReader cr = new ClassReader(code);
	// ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new
	// PrintWriter(System.out));
	// cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	// }
}
