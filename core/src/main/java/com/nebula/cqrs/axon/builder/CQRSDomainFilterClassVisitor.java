package com.nebula.cqrs.axon.builder;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.springframework.asm.Type;

import com.nebula.cqrs.core.CqrsEntity;

public class CQRSDomainFilterClassVisitor extends ClassVisitor {

	public CQRSDomainFilterClassVisitor(ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		String cqrs = Type.getDescriptor(CqrsEntity.class);
		if (!cqrs.equals(desc)) {
			return super.visitAnnotation(desc, visible);
		} else {
			return null;
		}
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if ("<init>".equals(name) && Type.getArgumentTypes(desc).length == 0) {
			return cv.visitMethod(access, name, desc, signature, exceptions);
		} else {
			return null;
		}
	}

	@Override
	public void visitEnd() {
//		super.visitEnd();
	}

}
