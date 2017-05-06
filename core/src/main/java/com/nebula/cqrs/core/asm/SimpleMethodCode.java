package com.nebula.cqrs.core.asm;

interface SimpleMethodCode extends Types, MethodCode<SimpleMethodCode, Instance<SimpleMethodCode>> {
	@Override
	default SimpleMethodCode localVariable(String name, Class<?> clz) {
		return localVariable(name, typeOf(clz));
	}

	@Override
	default SimpleMethodCode returnType(Class<?> returnClass) {
		return returnType(typeOf(returnClass));
	}
}
