package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface ClassBody extends Types, Opcodes {

	default ClassBody annotation(Class<?> annotationClass) {
		return annotation(typeOf(annotationClass));
	}

	default ClassBody annotation(Class<?> annotationClass, Object value) {
		return annotation(typeOf(annotationClass), value);
	}

	default ClassBody annotation(Type annotationType) {
		return annotation(annotationType, null);
	}

	ClassBody annotation(Type annotationType, Object value);

	void end();

	default ClassBody field(Field field) {
		return field(field, null, null, null);
	}

	default ClassBody field(Field field, String signature) {
		return field(field, signature, null, null);
	}

	default ClassBody field(Field field, Type annotationType, Object value) {
		return field(null, annotationType, annotationType, value);
	}

	ClassBody field(Field field, String signature, Type annotationType, Object value);

	default ClassBody field(String name, Class<?> fieldClass) {
		return field(new Field(name, typeOf(fieldClass)));
	}

	default ClassBody field(String name, Class<?> fieldClass, Class<?> annotationClass) {
		return field(new Field(name, typeOf(fieldClass)), typeOf(annotationClass), null);
	}

	default ClassBody field(String name, Class<?> fieldClass, Class<?> annotationClass, Object value) {
		return field(new Field(name, typeOf(fieldClass)), typeOf(annotationClass), value);
	}

	default ClassBody field(String name, Class<?> fieldClass, String signature) {
		return field(new Field(name, typeOf(fieldClass)), signature, null, null);
	}

	default ClassBody field(String name, Class<?> fieldClass, String signature, Class<?> annotationClass) {
		return field(new Field(name, typeOf(fieldClass)), signature, typeOf(annotationClass), null);
	}

	default ClassBody field(String name, Class<?> fieldClass, String signature, Class<?> annotationClass, Object value) {
		return field(new Field(name, typeOf(fieldClass)), signature, typeOf(annotationClass), value);
	}

	default ClassBody field(String name, Type type) {
		return field(new Field(name, type));
	}

	default ClassBody field(String name, Type fieldType, Class<?> annotationClass, Object value) {
		return field(new Field(name, fieldType), typeOf(annotationClass), value);
	}

	default ClassBody field(String name, Type fieldType, Type annotationType, Object value) {
		return field(new Field(name, annotationType), annotationType, value);
	}

	ClassMethodHeader method(int access, Type returnType, String methodName, Class<?>... exceptionClasses);

	default ClassMethodHeader privateMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PRIVATE, typeOf(returnClass), methodName);
	}

	default ClassMethodHeader privateMethod(String methodName) {
		return method(ACC_PRIVATE, Type.VOID_TYPE, methodName);
	}

	default ClassMethodHeader privateMethod(Type returnType, String methodName) {
		return method(ACC_PRIVATE, returnType, methodName);
	}

	default ClassMethodHeader protectdMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PROTECTED, typeOf(returnClass), methodName);
	}

	default ClassMethodHeader protectdMethod(String methodName) {
		return method(ACC_PROTECTED, Type.VOID_TYPE, methodName);
	}

	default ClassMethodHeader protectdMethod(Type returnType, String methodName) {
		return method(ACC_PROTECTED, returnType, methodName);
	}

	default ClassMethodHeader publicMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PUBLIC, typeOf(returnClass), methodName);
	}

	default ClassMethodHeader publicMethod(String methodName) {
		return method(ACC_PUBLIC, Type.VOID_TYPE, methodName);
	}

	default ClassMethodHeader publicMethod(Type returnType, String methodName) {
		return method(ACC_PUBLIC, returnType, methodName);
	}

	default ClassMethodHeader privateMethod(Class<?> returnClass, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PRIVATE, typeOf(returnClass), methodName, exceptionClasses);
	}

	default ClassMethodHeader privateMethod(String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PRIVATE, Type.VOID_TYPE, methodName, exceptionClasses);
	}

	default ClassMethodHeader privateMethod(Type returnType, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PRIVATE, returnType, methodName, exceptionClasses);
	}

	default ClassMethodHeader protectdMethod(Class<?> returnClass, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PROTECTED, typeOf(returnClass), methodName, exceptionClasses);
	}

	default ClassMethodHeader protectdMethod(String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PROTECTED, Type.VOID_TYPE, methodName, exceptionClasses);
	}

	default ClassMethodHeader protectdMethod(Type returnType, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PROTECTED, returnType, methodName, exceptionClasses);
	}

	default ClassMethodHeader publicMethod(Class<?> returnClass, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PUBLIC, typeOf(returnClass), methodName, exceptionClasses);
	}

	default ClassMethodHeader publicMethod(String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PUBLIC, Type.VOID_TYPE, methodName, exceptionClasses);
	}

	default ClassMethodHeader publicMethod(Type returnType, String methodName, Class<?>... exceptionClasses) {
		return method(ACC_PUBLIC, returnType, methodName, exceptionClasses);
	}

}