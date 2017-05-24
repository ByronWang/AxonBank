package com.nebula.cqrs.axon.builder;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.nebula.tinyasm.util.MethodInfo;

import static org.objectweb.asm.Opcodes.*;

public class SagaClassListener extends ClassVisitor implements DomainListener {

	DomainContext context;
	public SagaClassListener() {
		super(ASM5);
	}

	@Override
	public ClassVisitor listen(DomainContext context) {
		this.context = context;
		return this;
	}

	boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (is(access, ACC_PUBLIC) && is(access, ACC_STATIC)) {
			MethodInfo method = context.getDomainDefinition().methods.get(name);
			return new SagaMethodVisitor(context, mv, method, access, name, desc, signature);
		} else {

			return mv;
		}
	}
}
