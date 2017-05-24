package com.nebula.cqrs.axon.builder;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.nebula.tinyasm.ClassBuilder;

public class CQRSDomainClassListener extends ClassBuilder implements DomainListener {

	public CQRSDomainClassListener() {
		super();
	}

	DomainContext context;

	@Override
	public ClassVisitor listen(DomainContext context) {
		this.context = context;
		return this;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return null;
	}
}
