package com.nebula.tinyasm.api;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface ClassBody extends Types, Opcodes, DefineField<ClassBody>, DefineMethod {

	ClassBody annotation(Type annotationType, Object value);

	default ClassBody annotation(Class<?> annotationClass) {
		return annotation(typeOf(annotationClass));
	}

	default ClassBody annotation(Class<?> annotationClass, Object value) {
		return annotation(typeOf(annotationClass), value);
	}

	default ClassBody annotation(Type annotationType) {
		return annotation(annotationType, null);
	}

	Type referInnerClass(String innerClass);

	void end();

	ClassVisitor visitor();
	byte[] toByteArray();

}