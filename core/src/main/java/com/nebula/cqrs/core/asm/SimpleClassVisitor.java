package com.nebula.cqrs.core.asm;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.asm.ClassUtils;

public class SimpleClassVisitor extends ClassVisitor implements Opcodes, ClassMethodBody {
	final static int THIS = 0;
	final static String THIS_NAME = "this";
	private final Type thisType;

	private Map<String, Field> fields = new HashMap<>();

	public SimpleClassVisitor(ClassVisitor cv, Type thisType) {
		this(cv, thisType, Type.getType(Object.class));
	}

	public Field fieldOf(String fieldName) {
		return fields.get(fieldName);
	}

	public SimpleClassVisitor(ClassVisitor cv, Type thisType, Type superType) {
		super(Opcodes.ASM5);
		this.cv = cv;
		this.thisType = thisType;

		cv.visit(52, ACC_PUBLIC + ACC_SUPER, thisType.getInternalName(), null, superType.getInternalName(), null);
		String simpleName = ClassUtils.toSimpleName(this.thisType.getClassName());
		cv.visitSource(simpleName + ".java", null);
	}

	@Override
	public ClassMethodBody annotation(Type annotationType, Object value) {
		ASMBuilder.visitAnnotation(cv, annotationType, value);
		return this;
	}

	@Override
	public void end() {
		cv.visitEnd();
	}

	@Override
	public ClassMethodBody field(Field field) {
		fields.put(field.name, field);
		ASMBuilder.visitDefineField(cv, field.name, field.type);
		return this;
	}

	@Override
	public ClassMethodBody field(Field field, Type annotationType, Object value) {
		fields.put(field.name, field);
		ASMBuilder.visitDefineField(cv, field.name, field.type, annotationType, value);
		return this;
	}

	@Override
	public ClassMethodBody field(Field field, String signature, Type annotationType, Object value) {
		fields.put(field.name, field);
		ASMBuilder.visitDefineField(cv, field.name, field.type, signature, annotationType, value);
		return this;
	}

	@Override
	public ClassMethodHeader method(int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		return new ClassMethodVisitor(this, thisType, access, returnType, methodName, exceptionClasses);
	}
}
