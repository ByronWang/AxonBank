package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClassMethodVisitor extends AbstractMethodVistor<ClassMethodHeader, ClassUseCaller, ClassMethodCode>
        implements ClassMethodHeader, ClassMethodCode, ClassThisInstance, Opcodes {
	class RealThisUseCaller extends RealUseCaller implements ClassUseCaller {

		public RealThisUseCaller(Type objectType) {
			super(objectType);
		}

		@Override
		ClassUseCaller caller() {
			return this;
		}

		@Override
		public ClassMethodCode code() {
			return ClassMethodVisitor.this;
		}

	}

	class ThisInstance extends MyInstance implements ClassThisInstance {
		@Override
		public Field fieldOf(String fieldName) {
			return cv.fieldOf(fieldName);
		}
	}

	SimpleClassVisitor cv;

	ThisInstance thisInstance = new ThisInstance();

	public ClassMethodVisitor(SimpleClassVisitor cv, Type thisType, int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		super(cv, thisType, access, returnType, methodName, exceptionClasses);
		this.cv = cv;
	}

	@Override
	public ClassMethodCode code() {
		return this;
	}

	@Override
	public ClassMethodHeader header() {
		return this;
	}

	@Override
	public Instance<ClassUseCaller, ClassMethodCode> get(Field field) {
		return loadThis().get(field);
	}

	@Override
	public Instance<ClassUseCaller, ClassMethodCode> get(String fieldName) {
		return loadThis().get(cv.fieldOf(fieldName));
	}

	@Override
	public Instance<ClassUseCaller, ClassMethodCode> getProperty(Field field) {
		return loadThis().getProperty(field);
	}

	@Override
	public Instance<ClassUseCaller, ClassMethodCode> getProperty(String fieldName) {
		return loadThis().getProperty(cv.fieldOf(fieldName));
	}

	@Override
	public ClassMethodCode initObject() {
		ASMBuilder.visitInitObject(mv, THIS);
		return code();
	}

	@Override
	public void invoke(int invoketype, String methodName, Type... params) {
		loadThis().invoke(invoketype, methodName, params);
	}

	@Override
	public Instance<ClassUseCaller, ClassMethodCode> invoke(int invoketype, Type returnType, String methodName, Type... params) {
		return loadThis().invoke(invoketype, returnType, methodName, params);
	}

	@Override
	public ClassThisInstance loadThis() {
		object(THIS);
		return thisInstance;
	}

	@Override
	public ClassMethodCode put(int dataIndex, Field field) {
		return loadThis().put(dataIndex, field);
	}

	@Override
	public ClassMethodCode put(int dataIndex, String fieldName) {
		return loadThis().put(dataIndex, cv.fieldOf(fieldName));
	}

	@Override
	public ClassMethodCode put(String varName, String fieldName) {
		return loadThis().put(varIndex(fieldName), cv.fieldOf(fieldName));
	}

	@Override
	public ClassMethodCode putTopTo(Field field) {
		return thisType().putTopTo(field);
	}

	@Override
	public ClassMethodCode putTopTo(String fieldName) {
		return thisType().putTopTo(cv.fieldOf(fieldName));
	}

	@Override
	public ClassType<ClassUseCaller, ClassMethodCode> thisType() {
		return type(thisObjectType);
	}

	@Override
	public ClassUseCaller useThis() {
		loadThis();
		return new RealThisUseCaller(thisObjectType);
	}

	@Override
	public Type getType() {
		return thisObjectType;
	}

	@Override
	public ClassUseCaller useTop(Type type) {
		return new RealThisUseCaller(type);
	}

	@Override
	public ClassUseCaller use() {
		return useThis();
	}

	@Override
	public Field fieldOf(String fieldName) {
		return cv.fieldOf(fieldName);
	}
}
