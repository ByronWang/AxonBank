package com.nebula.tinyasm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.api.StaticMethodCode;
import com.nebula.tinyasm.api.StaticUseCaller;

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

	public StaticMethodVisitor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName, String[] exceptionClasses) {
		super(cv, thisType, access, returnType, methodName, exceptionClasses);
	}

	@Override
	public Type thisType() {
		return thisObjectType;
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