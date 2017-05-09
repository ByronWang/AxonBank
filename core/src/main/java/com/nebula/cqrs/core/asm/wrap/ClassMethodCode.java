package com.nebula.cqrs.core.asm.wrap;

import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.Field;

public interface ClassMethodCode extends MethodCode<ClassUseCaller, ClassMethodCode> {
	ClassThisInstance loadThis();

	Type thisType();

	ClassMethodCode initObject();

	ClassUseCaller useThis();

	Field fieldOf(String fieldName);

	final int _THIS = 0;

	ClassUseCaller useTopThis();
}
