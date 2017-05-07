package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface InvokeMethod<C> extends Types, Opcodes {

	default void invoke(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	C putTopTo(Field field);

	default C putTopTo(String fieldName, Class<?> fieldClass) {
		return putTopTo(new Field(fieldName, typeOf(fieldClass)));
	}

	default C putTopTo(String fieldName, Type fieldType) {
		return putTopTo(new Field(fieldName, fieldType));
	}
	
	void invoke(int invoketype, String methodName, Type... params);

	Instance<C> invoke(int invoketype, Type returnType, String methodName, Type... params);

	// default void invoke(String methodName, Type... params) {
	// invoke(INVOKEVIRTUAL, methodName, params);
	// }
	//
	// default void invoke(Type returnType, String methodName, Class<?>...
	// paramClasses) {
	// invoke(INVOKEVIRTUAL, returnType, methodName, typesOf(paramClasses));
	// // }
	//
	// default Instance<C> invoke(Type returnType, String methodName, Type...
	// params) {
	// return invoke(INVOKEVIRTUAL, returnType, methodName, params);
	// }

	default Instance<C> invokeInterface(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEINTERFACE, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeInterface(String methodName, Class<?>... paramClasses) {
		invoke(INVOKEINTERFACE, methodName, typesOf(paramClasses));
	}

	default void invokeInterface(String methodName, Type... params) {
		invoke(INVOKEINTERFACE, methodName, params);
	}

	default Instance<C> invokeInterface(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEINTERFACE, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<C> invokeInterface(Type returnType, String methodName, Type... params) {
		return invoke(INVOKEINTERFACE, returnType, methodName, params);
	}

	default Instance<C> invokeSpecial(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKESPECIAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(String methodName, Class<?>... paramClasses) {
		invoke(INVOKESPECIAL, methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(String methodName, Type... params) {
		invoke(INVOKESPECIAL, methodName, params);
	}

	default Instance<C> invokeSpecial(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKESPECIAL, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<C> invokeSpecial(Type returnType, String methodName, Type... params) {
		return invoke(INVOKESPECIAL, returnType, methodName, params);
	}

	default Instance<C> invokeVirtual(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeVirtual(String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, methodName, typesOf(paramClasses));
	}

	default void invokeVirtual(String methodName, Type... params) {
		invoke(INVOKEVIRTUAL, methodName, params);
	}

	default Instance<C> invokeVirtual(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEVIRTUAL, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<C> invokeVirtual(Type returnType, String methodName, Type... params) {
		return invoke(INVOKEVIRTUAL, returnType, methodName, params);
	}
}
