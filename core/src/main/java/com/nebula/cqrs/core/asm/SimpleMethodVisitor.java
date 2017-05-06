package com.nebula.cqrs.core.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class SimpleMethodVisitor extends AbstractMethodVistor<SimpleMethodHeader, SimpleMethodCode>
        implements SimpleMethodCode, Instance<SimpleMethodCode>, SimpleMethodHeader, Opcodes {

	public SimpleMethodVisitor(ClassVisitor cv, Type thisType, int access, String methodName) {
		this(cv, thisType, access, Type.VOID_TYPE, methodName);
	}

	public SimpleMethodVisitor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName) {
		super(cv, thisType, access, returnType, methodName);
	}

	@Override
	SimpleMethodCode _code() {
		return this;
	}

	@Override
	SimpleMethodHeader _header() {
		return this;
	}

}