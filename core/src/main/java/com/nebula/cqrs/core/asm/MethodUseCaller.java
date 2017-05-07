package com.nebula.cqrs.core.asm;

import java.util.function.Consumer;

public interface MethodUseCaller<M, C extends MethodCode<M,C>> extends InvokeMethod<C> {
	M add(int varIndex);

	default M add(String varName) {
		return add(code().var(varName));
	}

	M with(Consumer<C> invocation);

	C code();
}
