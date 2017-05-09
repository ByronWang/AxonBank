package com.nebula.cqrs.core.asm.wrap;

public interface ClassUseCaller extends MethodUseCaller<ClassUseCaller, ClassMethodCode> {
	default public ClassMethodCode putTo(String fieldName) {
		return putTo(code().fieldOf(fieldName));
	}
}