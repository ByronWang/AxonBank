package com.nebula.cqrs.core.asm.wrap;

import java.util.function.Consumer;

import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.Field;

public interface MethodHeader<C> extends Types {

	MethodHeader<C> annotation(Type type, String value);

	default MethodHeader<C> annotation(Class<?> annotationClass) {
		return annotation(typeOf(annotationClass), null);
	}

	default MethodHeader<C> annotation(Type type) {
		return annotation(type, null);
	}

	C code(Consumer<C> invocation);

	default MethodHeader<C> parameter(Field field) {
		return parameter(field.name, field.type);
	}

	default MethodHeader<C> parameter(String fieldName, Class<?> clz) {
		return parameter(fieldName, typeOf(clz));
	}

	default MethodHeader<C> parameter(String fieldName, Class<?> clz, Class<?>... signatureClasses) {
		return parameter(fieldName, typeOf(clz), typesOf(signatureClasses));
	}

	default MethodHeader<C> parameter(String fieldName, Class<?> clz, String signature) {
		return parameter(fieldName, typeOf(clz), signature);
	}

	default MethodHeader<C> parameter(String fieldName, Class<?> clz, Type... signatureTypes) {
		return parameter(fieldName, typeOf(clz), signatureTypes);
	}

	MethodHeader<C> parameter(String fieldName, Type fieldType, String signature);;

	default MethodHeader<C> parameter(String fieldName, Type fieldType, Type... signatureTypes) {
		String signature = null;
		if (signatureTypes != null && signatureTypes.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("L");
			sb.append(fieldType.getInternalName());
			sb.append("<");
			for (Type signatureType : signatureTypes) {
				sb.append(signatureType.getDescriptor());
			}
			sb.append(">;");
			signature = sb.toString();
		}
		return parameter(fieldName, fieldType, signature);
	};

	default MethodHeader<C> parameterAnnotation(Class<?> annotationClass) {
		return parameterAnnotation(typeOf(annotationClass), null);
	}

	default MethodHeader<C> parameterAnnotation(Type type) {
		return parameterAnnotation(type, null);
	}

	MethodHeader<C> parameterAnnotation(Type type, Object value);
}