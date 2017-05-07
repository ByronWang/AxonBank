package com.nebula.cqrs.core.asm;

public interface ClassMethodCode extends ClassThisInstance, MethodCode<ClassMethodCode> {
	ClassThisInstance loadThis();

	ClassType<ClassMethodCode> thisType();

	ClassMethodCode initObject();
}
