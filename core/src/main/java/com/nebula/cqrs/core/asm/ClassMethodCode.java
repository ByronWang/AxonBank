package com.nebula.cqrs.core.asm;

public interface ClassMethodCode extends ClassThisInstance, MethodCode<ClassUseCaller,ClassMethodCode> {
	ClassThisInstance loadThis();

	ClassType<ClassUseCaller,ClassMethodCode> thisType();

	ClassMethodCode initObject();

	ClassUseCaller useThis();

	final int _THIS = 0;
}
