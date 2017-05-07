package com.nebula.cqrs.core.asm;

public interface ClassUseCaller extends MethodUseCaller<ClassUseCaller, ClassMethodCode> {
	ClassMethodCode putTopTo(String fieldName);
}
