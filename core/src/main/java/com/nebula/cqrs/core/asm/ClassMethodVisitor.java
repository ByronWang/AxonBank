package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClassMethodVisitor extends AbstractMethodVistor<ClassMethodHeader, ClassMethodCode> implements ClassMethodHeader, ClassMethodCode, Opcodes {
	SimpleClassVisitor cv;

	public ClassMethodVisitor(SimpleClassVisitor cv, Type thisType, int access, String methodName) {
		this(cv, thisType, access, thisType, methodName);
	}

	public ClassMethodVisitor(SimpleClassVisitor cv, Type thisType, int access, Type returnType, String methodName) {
		super(cv, thisType, access, returnType, methodName);
		this.cv = cv;
	}

	@Override
	public ClassMethodCode me() {
		return this;
	}

	@Override
	public ClassMethodCode get(String fieldName) {
		return get(cv.fields.get(fieldName));
	}

	@Override
	public ClassMethodCode put(String fieldName) {
		return put(cv.fields.get(fieldName));
	}

	@Override
	public ClassMethodCode getProperty(String fieldName) {
		return getProperty(cv.fields.get(fieldName));
	}

	@Override
	public ClassMethodCode put(int dataIndex, String fieldName) {
		return put(dataIndex, cv.fields.get(fieldName));
	}

	@Override
	ClassMethodCode _code() {
		return this;
	}

	@Override
	ClassMethodHeader _header() {
		return this;
	}
}
