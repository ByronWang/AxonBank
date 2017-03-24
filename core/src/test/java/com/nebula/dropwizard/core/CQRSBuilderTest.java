package com.nebula.dropwizard.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class CQRSBuilderTest {

	@Test
	public void testMakeDomainCQRSHelper() throws Exception {
		CQRSBuilder.makeDomainCQRSHelper("org.axonframework.samples.bank.cqrs.BankAccount");
	}

}
