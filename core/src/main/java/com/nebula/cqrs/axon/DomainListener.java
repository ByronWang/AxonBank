package com.nebula.cqrs.axon;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.asm.CQRSDomainBuilder;

public interface DomainListener {
	void define(CQRSContext ctx, Type typeDomain ,CQRSDomainBuilder cqrs,ClassReader domainClassReader);
}
