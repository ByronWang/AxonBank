package com.nebula.cqrs.core.asm;

public interface ClassUseCaller extends MethodUseCaller<ClassUseCaller, ClassMethodCode> {
	default public ClassMethodCode putTopTo(String fieldName) {
		return putTopTo(code().fieldOf(fieldName));
	}
}
