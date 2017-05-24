package com.nebula.tinyasm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.ClassMethodCode;
import com.nebula.tinyasm.api.ClassThisInstance;
import com.nebula.tinyasm.api.ClassUseCaller;
import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.Field;

public class ClassMethodVisitor extends AbstractInstanceMethodVisitor<MethodHeader<ClassMethodCode>, ClassUseCaller, ClassMethodCode>
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

	ClassBuilder cv;

	ThisInstance thisInstance;

	public ClassMethodVisitor(ClassBuilder cv, Type thisType, int access, Type returnType, String methodName, String[] exceptionClasses) {
		super(cv, thisType, access, returnType, methodName, exceptionClasses);
		this.cv = cv;
	}

	@Override
	public ClassMethodCode code() {
		return this;
	}

	@Override
	protected ClassMethodCode doMethodBegin() {
		thisInstance = new ThisInstance(mv);
		return super.doMethodBegin();
	}

	@Override
	public Field fieldOf(String fieldName) {
		return cv.fieldOf(fieldName);
	}

	@Override
	public ClassMethodCode initObject() {
		AsmBuilder.visitInitObject(mv, THIS);
		return code();
	}

	@Override
	public ClassThisInstance loadThis() {
		object(THIS);
		return thisInstance;
	}

	@Override
	public ClassUseCaller useThis() {
		loadThis();
		return new RealThisUseCaller(mv, thisObjectType);
	}

	@Override
	public ClassUseCaller useTop(Type type) {
		return new RealThisUseCaller(mv, type);
	}

	@Override
	public ClassUseCaller useTopThis() {
		return new RealThisUseCaller(mv, thisObjectType);
	}
}
