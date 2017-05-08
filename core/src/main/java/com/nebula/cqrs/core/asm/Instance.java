package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface Instance<M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>> extends InvokeMethod<M, C>, ToType, Types, Opcodes {

	Instance<M, C> get(Field field);

	default Instance<M, C> get(String fieldName, Class<?> fieldClass) {
		return get(new Field(fieldName, typeOf(fieldClass)));
	}

	default Instance<M, C> get(String fieldName, Type fieldType) {
		return get(new Field(fieldName, fieldType));
	}

	default Instance<M, C> getProperty(Field field) {
		return null;
	}

	default Instance<M, C> getProperty(String fieldName, Type fieldType) {
		return getProperty(new Field(fieldName, fieldType));
	}

	default Instance<M, C> getProperty(String fieldName, Class<?> fieldClass) {
		return getProperty(new Field(fieldName, typeOf(fieldClass)));
	}

	C put(int varIndex, Field field);

	default C put(String varName, Field field) {
		return put(code().varIndex(varName), field);
	}

	C code();

	default void store(String varName) {
		code().storeTop(varName);
	}

	default void store(int varIndex) {
		code().storeTop(varIndex);
	}

	M use();
}