package com.nebula.cqrs.axon;

import static org.junit.Assert.*;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.samples.bank.query.bankaccount.BankAccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Type;

import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ControllerDomainListenerTest {
	
	CQRSWebSpringApplicationListener listener;
	CQRSBuilder cqrsBuilder = new CQRSBuilder();
	
    @Before
    public void setUp() throws Exception {
    	listener = new CQRSWebSpringApplicationListener(cqrsBuilder);
    }
	@Test
	public void testDefine() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String domainName = "org.axonframework.samples.bank.cqrs.MyBankAccount";

		String controllerName = domainName + "Controller";
		String repositoryName = domainName + "Repository";
		String entryName = domainName + "Entry";
		
		cqrsBuilder.add(listener);
		cqrsBuilder.makeDomainCQRSHelper(domainName);

		Class<?> clzRepository = cqrsBuilder.loadClass(repositoryName);
		Object repository = mock(clzRepository);
		CommandBus commandBus = mock(CommandBus.class);
        
		Class<?> clz = cqrsBuilder.loadClass(controllerName);
		Constructor<?> con =  clz.getConstructor(CommandBus.class);
		Object controller =  con.newInstance(commandBus);
	}
}
