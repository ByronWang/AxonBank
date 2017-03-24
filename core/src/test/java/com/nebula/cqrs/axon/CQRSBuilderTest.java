package com.nebula.cqrs.axon;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nebula.cqrs.axon.CQRSBuilder;

public class CQRSBuilderTest {

	@Test
	public void testMakeDomainCQRSHelper() throws Exception {
		CQRSBuilder.makeDomainCQRSHelper("org.axonframework.samples.bank.cqrs.BankAccount");
	}

}
