package com.nebula.tinyasm.api;

import java.util.List;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface Types {
	default Type typeOf(Class<?> clz) {
		return Type.getType(clz);
	}

	default String toPropertyGetName(String fieldName,Type FieldType) {
		return "get" + toPropertyName(fieldName);
	}

	default String toPropertySetName(String fieldName,Type FieldType) {
		return "set" + toPropertyName(fieldName);
	}

	default String toPropertyName(String fieldName) {
		return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}

	default String toSimpleName(String name) {
		int index = name.lastIndexOf('.');
		if (index < 0) index = name.lastIndexOf('/');

		return name.substring(index + 1);
	}

	default Type[] typesOf(Class<?>... classes) {
		Type[] types = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			types[i] = Type.getType(classes[i]);
		}
		return types;
	}

	default Type[] typesOf(Field... fields) {
		Type[] types = new Type[fields.length];
		for (int i = 0; i < fields.length; i++) {
			types[i] = fields[i].type;
		}
		return types;
	}

	default Type[] typesOf(List<Field> fields) {
		Type[] types = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			types[i] = fields.get(i).type;
		}
		return types;
	}
	default String signatureOf(Type type, Type... signatureTypes) {
		String signature = null;
		if (signatureTypes != null && signatureTypes.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("L");
			sb.append(type.getInternalName());
			sb.append("<");
			for (Type signatureType : signatureTypes) {
				sb.append(signatureType.getDescriptor());
			}
			sb.append(">;");
			signature = sb.toString();
		}
		return signature;
	};

	default String signatureOf(Type type, Class<?>... signatureClasses) {
		String signature = null;
		if (signatureClasses != null && signatureClasses.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("L");
			sb.append(type.getInternalName());
			sb.append("<");
			for (Class<?> signatureClass : signatureClasses) {
				sb.append(Type.getDescriptor(signatureClass));
			}
			sb.append(">;");
			signature = sb.toString();
		}
		return signature;
	};

}
