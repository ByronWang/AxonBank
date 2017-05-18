package com.nebula.tinyasm.api;

import java.util.function.Consumer;

public interface MethodUseCaller<M, C extends MethodCode<M, C>> extends InvokeMethod<M, C>, ToType {
	M add(int varIndex);

	default M add(String varName) {
		return add(code().varIndex(varName));
	}

	void returnMe();

	M with(Consumer<C> invocation);
}
