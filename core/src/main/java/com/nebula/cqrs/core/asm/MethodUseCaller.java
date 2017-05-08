package com.nebula.cqrs.core.asm;

import java.util.function.Consumer;

public interface MethodUseCaller<M, C extends MethodCode<M, C>> extends InvokeMethod<M,C>, ToType {
	M add(int varIndex);

	default M add(String varName) {
		return add(code().varIndex(varName));
	}

	M with(Consumer<C> invocation);

	C code();

	default C store(int index) {
		return code().store(index);
	}

	default C store(String varName) {
		return code().store(varName);
	}

	void returnMe();
}
