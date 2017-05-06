package com.nebula.cqrs.core.asm;

import java.util.function.Consumer;

import org.objectweb.asm.Type;

interface MethodHeader extends Types {
	MethodHeader annotation(Type type, String value);

	MethodCode code(Consumer<MethodCode> invocation);

	MethodCode code(int line, Consumer<MethodCode> invocation);

	MethodHeader parameter(Field field);

	default MethodHeader parameter(String fieldName, Type fieldType) {
		return parameter(new Field(fieldName, fieldType));
	}

	default MethodHeader parameter(String fieldName, Class<?> clz) {
		return parameter(new Field(fieldName, typeOf(clz)));
	}

	MethodHeader parameterAnnotation(int parameter, Type type, Object value);;
}
