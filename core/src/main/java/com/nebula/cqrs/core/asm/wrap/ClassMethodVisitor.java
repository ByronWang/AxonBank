package com.nebula.cqrs.core.asm.wrap;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.ASMBuilder;
import com.nebula.cqrs.core.asm.Field;

public class ClassMethodVisitor extends AbstractMethodVistor<MethodHeader<ClassMethodCode>, ClassUseCaller, ClassMethodCode>
        implements MethodHeader<ClassMethodCode>, ClassMethodCode, Opcodes {
	class RealThisUseCaller extends RealUseCaller implements ClassUseCaller {

		public RealThisUseCaller(MethodVisitor mv, Type objectType) {
			super(mv, objectType);
		}

		@Override
		ClassUseCaller caller() {
			return this;
		}
	}

	class ThisInstance extends MyInstance implements ClassThisInstance {
		ThisInstance(MethodVisitor mv) {
			super(mv);
		}

		@Override
		public Field fieldOf(String fieldName) {
			return cv.fieldOf(fieldName);
		}
	}

	@Override
	protected ClassMethodCode methodBegin() {
		thisInstance = new ThisInstance(mv);
		return super.methodBegin();
	}

	SimpleClassVisitor cv;

	ThisInstance thisInstance;

	public ClassMethodVisitor(SimpleClassVisitor cv, Type thisType, int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		super(cv, thisType, access, returnType, methodName, exceptionClasses);
		this.cv = cv;
	}

	@Override
	public ClassMethodCode code() {
		return this;
	}

	@Override
	public ClassMethodCode initObject() {
		ASMBuilder.visitInitObject(mv, THIS);
		return code();
	}

	@Override
	public ClassThisInstance This() {
		object(THIS);
		return thisInstance;
	}

	@Override
	public Instance<ClassUseCaller, ClassMethodCode> thisType() {
		return type(thisObjectType);
	}

	@Override
	public ClassUseCaller useThis() {
		This();
		return new RealThisUseCaller(mv,thisObjectType);
	}

	@Override
	public ClassUseCaller useTop(Type type) {
		return new RealThisUseCaller(mv,type);
	}

	@Override
	public Field fieldOf(String fieldName) {
		return cv.fieldOf(fieldName);
	}
}
