package com.nebula.cqrs.core.asm;

import java.util.function.Consumer;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

interface MethodCode<C, I> extends Types {

	C accessLabel(Label label);

	C accessLabel(Label label, int line);

	C block(Consumer<C> invocation);

	Label defineLabel();

	C insn(int d);
	
	I object(Type objectType);

	default I object(Class<?> returnClass) {
		return object(typeOf(returnClass));
	}

	C jumpInsn(int ifgt, Label label);

	C line(int line);

	C load(int... index);

	default C localVariable(String name, Class<?> clz) {
		return localVariable(name, typeOf(clz));
	}
	

	C localVariable(String name, Type type);

	I me();

	C returnObject();

	default C returnType(Class<?> returnClass) {
		return returnType(typeOf(returnClass));
	}

	C returnType(Type type);

	C returnVoid();

}