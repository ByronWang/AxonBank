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

	ClassBody field(Field field);

	ClassBody field(Field field, Type annotationType, Object value);

	default ClassBody field(String name, Class<?> fieldClass) {
		return field(new Field(name, typeOf(fieldClass)));
	}

	default ClassBody field(String name, Class<?> fieldClass, Class<?> annotationClass) {
		return field(new Field(name, typeOf(fieldClass)), typeOf(annotationClass), null);
	}

	default ClassBody field(String name, Class<?> fieldClass, Class<?> annotationClass, Object value) {
		return field(new Field(name, typeOf(fieldClass)), typeOf(annotationClass), value);
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

	MethodHeader method(int access, Type returnType, String methodName);

	default MethodHeader privateMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PRIVATE, typeOf(returnClass), methodName);
	}

	default MethodHeader privateMethod(String methodName) {
		return method(ACC_PRIVATE, Type.VOID_TYPE, methodName);
	}

	default MethodHeader privateMethod(Type returnType, String methodName) {
		return method(ACC_PRIVATE, returnType, methodName);
	}

	default MethodHeader protectdMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PROTECTED, typeOf(returnClass), methodName);
	}

	default MethodHeader protectdMethod(String methodName) {
		return method(ACC_PROTECTED, Type.VOID_TYPE, methodName);
	}

	default MethodHeader protectdMethod(Type returnType, String methodName) {
		return method(ACC_PROTECTED, returnType, methodName);
	}

	default MethodHeader publicMethod(Class<?> returnClass, String methodName) {
		return method(ACC_PUBLIC, typeOf(returnClass), methodName);
	}

	default MethodHeader publicMethod(String methodName) {
		return method(ACC_PUBLIC, Type.VOID_TYPE, methodName);
	}

	default MethodHeader publicMethod(Type returnType, String methodName) {
		return method(ACC_PUBLIC, returnType, methodName);
	}

}