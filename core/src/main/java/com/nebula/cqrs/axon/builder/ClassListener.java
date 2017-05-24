package com.nebula.cqrs.axon.builder;

import org.objectweb.asm.ClassVisitor;

public interface ClassListener {
	ClassVisitor listen(DomainContext context);
}