package com.nebula.cqrs.axon.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.core.CqrsEntity;

public class RemoveCqrsAnnotationClassVisitor extends ClassVisitor {

	public RemoveCqrsAnnotationClassVisitor(ClassVisitor cv) {
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
}
