package com.nebula.cqrs.core.asm.wrap;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

interface SimpleMethodCode extends Types, MethodCode<SimpleUseCaller, SimpleMethodCode> {

}

interface SimpleUseCaller extends MethodUseCaller<SimpleUseCaller, SimpleMethodCode> {

}

public class SimpleMethodVisitor extends AbstractMethodVistor<MethodHeader<SimpleMethodCode>, SimpleUseCaller, SimpleMethodCode>
        implements SimpleMethodCode, MethodHeader<SimpleMethodCode>, Opcodes {

	class RealSimpleUseCaller extends RealUseCaller implements SimpleUseCaller {

		public RealSimpleUseCaller(Type objectType) {
			super(objectType);
		}

		@Override
		SimpleUseCaller caller() {
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
	public SimpleUseCaller useTop(Type type) {
		return new RealSimpleUseCaller(type);
	}
}