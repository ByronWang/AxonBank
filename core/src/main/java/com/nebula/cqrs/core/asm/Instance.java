package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface Instance<C> extends Types, Opcodes {

	C get(Field field);

	C newInstace();

	default C get(String fieldName, Class<?> fieldClass) {
		return get(new Field(fieldName, typeOf(fieldClass)));
	}

	default C get(String fieldName, Type fieldType) {
		return get(new Field(fieldName, fieldType));
	}

	C getProperty(Field field);

	default C getProperty(String fieldName, Type fieldType) {
		return getProperty(new Field(fieldName, fieldType));
	}

	default void invoke(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	void invoke(int invoketype, Type returnType, String methodName, Type... params);

	// default void invoke(String methodName, Type... params) {
	// invoke(INVOKEVIRTUAL, Type.VOID_TYPE, methodName, params);
	// }
	//
	// default void invoke(Type returnType, String methodName, Class<?>...
	// paramClasses) {
	// invoke(INVOKEVIRTUAL, returnType, methodName, typesOf(paramClasses));
	// }

	default void invoke(Type returnType, String methodName, Type... params) {
		invoke(INVOKEVIRTUAL, returnType, methodName, params);
	}

	default void invokeInterface(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		invoke(INVOKEINTERFACE, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeInterface(String methodName, Type... params) {
		invoke(INVOKEINTERFACE, Type.VOID_TYPE, methodName, params);
	}

	default void invokeInterface(Type returnType, String methodName, Class<?>... paramClasses) {
		invoke(INVOKEINTERFACE, returnType, methodName, typesOf(paramClasses));
	}

	default void invokeInterface(Type returnType, String methodName, Type... params) {
		invoke(INVOKEINTERFACE, returnType, methodName, params);
	}

	default void invokeSpecial(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		invoke(INVOKESPECIAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(String methodName, Class<?>... paramClasses) {
		invoke(INVOKESPECIAL, Type.VOID_TYPE, methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(String methodName, Type... params) {
		invoke(INVOKESPECIAL, Type.VOID_TYPE, methodName, params);
	}

	default void invokeSpecial(Type returnType, String methodName, Class<?>... paramClasses) {
		invoke(INVOKESPECIAL, returnType, methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(Type returnType, String methodName, Type... params) {
		invoke(INVOKESPECIAL, returnType, methodName, params);
	}

	default void invokeVirtual(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeVirtual(String methodName, Type... params) {
		invoke(INVOKEVIRTUAL, Type.VOID_TYPE, methodName, params);
	}

	default void invokeVirtual(Type returnType, String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, returnType, methodName, typesOf(paramClasses));
	}

	default void invokeVirtual(Type returnType, String methodName, Type... params) {
		invoke(INVOKEVIRTUAL, returnType, methodName, params);
	}

	C put(Field field);

	C put(int dataIndex, Field field);

	default C put(int dataIndex, String fieldName, Class<?> fieldClass) {
		return put(dataIndex, new Field(fieldName, typeOf(fieldClass)));
	}

	default C put(int dataIndex, String fieldName, Type fieldType) {
		return put(dataIndex, new Field(fieldName, fieldType));
	}

	default C putCurrent(String fieldName, Class<?> fieldClass) {
		return put(new Field(fieldName, typeOf(fieldClass)));
	}

	default C putCurrent(String fieldName, Type fieldType) {
		return put(new Field(fieldName, fieldType));
	}
}