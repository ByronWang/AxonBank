package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Type;

public interface ClassThisInstance extends Types, Instance<ClassUseCaller, ClassMethodCode> {
	Field fieldOf(String fieldName);

	default Instance<ClassUseCaller, ClassMethodCode> get(String fieldName) {
		return get(fieldOf(fieldName));
	}

	default Instance<ClassUseCaller, ClassMethodCode> getProperty(String fieldName) {
		return getProperty(fieldOf(fieldName));
	}

	default ClassMethodCode put(int varIndex, String fieldName) {
		return put(varIndex, fieldOf(fieldName));
	}

	default ClassMethodCode put(String varName, String fieldName) {
		return put(code().varIndex(varName), fieldName);
	}

	default ClassMethodCode putTopTo(String fieldName) {
		return code().putTopTo(fieldOf(fieldName));
	}

	default ClassMethodCode putTopTo(String fieldName, Class<?> fieldClass) {
		return putTopTo(new Field(fieldName, typeOf(fieldClass)));
	}

	default ClassMethodCode putTopTo(String fieldName, Type fieldType) {
		return putTopTo(new Field(fieldName, fieldType));
	}
}
