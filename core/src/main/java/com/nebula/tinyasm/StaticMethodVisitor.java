package com.nebula.tinyasm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.MethodCode;
import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.api.MethodUseCaller;
import com.nebula.tinyasm.api.Types;

interface StaticMethodCode extends Types, MethodCode<StaticUseCaller, StaticMethodCode> {

}

interface StaticUseCaller extends MethodUseCaller<StaticUseCaller, StaticMethodCode> {

}

public class StaticMethodVisitor extends AbstractMethodVistor<MethodHeader<StaticMethodCode>, StaticUseCaller, StaticMethodCode>
        implements StaticMethodCode, MethodHeader<StaticMethodCode>, Opcodes {

	class RealSimpleUseCaller extends RealUseCaller implements StaticUseCaller {
		public RealSimpleUseCaller(MethodVisitor mv, Type objectType) {
			super(mv, objectType);
		}

		@Override
		StaticUseCaller caller() {
			return this;
		}

		@Override
		public StaticMethodCode code() {
			return StaticMethodVisitor.this;
		}
	}

	public StaticMethodVisitor(ClassVisitor cv, Type thisType, int access, String methodName) {
		this(cv, thisType, access, Type.VOID_TYPE, methodName);
	}

	public StaticMethodVisitor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName) {
		super(cv, thisType, access, returnType, methodName);
	}

	@Override
	public StaticMethodCode code() {
		return this;
	}

	@Override
	public StaticUseCaller useTop(Type type) {
		return new RealSimpleUseCaller(mv, type);
	}
}