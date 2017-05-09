package com.nebula.tinyasm.api;

import java.util.function.Consumer;

import org.objectweb.asm.Type;

public interface MethodHeader<C> extends Types, DefineParameter<MethodHeader<C>> {

	MethodHeader<C> annotation(Type type, String value);

	default MethodHeader<C> annotation(Class<?> annotationClass) {
		return annotation(typeOf(annotationClass), null);
	}

	default MethodHeader<C> annotation(Type type) {
		return annotation(type, null);
	}

	C code(Consumer<C> invocation);

	default MethodHeader<C> parameterAnnotation(Class<?> annotationClass) {
		return parameterAnnotation(typeOf(annotationClass), null);
	}

	default MethodHeader<C> parameterAnnotation(Type type) {
		return parameterAnnotation(type, null);
	}

	MethodHeader<C> parameterAnnotation(Type type, Object value);
}