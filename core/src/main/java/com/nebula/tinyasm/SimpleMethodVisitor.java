package com.nebula.tinyasm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.Instance;
import com.nebula.tinyasm.api.MethodCode;
import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.api.MethodUseCaller;
import com.nebula.tinyasm.api.Types;

interface SimpleMethodCode extends Types, MethodCode<SimpleUseCaller, SimpleMethodCode> {

}

interface SimpleUseCaller extends MethodUseCaller<SimpleUseCaller, SimpleMethodCode> {

}

public class SimpleMethodVisitor extends AbstractMethodVistor<MethodHeader<SimpleMethodCode>, SimpleUseCaller, SimpleMethodCode>
        implements SimpleMethodCode, MethodHeader<SimpleMethodCode>, Opcodes {

	class RealSimpleUseCaller extends RealUseCaller implements SimpleUseCaller {
		public RealSimpleUseCaller(MethodVisitor mv, Type objectType) {
			super(mv, objectType);
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
		return new RealSimpleUseCaller(mv,type);
	}

	@Override
	public Instance<SimpleUseCaller, SimpleMethodCode> type(Type objectType) {
		// TODO Auto-generated method stub
		return null;
	}
}