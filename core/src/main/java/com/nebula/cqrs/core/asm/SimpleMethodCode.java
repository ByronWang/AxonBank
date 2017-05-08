package com.nebula.cqrs.core.asm;

interface SimpleMethodCode extends Types, MethodCode<SimpleUseCaller<SimpleMethodCode>, SimpleMethodCode> {
	@Override
	default SimpleMethodCode def(String name, Class<?> clz) {
		return def(name, typeOf(clz));
	}

	@Override
	default void returnTop(Class<?> returnClass) {
		returnTop(typeOf(returnClass));
	}
}
