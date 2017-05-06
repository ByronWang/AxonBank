package com.nebula.cqrs.core.asm;

import java.util.function.Consumer;

import org.objectweb.asm.Type;

interface MethodHeader<H, C> extends Types {

	H annotation(Type type, String value);

	C code(Consumer<C> invocation);

	C code(int line, Consumer<C> invocation);

	H parameter(Field field);

	H parameterAnnotation(int parameter, Type type, Object value);

	default H parameter(String fieldName, Type fieldType) {
		return parameter(new Field(fieldName, fieldType));
	}

	default H parameter(String fieldName, Class<?> clz) {
		return parameter(new Field(fieldName, typeOf(clz)));
	};
}