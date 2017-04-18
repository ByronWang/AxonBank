package com.nebula.cqrs.axon;

import static org.junit.Assert.*;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.samples.bank.query.bankaccount.BankAccountRepository;
import org.junit.Before;
import org.junit.Test;

public class ControllerDomainListenerTest {
	
	CQRSWebDomainListener listener;
	
    @Before
    public void setUp() throws Exception {
    	listener = new CQRSWebDomainListener();
    }
	@Test
	public void testDefine() throws ClassNotFoundException {
		CQRSBuilder cqrsBuilder = new CQRSBuilder();
		cqrsBuilder.add(listener);
		cqrsBuilder.makeDomainCQRSHelper("org.axonframework.samples.bank.cqrs.MyBankAccount");
//		
//		CommandBus commandBus,
//        BankAccountRepository bankAccountRepository
        
        
		Class<?> clz = cqrsBuilder.loadClass("org.axonframework.samples.bank.cqrs.MyBankAccountController");
	
	}

}
