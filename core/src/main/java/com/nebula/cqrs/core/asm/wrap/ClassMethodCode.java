package com.nebula.cqrs.core.asm.wrap;

import com.nebula.cqrs.core.asm.Field;

public interface ClassMethodCode extends MethodCode<ClassUseCaller,ClassMethodCode> {
	ClassThisInstance This();

	ClassType<ClassUseCaller,ClassMethodCode> thisType();
	
	ClassMethodCode initObject();

	ClassUseCaller useThis();
	
	Field fieldOf(String fieldName);

	final int _THIS = 0;
}
