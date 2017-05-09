package com.nebula.tinyasm.api;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface DefineParameter<CR> extends Types {
	CR parameter(String fieldName, Type fieldType);

	CR parameter(String fieldName, Type fieldType, String signature);

	CR parameter(Type annotationType, Object value, String fieldName, Type fieldType);

	CR parameter(Type annotationType, Object value, String fieldName, Type fieldType, String signature);

	default CR parameter(String fieldName, Class<?> fieldClass) {
		return parameter(fieldName, typeOf(fieldClass));
	}

	default CR parameter(Field parameter) {
		return parameter(parameter.name, parameter.type);
	}

	default CR parameter(String fieldName, Type fieldType, Type... signatureTypes) {
		return parameter(fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR parameter(String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return parameter(fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureTypes));
	}

	default CR parameter(Type annotationType, Object value, String fieldName, Type fieldType, Type... signatureTypes) {
		return parameter(annotationType, value, fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, Object value, String fieldName, Type fieldType, Type... signatureTypes) {
		return parameter(Type.getType(annotationClass), value, fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR parameter(Type annotationType, Object value, String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return parameter(annotationType, value, fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return parameter(Type.getType(annotationClass), value, fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, Object value, String fieldName, Type fieldType) {
		return parameter(Type.getType(annotationClass), value, fieldName, fieldType);
	}

	default CR parameter(Type annotationType, Object value, String fieldName, Class<?> fieldClass) {
		return parameter(annotationType, value, fieldName, Type.getType(fieldClass));
	}

	default CR parameter(Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass) {
		return parameter(Type.getType(annotationClass), value, fieldName, Type.getType(fieldClass));
	}

	default CR parameter(Field parameter, Type... signatureTypes) {
		return parameter(parameter.name, parameter.type, signatureOf(parameter.type, signatureTypes));
	}

	default CR parameter(Type annotationType, Object value, Field parameter, Type... signatureTypes) {
		return parameter(annotationType, value, parameter.name, parameter.type, signatureOf(parameter.type, signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, Object value, Field parameter, Type... signatureTypes) {
		return parameter(Type.getType(annotationClass), value, parameter.name, parameter.type, signatureOf(parameter.type, signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, Object value, Field parameter) {
		return parameter(Type.getType(annotationClass), value, parameter.name, parameter.type);
	}

	default CR parameter(String fieldName, Class<?> fieldClass, String signature) {
		return parameter(fieldName, Type.getType(fieldClass), signature);
	}

	default CR parameter(Class<?> annotationClass, Object value, String fieldName, Type fieldType, String signature) {
		return parameter(Type.getType(annotationClass), value, fieldName, fieldType, signature);
	}

	default CR parameter(Type annotationType, Object value, String fieldName, Class<?> fieldClass, String signature) {
		return parameter(annotationType, value, fieldName, Type.getType(fieldClass), signature);
	}

	default CR parameter(Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass, String signature) {
		return parameter(Type.getType(annotationClass), value, fieldName, Type.getType(fieldClass), signature);
	}

	default CR parameter(Field parameter, String signature) {
		return parameter(parameter.name, parameter.type, signature);
	}

	default CR parameter(Type annotationType, Object value, Field parameter, String signature) {
		return parameter(annotationType, value, parameter.name, parameter.type, signature);
	}

	default CR parameter(Class<?> annotationClass, Object value, Field parameter, String signature) {
		return parameter(Type.getType(annotationClass), value, parameter.name, parameter.type, signature);
	}

	default CR parameter(String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return parameter(fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR parameter(String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return parameter(fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureClasses));
	}

	default CR parameter(Type annotationType, Object value, String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return parameter(annotationType, value, fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR parameter(Class<?> annotationClass, Object value, String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return parameter(Type.getType(annotationClass), value, fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR parameter(Type annotationType, Object value, String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return parameter(annotationType, value, fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureClasses));
	}

	default CR parameter(Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return parameter(Type.getType(annotationClass), value, fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureClasses));
	}

	default CR parameter(Field parameter, Class<?>... signatureClasses) {
		return parameter(parameter.name, parameter.type, signatureOf(parameter.type, signatureClasses));
	}

	default CR parameter(Type annotationType, Object value, Field parameter, Class<?>... signatureClasses) {
		return parameter(annotationType, value, parameter.name, parameter.type, signatureOf(parameter.type, signatureClasses));
	}

	default CR parameter(Class<?> annotationClass, Object value, Field parameter, Class<?>... signatureClasses) {
		return parameter(Type.getType(annotationClass), value, parameter.name, parameter.type, signatureOf(parameter.type, signatureClasses));
	}

	default CR parameter(Type annotationType, String fieldName, Type fieldType, Type... signatureTypes) {
		return parameter(annotationType, null, fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, String fieldName, Type fieldType, Type... signatureTypes) {
		return parameter(Type.getType(annotationClass), null, fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR parameter(Type annotationType, String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return parameter(annotationType, null, fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return parameter(Type.getType(annotationClass), null, fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, String fieldName, Type fieldType) {
		return parameter(Type.getType(annotationClass), null, fieldName, fieldType);
	}

	default CR parameter(Type annotationType, String fieldName, Class<?> fieldClass) {
		return parameter(annotationType, null, fieldName, Type.getType(fieldClass));
	}

	default CR parameter(Class<?> annotationClass, String fieldName, Class<?> fieldClass) {
		return parameter(Type.getType(annotationClass), null, fieldName, Type.getType(fieldClass));
	}

	default CR parameter(Type annotationType, Field parameter, Type... signatureTypes) {
		return parameter(annotationType, null, parameter.name, parameter.type, signatureOf(parameter.type, signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, Field parameter, Type... signatureTypes) {
		return parameter(Type.getType(annotationClass), null, parameter.name, parameter.type, signatureOf(parameter.type, signatureTypes));
	}

	default CR parameter(Class<?> annotationClass, Field parameter) {
		return parameter(Type.getType(annotationClass), null, parameter.name, parameter.type);
	}

	default CR parameter(Class<?> annotationClass, String fieldName, Type fieldType, String signature) {
		return parameter(Type.getType(annotationClass), null, fieldName, fieldType, signature);
	}

	default CR parameter(Type annotationType, String fieldName, Class<?> fieldClass, String signature) {
		return parameter(annotationType, null, fieldName, Type.getType(fieldClass), signature);
	}

	default CR parameter(Class<?> annotationClass, String fieldName, Class<?> fieldClass, String signature) {
		return parameter(Type.getType(annotationClass), null, fieldName, Type.getType(fieldClass), signature);
	}

	default CR parameter(Type annotationType, Field parameter, String signature) {
		return parameter(annotationType, null, parameter.name, parameter.type, signature);
	}

	default CR parameter(Class<?> annotationClass, Field parameter, String signature) {
		return parameter(Type.getType(annotationClass), null, parameter.name, parameter.type, signature);
	}

	default CR parameter(Type annotationType, String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return parameter(annotationType, null, fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR parameter(Class<?> annotationClass, String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return parameter(Type.getType(annotationClass), null, fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR parameter(Type annotationType, String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return parameter(annotationType, null, fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureClasses));
	}

	default CR parameter(Class<?> annotationClass, String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return parameter(Type.getType(annotationClass), null, fieldName, Type.getType(fieldClass), signatureOf(Type.getType(fieldClass), signatureClasses));
	}

	default CR parameter(Type annotationType, Field parameter, Class<?>... signatureClasses) {
		return parameter(annotationType, null, parameter.name, parameter.type, signatureOf(parameter.type, signatureClasses));
	}

	default CR parameter(Class<?> annotationClass, Field parameter, Class<?>... signatureClasses) {
		return parameter(Type.getType(annotationClass), null, parameter.name, parameter.type, signatureOf(parameter.type, signatureClasses));
	}
}