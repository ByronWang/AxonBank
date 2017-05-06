package com.nebula.cqrs.core.asm;

public interface ClassMethodCode extends MethodCode<ClassMethodCode, Instance<ClassMethodCode>> {
	ClassMethodCode get(String fieldName);

	ClassMethodCode put(String fieldName);

	ClassMethodCode put(int dataIndex, String fieldName);

	ClassMethodCode getProperty(String fieldName);

	ClassMethodCode initObject();
}
