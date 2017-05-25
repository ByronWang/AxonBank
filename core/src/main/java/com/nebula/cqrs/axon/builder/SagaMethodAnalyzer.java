package com.nebula.cqrs.axon.builder;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SagaMethodAnalyzer extends MethodVisitor {

	public SagaMethodAnalyzer(MethodVisitor mv) {
		super(Opcodes.ASM5, mv);
	}

}
