package com.nebula.cqrs.axon;

import com.nebula.cqrs.axon.pojo.DomainDefinition;

public interface DomainListener {
	void define(CQRSContext ctx, DomainDefinition domainDefinition);
}
