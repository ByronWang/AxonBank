package com.nebula.tinyasm.api;

import com.nebula.tinyasm.util.Field;

public interface ClassMethodCode extends InstanceMethodCode<ClassUseCaller, ClassMethodCode> {

	Field fieldOf(String fieldName);

	ClassMethodCode initObject();
	
	ClassThisInstance loadThis();

	default ClassMethodCode putTopTo(String fieldName) {
		return putTopTo(fieldOf(fieldName));
	};
}
