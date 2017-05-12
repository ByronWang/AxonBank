package com.nebula.tinyasm.api;

import java.util.function.Consumer;

import org.objectweb.asm.Type;

public interface MethodHeader<C> extends Types, DefineParameter<MethodHeader<C>> {

	default MethodHeader<C> annotation(Class<?> annotationClass) {
		return annotation(typeOf(annotationClass), null);
	}

	default MethodHeader<C> annotation(Class<?> annotationClass, Object value) {
		return annotation(typeOf(annotationClass), value);
	}

	default MethodHeader<C> annotation(Type type) {
		return annotation(type, null);
	}

	default MethodHeader<C> annotation(Class<?> annotationClass, String name, Object value) {
		return annotation(typeOf(annotationClass), name, value);
	}

	MethodHeader<C> annotation(Type type, String name, Object value);

	MethodHeader<C> annotation(Type type, Object value);

	void code(Consumer<C> invocation);

	default MethodHeader<C> parameterAnnotation(Class<?> annotationClass) {
		return parameterAnnotation(typeOf(annotationClass), null);
	}

	default MethodHeader<C> parameterAnnotation(Type type) {
		return parameterAnnotation(type, null);
	}

	MethodHeader<C> parameterAnnotation(Type type, Object value);

	C begin();
}