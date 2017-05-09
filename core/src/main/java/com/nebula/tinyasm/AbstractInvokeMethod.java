package com.nebula.tinyasm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.Instance;
import com.nebula.tinyasm.api.InvokeMethod;
import com.nebula.tinyasm.api.MethodCode;
import com.nebula.tinyasm.api.MethodUseCaller;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.Field;

abstract class AbstractInvokeMethod<M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>> implements InvokeMethod<M, C> {
	AbstractInvokeMethod(MethodVisitor mv) {
		this.mv = mv;
	}

	@Override
	public Instance<M, C> get(Field field) {
		AsmBuilder.visitGetField(mv, getType(), field.name, field.type);
		code().type(field.type);
		return topInstance();
	}

	MethodVisitor mv;

	@Override
	public C putTo(Field field) {
		AsmBuilder.visitPutField(mv, getType(), field.name, field.type);
		return code();
	}

	@Override
	public void invoke(Type objectType, int invoketype, Type returnType, String methodName, Type... params) {
		AsmBuilder.visitInvoke(invoketype, mv, getType(), returnType, methodName, params);
	}
}