package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;

public interface ClassType<C> extends InvokeMethod<C>, Types, Opcodes {
	C newInstace();
}
