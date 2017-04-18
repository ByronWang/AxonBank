package com.nebula.cqrs.axon;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

public interface DomainListener {
	void define(CQRSContext ctx, Type typeDomain ,CQRSDomainBuilder cqrs,ClassReader domainClassReader);
}
