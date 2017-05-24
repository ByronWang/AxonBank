package com.nebula.cqrs.axon.builder;

import org.objectweb.asm.ClassVisitor;

public interface DomainListener {
	ClassVisitor listen(DomainContext context);
}