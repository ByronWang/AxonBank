package com.nebula.cqrs.core.asm.wrap;

import org.objectweb.asm.Opcodes;

public interface ClassType<M extends MethodUseCaller<M, C>,C extends MethodCode<M, C>> extends ToType, InvokeMethod<M,C>, Types, Opcodes {
}
