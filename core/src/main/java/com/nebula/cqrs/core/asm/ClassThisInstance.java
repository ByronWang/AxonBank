package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Type;

public interface ClassThisInstance extends Types, Instance<ClassMethodCode> {
	Instance<ClassMethodCode> get(String fieldName);

	ClassMethodCode put(int dataIndex, String fieldName);

	Instance<ClassMethodCode> getProperty(String fieldName);

	ClassMethodCode putTopTo(Field field);

	ClassMethodCode putTopTo(String fieldName);

	default ClassMethodCode putTopTo(String fieldName, Class<?> fieldClass) {
		return putTopTo(new Field(fieldName, typeOf(fieldClass)));
	}

	default ClassMethodCode putTopTo(String fieldName, Type fieldType) {
		return putTopTo(new Field(fieldName, fieldType));
	}
}
