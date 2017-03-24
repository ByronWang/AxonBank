package com.nebula.cqrs.axon;

import org.junit.Before;
import org.junit.Test;

public class CQRSBuilderTest {

	CQRSBuilder cqrs;
	@Before
	public void setUp() throws Exception {
		cqrs = new CQRSBuilder();
	}
	@Test
	public void testMakeDomainCQRSHelper() throws Exception {
		cqrs.makeDomainCQRSHelper("org.axonframework.samples.bank.cqrs.MyBankAccount");
	}

}
