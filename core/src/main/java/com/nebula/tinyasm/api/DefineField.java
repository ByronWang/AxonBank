package com.nebula.tinyasm.api;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface DefineField<CR> extends Types {

	default CR field(Class<?> annotationClass, Field field) {
		return field(typeOf(annotationClass), null, field.name, field.type);
	}

	default CR field(Class<?> annotationClass, Field field, Class<?>... signatureClasses) {
		return field(typeOf(annotationClass), null, field.name, field.type, signatureOf(field.type, signatureClasses));
	}

	default CR field(Class<?> annotationClass, Field field, String signature) {
		return field(typeOf(annotationClass), null, field.name, field.type, signature);
	}

	default CR field(Class<?> annotationClass, Field field, Type... signatureTypes) {
		return field(typeOf(annotationClass), null, field.name, field.type, signatureOf(field.type, signatureTypes));
	}

	default CR field(Class<?> annotationClass, Object value, Field field) {
		return field(typeOf(annotationClass), value, field.name, field.type);
	}

	default CR field(Class<?> annotationClass, Object value, Field field, Class<?>... signatureClasses) {
		return field(typeOf(annotationClass), value, field.name, field.type, signatureOf(field.type, signatureClasses));
	}

	default CR field(Class<?> annotationClass, Object value, Field field, String signature) {
		return field(typeOf(annotationClass), value, field.name, field.type, signature);
	}

	default CR field(Class<?> annotationClass, Object value, Field field, Type... signatureTypes) {
		return field(typeOf(annotationClass), value, field.name, field.type, signatureOf(field.type, signatureTypes));
	}

	default CR field(Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass) {
		return field(typeOf(annotationClass), value, fieldName, typeOf(fieldClass));
	}

	default CR field(Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return field(typeOf(annotationClass), value, fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureClasses));
	}

	default CR field(Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass, String signature) {
		return field(typeOf(annotationClass), value, fieldName, typeOf(fieldClass), signature);
	}

	default CR field(Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return field(typeOf(annotationClass), value, fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureTypes));
	}

	default CR field(Class<?> annotationClass, Object value, String fieldName, Type fieldType) {
		return field(typeOf(annotationClass), value, fieldName, fieldType);
	}

	default CR field(Class<?> annotationClass, Object value, String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return field(typeOf(annotationClass), value, fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR field(Class<?> annotationClass, Object value, String fieldName, Type fieldType, String signature) {
		return field(typeOf(annotationClass), value, fieldName, fieldType, signature);
	}

	default CR field(Class<?> annotationClass, Object value, String fieldName, Type fieldType, Type... signatureTypes) {
		return field(typeOf(annotationClass), value, fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR field(Class<?> annotationClass, String fieldName, Class<?> fieldClass) {
		return field(typeOf(annotationClass), null, fieldName, typeOf(fieldClass));
	}

	default CR field(Class<?> annotationClass, String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return field(typeOf(annotationClass), null, fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureClasses));
	}

	default CR field(Class<?> annotationClass, String fieldName, Class<?> fieldClass, String signature) {
		return field(typeOf(annotationClass), null, fieldName, typeOf(fieldClass), signature);
	}

	default CR field(Class<?> annotationClass, String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return field(typeOf(annotationClass), null, fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureTypes));
	}

	default CR field(Class<?> annotationClass, String fieldName, Type fieldType) {
		return field(typeOf(annotationClass), null, fieldName, fieldType);
	}

	default CR field(Class<?> annotationClass, String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return field(typeOf(annotationClass), null, fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR field(Class<?> annotationClass, String fieldName, Type fieldType, String signature) {
		return field(typeOf(annotationClass), null, fieldName, fieldType, signature);
	}

	default CR field(Class<?> annotationClass, String fieldName, Type fieldType, Type... signatureTypes) {
		return field(typeOf(annotationClass), null, fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR field(Field field) {
		return field(field.name, field.type);
	}

	default CR field(Field field, Class<?>... signatureClasses) {
		return field(field.name, field.type, signatureOf(field.type, signatureClasses));
	}

	default CR field(Field field, String signature) {
		return field(field.name, field.type, signature);
	}

	default CR field(Field field, Type... signatureTypes) {
		return field(field.name, field.type, signatureOf(field.type, signatureTypes));
	}

	CR field(int access, String fieldName, Type fieldType);

	default CR field(int access, String fieldName, Class<?> fieldClass) {
		return field(access, fieldName, typeOf(fieldClass));
	}

	default CR field(int access, Class<?> annotationClass, Object value, String fieldName, Class<?> fieldClass) {
		return field(access, typeOf(annotationClass), value, fieldName, typeOf(fieldClass));
	}

	default CR field(int access, Class<?> annotationClass, String fieldName, Class<?> fieldClass) {
		return field(access, typeOf(annotationClass), null, fieldName, typeOf(fieldClass));
	}

	CR field(int access, String fieldName, Type fieldType, String signature);

	CR field(int access, Type annotationType, Object value, String fieldName, Type fieldType);

	CR field(int access, Type annotationType, Object value, String fieldName, Type fieldType, String signature);

	default CR field(String fieldName, Class<?> fieldClass) {
		return field(fieldName, typeOf(fieldClass));
	}

	default CR field(String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return field(fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureClasses));
	}

	default CR field(String fieldName, Class<?> fieldClass, String signature) {
		return field(fieldName, typeOf(fieldClass), signature);
	}

	default CR field(String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return field(fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureTypes));
	}

	default CR field(String fieldName, Type fieldType) {
		return field(ACC_PRIVATE, fieldName, fieldType);
	}

	default CR field(String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return field(fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR field(String fieldName, Type fieldType, String signature) {
		return field(ACC_PRIVATE, fieldName, fieldType, signature);
	}

	default CR field(String fieldName, Type fieldType, Type... signatureTypes) {
		return field(fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR field(Type annotationType, Field field, Class<?>... signatureClasses) {
		return field(annotationType, null, field.name, field.type, signatureOf(field.type, signatureClasses));
	}

	default CR field(Type annotationType, Field field, String signature) {
		return field(annotationType, null, field.name, field.type, signature);
	}

	default CR field(Type annotationType, Field field, Type... signatureTypes) {
		return field(annotationType, null, field.name, field.type, signatureOf(field.type, signatureTypes));
	}

	default CR field(Type annotationType, Object value, Field field, Class<?>... signatureClasses) {
		return field(annotationType, value, field.name, field.type, signatureOf(field.type, signatureClasses));
	}

	default CR field(Type annotationType, Object value, Field field, String signature) {
		return field(annotationType, value, field.name, field.type, signature);
	}

	default CR field(Type annotationType, Object value, Field field, Type... signatureTypes) {
		return field(annotationType, value, field.name, field.type, signatureOf(field.type, signatureTypes));
	}

	default CR field(Type annotationType, Object value, String fieldName, Class<?> fieldClass) {
		return field(annotationType, value, fieldName, typeOf(fieldClass));
	}

	default CR field(Type annotationType, Object value, String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return field(annotationType, value, fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureClasses));
	}

	default CR field(Type annotationType, Object value, String fieldName, Class<?> fieldClass, String signature) {
		return field(annotationType, value, fieldName, typeOf(fieldClass), signature);
	}

	default CR field(Type annotationType, Object value, String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return field(annotationType, value, fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureTypes));
	}

	default CR field(Type annotationType, Object value, String fieldName, Type fieldType) {
		return field(ACC_PRIVATE, annotationType, value, fieldName, fieldType);
	}

	default CR field(Type annotationType, Object value, String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return field(annotationType, value, fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR field(Type annotationType, Object value, String fieldName, Type fieldType, String signature) {
		return field(ACC_PRIVATE, annotationType, value, fieldName, fieldType, signature);
	}

	default CR field(Type annotationType, Object value, String fieldName, Type fieldType, Type... signatureTypes) {
		return field(annotationType, value, fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}

	default CR field(Type annotationType, String fieldName, Class<?> fieldClass) {
		return field(annotationType, null, fieldName, typeOf(fieldClass));
	}

	default CR field(Type annotationType, String fieldName, Class<?> fieldClass, Class<?>... signatureClasses) {
		return field(annotationType, null, fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureClasses));
	}

	default CR field(Type annotationType, String fieldName, Class<?> fieldClass, String signature) {
		return field(annotationType, null, fieldName, typeOf(fieldClass), signature);
	}

	default CR field(Type annotationType, String fieldName, Class<?> fieldClass, Type... signatureTypes) {
		return field(annotationType, null, fieldName, typeOf(fieldClass), signatureOf(typeOf(fieldClass), signatureTypes));
	}

	default CR field(Type annotationType, String fieldName, Type fieldType, Class<?>... signatureClasses) {
		return field(annotationType, null, fieldName, fieldType, signatureOf(fieldType, signatureClasses));
	}

	default CR field(Type annotationType, String fieldName, Type fieldType, Type... signatureTypes) {
		return field(annotationType, null, fieldName, fieldType, signatureOf(fieldType, signatureTypes));
	}
}