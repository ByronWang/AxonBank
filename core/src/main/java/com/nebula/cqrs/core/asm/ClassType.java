package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Opcodes;

public interface ClassType<M,C> extends ToType, InvokeMethod<M,C>, Types, Opcodes {
}
