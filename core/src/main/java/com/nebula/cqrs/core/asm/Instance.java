package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface Instance<C> extends InvokeMethod<C>, Types, Opcodes {

	Instance<C> get(Field field);

	default Instance<C> get(String fieldName, Class<?> fieldClass) {
		return get(new Field(fieldName, typeOf(fieldClass)));
	}

	default Instance<C> get(String fieldName, Type fieldType) {
		return get(new Field(fieldName, fieldType));
	}

	Instance<C> getProperty(Field field);

	default Instance<C> getProperty(String fieldName, Type fieldType) {
		return getProperty(new Field(fieldName, fieldType));
	}

	default Instance<C> getProperty(String fieldName, Class<?> fieldClass) {
		return getProperty(new Field(fieldName, typeOf(fieldClass)));
	}

	C put(int dataIndex, Field field);
}