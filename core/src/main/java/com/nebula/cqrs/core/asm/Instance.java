package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface Instance<M,C> extends InvokeMethod<M,C>, ToType, Types, Opcodes {

	Instance<M,C> get(Field field);

	default Instance<M,C> get(String fieldName, Class<?> fieldClass) {
		return get(new Field(fieldName, typeOf(fieldClass)));
	}

	default Instance<M,C> get(String fieldName, Type fieldType) {
		return get(new Field(fieldName, fieldType));
	}

	Instance<M,C> getProperty(Field field);

	default Instance<M,C> getProperty(String fieldName, Type fieldType) {
		return getProperty(new Field(fieldName, fieldType));
	}

	default Instance<M,C> getProperty(String fieldName, Class<?> fieldClass) {
		return getProperty(new Field(fieldName, typeOf(fieldClass)));
	}

	C put(int dataIndex, Field field);
	
	M  use();
}