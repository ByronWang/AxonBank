package com.nebula.tinyasm.api;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface ClassMethodCode extends MethodCode<ClassUseCaller, ClassMethodCode> {
	ClassThisInstance loadThis();

	Type thisType();

	ClassMethodCode initObject();

	ClassUseCaller useThis();

	Field fieldOf(String fieldName);

	final int _THIS = 0;

	ClassUseCaller useTopThis();

}
