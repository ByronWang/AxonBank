package com.nebula.tinyasm.api;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.Type;

public interface DefineMethod extends Types {

	MethodHeader<ClassMethodCode> method(int access, Type returnType, String methodName, Class<?>... exceptionClasses);

	default MethodHeader<ClassMethodCode> method(int access, Class<?> returnClass, String methodName, Class<?>... exceptionClasses) {
		return method(access, Type.getType(returnClass), methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> privateMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PRIVATE, typeOf(returnClass), methodName);
	}

	default MethodHeader<ClassMethodCode> privateMethod(Class<?> returnClass, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PRIVATE, typeOf(returnClass), methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> privateMethod(String methodName) {
		return method(ACC_PRIVATE, Type.VOID_TYPE, methodName);
	}

	default MethodHeader<ClassMethodCode> privateMethod(String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PRIVATE, Type.VOID_TYPE, methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> privateMethod(Type returnType, String methodName) {
		return method(ACC_PRIVATE, returnType, methodName);
	}

	default MethodHeader<ClassMethodCode> privateMethod(Type returnType, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PRIVATE, returnType, methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> protectdMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PROTECTED, typeOf(returnClass), methodName);
	}

	default MethodHeader<ClassMethodCode> protectdMethod(Class<?> returnClass, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PROTECTED, typeOf(returnClass), methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> protectdMethod(String methodName) {
		return method(ACC_PROTECTED, Type.VOID_TYPE, methodName);
	}

	default MethodHeader<ClassMethodCode> protectdMethod(String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PROTECTED, Type.VOID_TYPE, methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> protectdMethod(Type returnType, String methodName) {
		return method(ACC_PROTECTED, returnType, methodName);
	}

	default MethodHeader<ClassMethodCode> protectdMethod(Type returnType, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PROTECTED, returnType, methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> publicMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PUBLIC, typeOf(returnClass), methodName);
	}

	default MethodHeader<ClassMethodCode> publicMethod(Class<?> returnClass, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PUBLIC, typeOf(returnClass), methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> publicMethod(String methodName) {
		return method(ACC_PUBLIC, Type.VOID_TYPE, methodName);
	}

	default MethodHeader<ClassMethodCode> publicMethod(String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PUBLIC, Type.VOID_TYPE, methodName, exceptionClasses);
	}

	default MethodHeader<ClassMethodCode> publicMethod(Type returnType, String methodName) {
		return method(ACC_PUBLIC, returnType, methodName);
	}

	default MethodHeader<ClassMethodCode> publicMethod(Type returnType, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PUBLIC, returnType, methodName, exceptionClasses);
	}

}
