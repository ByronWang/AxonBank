package com.nebula.cqrs.core.asm;

import java.util.function.Consumer;

import org.objectweb.asm.Type;

interface MethodHeader<H, C> extends Types {

	H annotation(Type type, String value);

	default H annotation(Class<?> annotationClass) {
		return annotation(typeOf(annotationClass), null);
	}

	default H annotation(Type type) {
		return annotation(type, null);
	}

	C code(Consumer<C> invocation);

	default H parameter(Field field) {
		return parameter(field.name, field.type);
	}

	default H parameter(String fieldName, Class<?> clz) {
		return parameter(fieldName, typeOf(clz));
	}

	default H parameter(String fieldName, Class<?> clz, Class<?>... signatureClasses) {
		return parameter(fieldName, typeOf(clz), typesOf(signatureClasses));
	}

	default H parameter(String fieldName, Class<?> clz, String signature) {
		return parameter(fieldName, typeOf(clz), signature);
	}

	default H parameter(String fieldName, Class<?> clz, Type... signatureTypes) {
		return parameter(fieldName, typeOf(clz), signatureTypes);
	}

	H parameter(String fieldName, Type fieldType, String signature);;

	default H parameter(String fieldName, Type fieldType, Type... signatureTypes) {
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

	default H parameterAnnotation(Class<?> annotationClass) {
		return parameterAnnotation(typeOf(annotationClass), null);
	}

	default H parameterAnnotation(Type type) {
		return parameterAnnotation(type, null);
	}

	H parameterAnnotation(Type type, Object value);
}