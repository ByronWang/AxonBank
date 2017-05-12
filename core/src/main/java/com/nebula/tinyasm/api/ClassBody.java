package com.nebula.tinyasm.api;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface ClassBody extends Types, ToType, Opcodes, DefineField<ClassBody>, DefineMethod {

	default ClassBody annotation(Class<?> annotationClass) {
		return annotation(typeOf(annotationClass));
	}

	default ClassBody annotation(Class<?> annotationClass, Object value) {
		return annotation(typeOf(annotationClass), value);
	}

	default ClassBody annotation(Type annotationType) {
		return annotation(annotationType, null);
	}

	ClassBody annotation(Type annotationType, Object value);

	default ClassBody defineAllPropetyGet() {
		for (Field param : getFields()) {
			definePropertyGet(param);
		}
		return this;
	}

	default ClassBody defineAllPropetySet() {
		for (Field param : getFields()) {
			definePropertySet(param);
		}
		return this;
	}

	default ClassBody definePropertyGet(final Field field) {
		return definePropertyGet(field.name, field.type);
	}

	default ClassBody definePropertyGet(final String fieldName, final Type fieldType) {
		publicMethod(fieldType, toPropertyGetName(fieldName, fieldType)).code(mc -> {
			mc.loadThis().get(fieldName, fieldType);
			mc.returnTop(fieldType);
		});
		return this;
	}

	default ClassBody definePropertySet(final Field field) {
		return definePropertySet(field.name, field.type);
	}

	default ClassBody definePropertySet(final String fieldName, final Class<?> fieldClass) {
		return definePropertySet(fieldName, typeOf(fieldClass));
	}

	default ClassBody definePropertySet(final String fieldName, final Type fieldType) {
		publicMethod(toPropertySetName(fieldName, fieldType)).parameter(fieldName, fieldType).code(mc -> {
			mc.loadThis().put(fieldName, fieldName);
			mc.returnVoid();
		});
		return this;
	}

	default ClassBody definePropertySet(final Type annotationType, final String fieldName, final Type fieldType) {
		publicMethod(toPropertySetName(fieldName, fieldType)).annotation(annotationType).parameter(fieldName, fieldType).code(mc -> {
			mc.loadThis().put(fieldName, fieldName);
			mc.returnVoid();
		});
		return this;
	}

	default ClassBody definePropertySet(final Type annotationType, String name, Object value, final String fieldName, final Type fieldType) {
		publicMethod(toPropertySetName(fieldName, fieldType)).annotation(annotationType, name, value).parameter(fieldName, fieldType).code(mc -> {
			mc.loadThis().put(fieldName, fieldName);
			mc.returnVoid();
		});
		return this;
	}

	default ClassBody definePropertySet(final Class<?> annotationClass, final String fieldName, final Type fieldType) {
		return definePropertySet(typeOf(annotationClass), fieldName, fieldType);
	}

	default ClassBody definePropertySet(final Class<?> annotationClass, final String fieldName, final Class<?> fieldClass) {
		return definePropertySet(typeOf(annotationClass), fieldName, typeOf(fieldClass));
	}
	
	ClassBody end();

	default ClassBody fields(List<Field> fields) {
		for (Field field : fields) {
			field(field);
		}
		return this;
	}

	default ClassBody fields(Field... fields) {
		for (Field field : fields) {
			field(field);
		}
		return this;
	}

	List<Field> getFields();

	Type getSuperType();

	default ClassBody readonlyPojo() {
		return publicInitAllFields().defineAllPropetyGet().publicToStringWithAllFields();
	}

	default ClassBody pojo() {
		return publicInitAllFields().defineAllPropetyGet().defineAllPropetySet().publicToStringWithAllFields();
	}

	default ClassBody publicInitAllFields() {
		final List<Field> fields = getFields();
		publicMethod("<init>").parameter(fields).code(mc -> {
			mc.initObject();
			for (Field param : fields) {
				mc.loadThis().put(param.name, param.name);
			}
			mc.returnVoid();
		});
		return this;
	}

	default ClassBody publicMethodInitWithAllFieldsToSuper(Field... fields) {
		publicMethod("<init>").parameter(fields).code(mc -> {
			mc.loadThis();
			for (Field param : fields) {
				mc.load(param.name);
			}
			mc.type(getSuperType()).invokeSpecial("<init>", typesOf(fields));
			mc.returnVoid();
		});
		return this;
	}

	default ClassBody publicMethodInitWithAllFieldsToSuper(List<Field> fields) {
		publicMethod("<init>").parameter(fields).code(mc -> {
			mc.loadThis();
			for (Field param : fields) {
				mc.load(param.name);
			}
			mc.type(getSuperType()).invokeSpecial("<init>", typesOf(fields));
			mc.returnVoid();
		});
		return this;
	}

	default ClassBody publicToStringWithAllFields() {
		final List<Field> fields = getFields();
		publicMethod(String.class, "toString").parameter(fields).code(mc -> {
			mc.newInstace(StringBuilder.class);

			mc.dup();
			mc.ldcInsn(toSimpleName(getType().getClassName()) + "(");
			mc.type(StringBuilder.class).invokeSpecial("<init>", String.class);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				if (i != 0) {
					mc.ldcInsn(",");
					mc.type(StringBuilder.class).invokeVirtual(StringBuilder.class, "append", String.class);
				}

				mc.ldcInsn(field.name + "=");
				mc.type(StringBuilder.class).invokeVirtual(StringBuilder.class, "append", String.class);

				mc.loadThis().get(field.name, field.type);
				mc.type(StringBuilder.class).invokeVirtual(StringBuilder.class, "append", field.type);
			}

			mc.ldcInsn(")");
			mc.type(StringBuilder.class).invokeVirtual(StringBuilder.class, "append", String.class);
			mc.type(StringBuilder.class).invokeVirtual(String.class, "toString");
			mc.returnObject();
		});
		return this;
	}

	Type referInnerClass(String innerClass);

	byte[] toByteArray();

	default ClassBody visitDefine_toString_withAllProperties() {
		final List<Field> fields = getFields();
		publicMethod(String.class, "toString").parameter(fields).code(mc -> {
			mc.newInstace(StringBuilder.class);

			mc.dup();
			mc.ldcInsn(toSimpleName(getType().getClassName()) + "(");
			mc.type(StringBuilder.class).invokeSpecial("<init>", String.class);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				if (i != 0) {
					mc.ldcInsn(",");
					mc.type(StringBuilder.class).invokeVirtual(StringBuilder.class, "append", String.class);
				}

				mc.ldcInsn(field.name + "=");
				mc.type(StringBuilder.class).invokeVirtual(StringBuilder.class, "append", String.class);

				mc.loadThis().getProperty(field.name, field.type);
				mc.type(StringBuilder.class).invokeVirtual(StringBuilder.class, "append", field.type);
			}

			mc.ldcInsn(")");
			mc.type(StringBuilder.class).invokeVirtual(StringBuilder.class, "append", String.class);
			mc.type(StringBuilder.class).invokeVirtual(String.class, "toString");
			mc.returnObject();
		});
		return this;
	}

	ClassVisitor visitor();

}