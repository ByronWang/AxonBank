package com.nebula.tinyasm.api;

public interface ClassUseCaller extends MethodUseCaller<ClassUseCaller, ClassMethodCode> {
	default public Instance<ClassUseCaller, ClassMethodCode> get(String fieldName) {
		return get(code().fieldOf(fieldName));
	}

	default public ClassMethodCode putTo(String fieldName) {
		return putTo(code().fieldOf(fieldName));
	}
}
