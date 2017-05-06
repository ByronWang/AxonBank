package com.nebula.cqrs.core.asm;

public interface ClassThisInstance extends Types, Instance<ClassMethodCode> {
	ClassMethodCode get(String fieldName);
	ClassMethodCode put(String fieldName);	
	ClassMethodCode put(int dataIndex,String fieldName);	
	ClassMethodCode getProperty(String fieldName);
//	MethodCode putProperty(String fieldName);
}
