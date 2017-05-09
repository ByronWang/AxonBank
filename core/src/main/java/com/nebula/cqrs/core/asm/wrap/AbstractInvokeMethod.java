package com.nebula.cqrs.core.asm.wrap;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.ASMBuilder;
import com.nebula.cqrs.core.asm.Field;

abstract class AbstractInvokeMethod<M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>> implements InvokeMethod<M, C> {
	AbstractInvokeMethod(MethodVisitor mv) {
		this.mv = mv;
	}

	@Override
	public Instance<M, C> get(Field field) {
		ASMBuilder.visitGetField(mv, getType(), field.name, field.type);
		code().type(field.type);
		return topInstance();
	}

	MethodVisitor mv;

	@Override
	public C putTo(Field field) {
		ASMBuilder.visitPutField(mv, getType(), field.name, field.type);
		return code();
	}

	@Override
	public void invoke(Type objectType, int invoketype, Type returnType, String methodName, Type... params) {
		ASMBuilder.visitInvoke(invoketype, mv, getType(), returnType, methodName, params);
	}
}