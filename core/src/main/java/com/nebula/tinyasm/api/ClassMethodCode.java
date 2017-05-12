package com.nebula.tinyasm.api;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface ClassMethodCode extends MethodCode<ClassUseCaller, ClassMethodCode> {
	final int _THIS = 0;

	Field fieldOf(String fieldName);

	ClassMethodCode initObject();

	ClassThisInstance loadThis();

	Type thisType();

	ClassUseCaller useThis();

	ClassUseCaller useTopThis();

	default ClassMethodCode putTopTo(String fieldName) {
		return putTopTo(fieldOf(fieldName));
	};
}
