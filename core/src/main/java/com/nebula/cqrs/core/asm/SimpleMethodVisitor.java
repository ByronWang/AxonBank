package com.nebula.cqrs.core.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class SimpleMethodVisitor extends AbstractMethodVistor<SimpleMethodHeader, SimpleUseCaller<SimpleMethodCode>, SimpleMethodCode>
        implements SimpleMethodCode, SimpleMethodHeader, Opcodes {

	class RealSimpleUseCaller extends RealUseCaller implements SimpleUseCaller<SimpleMethodCode> {

		public RealSimpleUseCaller(Type objectType) {
			super(objectType);
		}

		@Override
		SimpleUseCaller<SimpleMethodCode> caller() {
			return this;
		}

		@Override
		public SimpleMethodCode code() {
			return SimpleMethodVisitor.this;
		}
	}

	public SimpleMethodVisitor(ClassVisitor cv, Type thisType, int access, String methodName) {
		this(cv, thisType, access, Type.VOID_TYPE, methodName);
	}

	public SimpleMethodVisitor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName) {
		super(cv, thisType, access, returnType, methodName);
	}

	@Override
	public SimpleMethodCode code() {
		return this;
	}

	@Override
	public SimpleUseCaller<SimpleMethodCode> useTop(Type type) {
		return new RealSimpleUseCaller(type);
	}

	@Override
	public SimpleMethodHeader header() {
		return this;
	}

}