package com.nebula.tinyasm.api;

import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.List;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface InvokeMethod<M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>> extends Types, ToType {

	C code();

	Instance<M, C> get(Field field);

	default Instance<M, C> getProperty(String fieldName, Type fieldType) {
		return invoke(INVOKEVIRTUAL, fieldType, toPropertyGetName(fieldName, fieldType));
	}

	default void invoke(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invoke(int invoketype, String methodName, Type... params) {
		invoke(invoketype, Type.VOID_TYPE, methodName, params);
	}

	default Instance<M, C> invoke(int invoketype, Type returnType, String methodName, Type... params) {
		invoke(getType(), invoketype, returnType, methodName, params);
		return topInstance();
	}

	default void invoke(Type objectType, int invoketype, String methodName, Type... params) {
		invoke(objectType, invoketype, Type.VOID_TYPE, methodName, params);
	}

	void invoke(Type objectType, int invoketype, Type returnType, String methodName, Type... params);

	default Instance<M, C> invokeInterface(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEINTERFACE, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeInterface(Class<?> returnClass, String methodName, Type... params) {
		return invoke(INVOKEINTERFACE, typeOf(returnClass), methodName, params);
	}

	default void invokeInterface(String methodName, Class<?>... paramClasses) {
		invoke(INVOKEINTERFACE, methodName, typesOf(paramClasses));
	}

	default void invokeInterface(String methodName, Type... params) {
		invoke(INVOKEINTERFACE, methodName, params);
	}

	default Instance<M, C> invokeInterface(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEINTERFACE, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeInterface(Type returnType, String methodName, Type... params) {
		return invoke(INVOKEINTERFACE, returnType, methodName, params);
	}

	default Instance<M, C> invokeSpecial(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKESPECIAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(String methodName, Class<?>... paramClasses) {
		invoke(INVOKESPECIAL, methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(String methodName, Field... paramFields) {
		invoke(INVOKESPECIAL, methodName, typesOf(paramFields));
	}

	default void invokeSpecial(String methodName, List<Field> paramFields) {
		invoke(INVOKESPECIAL, methodName, typesOf(paramFields));
	}

	default void invokeSpecial(String methodName, Type... params) {
		invoke(INVOKESPECIAL, methodName, params);
	}

	default Instance<M, C> invokeSpecial(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKESPECIAL, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeSpecial(Type returnType, String methodName, Type... params) {
		return invoke(INVOKESPECIAL, returnType, methodName, params);
	}

	default Instance<M, C> invokeVirtual(Class<?> returnClass, String methodName) {
		return invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName);
	}

	default Instance<M, C> invokeVirtual(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeVirtual(Class<?> returnClass, String methodName, Type... params) {
		return invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, params);
	}

	default void invokeVirtual(String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, methodName, typesOf(paramClasses));
	}

	default void invokeVirtual(String methodName, Field... paramFields) {
		invoke(INVOKEVIRTUAL, methodName, typesOf(paramFields));
	}

	default void invokeVirtual(String methodName, List<Field> paramFields) {
		invoke(INVOKEVIRTUAL, methodName, typesOf(paramFields));
	}

	default void invokeVirtual(String methodName, Type... params) {
		invoke(INVOKEVIRTUAL, methodName, params);
	}

	default Instance<M, C> invokeVirtual(Type returnType, String methodName) {
		return invoke(INVOKEVIRTUAL, returnType, methodName);
	}

	default Instance<M, C> invokeVirtual(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEVIRTUAL, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeVirtual(Type returnType, String methodName, Field... paramFields) {
		return invoke(INVOKEVIRTUAL, returnType, methodName, typesOf(paramFields));
	}

	default Instance<M, C> invokeVirtual(Type returnType, String methodName, Type... params) {
		return invoke(INVOKEVIRTUAL, returnType, methodName, params);
	}

	default Instance<M, C> invokeStatic(Class<?> returnClass, String methodName) {
		return invoke(INVOKESTATIC, typeOf(returnClass), methodName);
	}

	default Instance<M, C> invokeStatic(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKESTATIC, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeStatic(Class<?> returnClass, String methodName, Type... params) {
		return invoke(INVOKESTATIC, typeOf(returnClass), methodName, params);
	}

	default void invokeStatic(String methodName, Class<?>... paramClasses) {
		invoke(INVOKESTATIC, methodName, typesOf(paramClasses));
	}

	default void invokeStatic(String methodName, Field... paramFields) {
		invoke(INVOKESTATIC, methodName, typesOf(paramFields));
	}

	default void invokeStatic(String methodName, List<Field> paramFields) {
		invoke(INVOKESTATIC, methodName, typesOf(paramFields));
	}

	default void invokeStatic(String methodName, Type... params) {
		invoke(INVOKESTATIC, methodName, params);
	}

	default Instance<M, C> invokeStatic(Type returnType, String methodName) {
		return invoke(INVOKESTATIC, returnType, methodName);
	}

	default Instance<M, C> invokeStatic(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKESTATIC, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeStatic(Type returnType, String methodName, Field... paramFields) {
		return invoke(INVOKESTATIC, returnType, methodName, typesOf(paramFields));
	}

	default Instance<M, C> invokeStatic(Type returnType, String methodName, Type... params) {
		return invoke(INVOKESTATIC, returnType, methodName, params);
	}

	C putTo(Field field);

	default C putTo(String fieldName, Class<?> fieldClass) {
		return putTo(new Field(fieldName, typeOf(fieldClass)));
	}

	default C putTo(String fieldName, Type fieldType) {
		return putTo(new Field(fieldName, fieldType));
	}

	default Instance<M, C> topInstance() {
		return code().type(getType());
	}

}
