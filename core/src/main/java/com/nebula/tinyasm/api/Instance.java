package com.nebula.tinyasm.api;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface Instance<M, C extends MethodCode<M, C>> extends InvokeMethod<M, C>, ToType, Types, Opcodes {

	C code();

	Instance<M, C> get(Field field);

	default Instance<M, C> get(String fieldName, Class<?> fieldClass) {
		return get(new Field(fieldName, typeOf(fieldClass)));
	}

	default Instance<M, C> get(String fieldName, Type fieldType) {
		return get(new Field(fieldName, fieldType));
	}

	default Instance<M, C> getProperty(Field field) {
		return getProperty(field.name, field.type);
	}

	default Instance<M, C> getProperty(String fieldName, Class<?> fieldClass) {
		return getProperty(new Field(fieldName, typeOf(fieldClass)));
	}

	C put(int varIndex, Field field);

	default C put(String varName, Field field) {
		return put(code().varIndex(varName), field);
	}

	default void store(int varIndex) {
		code().storeTop(varIndex);
	}

	default void store(String varName) {
		code().storeTop(varName);
	}

	M use();
}