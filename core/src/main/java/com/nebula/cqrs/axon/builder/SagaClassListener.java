package com.nebula.cqrs.axon.builder;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nebula.tinyasm.Variable;
import com.nebula.tinyasm.util.MethodInfo;

public class SagaClassListener extends ClassVisitor implements DomainListener {
	enum Status {
		Completed, Failed, Started
	}

	final static Logger LOGGER = LoggerFactory.getLogger(SagaClassListener.class);

	static Variable NA = new Variable("NA", Type.VOID_TYPE);

	static String repeat(String str, int times) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < times; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	DomainContext context;

	public SagaClassListener() {
		super(ASM5);
	}

	boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	@Override
	public ClassVisitor listen(DomainContext context) {
		this.context = context;
		return this;
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
