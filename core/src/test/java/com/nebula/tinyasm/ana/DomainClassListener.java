package com.nebula.tinyasm.ana;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.builder.ClassListener;
import com.nebula.tinyasm.builder.Context;

public class DomainClassListener extends ClassBuilder implements ClassListener {

	public DomainClassListener() {
		super();
	}

	Context context;

	@Override
	public ClassVisitor listen(Context context) {
		this.context = context;
		return this;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return null;
	}
}
