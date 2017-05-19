package com.nebula.tinyasm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.Instance;
import com.nebula.tinyasm.api.InvokeMethod;
import com.nebula.tinyasm.api.MethodCode;
import com.nebula.tinyasm.util.AsmBuilder;

abstract class AbstractInvokeMethod<M, C extends MethodCode<M, C>> implements InvokeMethod<M, C> {
	AbstractInvokeMethod(MethodVisitor mv) {
		this.mv = mv;
	}

	@Override
	public Instance<M, C> get(String fieldName, Type fieldType) {
		AsmBuilder.visitGetField(mv, getType(), fieldName, fieldType);
		return code().type(fieldType);
	}

	@Override
	public Instance<M, C> getStatic(String fieldName, Type fieldType) {
		AsmBuilder.visitGetStaticField(mv, getType(), fieldName, fieldType);
		return code().type(fieldType);
	}

	@Override
	public C putStaticTo(String fieldName, Type fieldType) {
		AsmBuilder.visitPutStaticField(mv, getType(), fieldName, fieldType);
		return code();
	}

	@Override
	public C putTo(String fieldName, Type fieldType) {
		AsmBuilder.visitPutField(mv, getType(), fieldName, fieldType);
		return code();
	}

	MethodVisitor mv;

	@Override
	public void invoke(Type objectType, int invoketype, Type returnType, String methodName, Type... params) {
		AsmBuilder.visitInvoke(invoketype, mv, getType(), returnType, methodName, params);
	}
}