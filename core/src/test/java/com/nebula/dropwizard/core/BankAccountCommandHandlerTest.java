/*
 * Copyright (c) 2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nebula.dropwizard.core;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.axonframework.samples.bank.api.bankaccount.BankAccountCreatedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountCreateCommand;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyDepositCommand;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyDepositedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyWithdrawnEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountWithdrawMoneyCommand;
import org.axonframework.samples.bank.simple.instanceCommand.BankAccount;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BankAccountCommandHandlerTest {

	private Class<?> clzDomain;
	Class<?> clzHandle;

	private FixtureConfiguration<?> testFixture;

	@Before
	public void setUp() throws Exception {

		String domainClassName = "org.axonframework.samples.bank.simple.instanceCommand.BankAccount";

		CQRSBuilder.makeDomainCQRSHelper(domainClassName);
		clzDomain = CQRSBuilder.classLoader.loadClass(domainClassName);
		clzHandle = CQRSBuilder.classLoader.loadClass(domainClassName + "CommandHandler");

		testFixture = new AggregateTestFixture<>(clzDomain);

		Object commandHandler = createCommandHandler(clzHandle, testFixture.getRepository(), testFixture.getEventBus());

		testFixture.registerAnnotatedCommandHandler(commandHandler);
		testFixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
	}

	private static Object createCommandHandler(Class<?> clz, Repository<?> repository, EventBus eventBus) throws Exception {
		Constructor<?> c = clz.getConstructor(Repository.class, EventBus.class);
		return c.newInstance(repository, eventBus);
	}

	// @Test(expected = JSR303ViolationException.class)
	// public void testCreateBankAccount_RejectNegativeOverdraft() throws
	// Exception {
	// Class<?> clzCommand =
	// CQRSBuilder.classLoader.loadClass("org.axonframework.samples.bank.simple.instanceCommand.BankAccount");
	// testFixture.givenNoPriorActivity().when(createCommand(clzCommand,
	// UUID.randomUUID().toString(), "wangshilian", 20));
	// }

	@Test
	public void testCreateBankAccount() throws Exception {
		String id = "bankAccountId";

		Class<?> clzCreateCommand = CQRSBuilder.classLoader.loadClass("org.axonframework.samples.bank.simple.instanceCommand.BankAccount_CtorCommand");
		Object createCommand = clzCreateCommand.getConstructor(String.class, long.class).newInstance(id, 0);

		Class<?> clzEvent = CQRSBuilder.classLoader.loadClass("org.axonframework.samples.bank.simple.instanceCommand.BankAccount_CtorFinishedEvent");
		Object event = clzEvent.getConstructor(String.class, long.class).newInstance(id, 0);

		testFixture.givenNoPriorActivity().when(createCommand).expectEvents(event);
	}

	@Test
	public void testDepositMoney() throws Exception {
		String id = "bankAccountId";

		Class<?> clzCreateCommand = CQRSBuilder.classLoader.loadClass("org.axonframework.samples.bank.simple.instanceCommand.BankAccount_CtorCommand");
		Object createCommand = clzCreateCommand.getConstructor(String.class, long.class).newInstance(id, 0);

		Class<?> clzCommand = CQRSBuilder.classLoader.loadClass("org.axonframework.samples.bank.simple.instanceCommand.BankAccountWithdrawCommand");
		Object thisCommand = clzCommand.getConstructor(String.class, long.class).newInstance(id, 1000);

		Class<?> clzEvent = CQRSBuilder.classLoader.loadClass("org.axonframework.samples.bank.simple.instanceCommand.BankAccountDepositFinishedEvent");
		Object thisEvent = clzEvent.getConstructor(String.class, long.class).newInstance(id, 1000);

		testFixture.given(createCommand).when(thisCommand).expectEvents(thisEvent);
		System.out.println("XXXX " + testFixture.getRepository().load(id));
	}

	@Test
	public void testWithdrawMoney() throws Exception {
		String id = "bankAccountId";

		testFixture.given(new BankAccountCreatedEvent(id, 0), new BankAccountMoneyDepositedEvent(id, 50)).when(new BankAccountWithdrawMoneyCommand(id, 50))
				.expectEvents(new BankAccountMoneyWithdrawnEvent(id, 50));
	}

	@Test
	public void testWithdrawMoney_RejectWithdrawal() throws Exception {
		String id = "bankAccountId";

		testFixture.given(new BankAccountCreatedEvent(id, 0), new BankAccountMoneyDepositedEvent(id, 50)).when(new BankAccountWithdrawMoneyCommand(id, 51))
				.expectEvents();
	}
	

	private void print(Class<?> clz) {
		Method[] methods = clz.getMethods();
		StringBuilder sb = new StringBuilder();
		for (Method method : methods) {
			sb.append(method.getName());
			sb.append("[");
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < parameterTypes.length; i++) {
				if (i == 0) sb.append(",");
				sb.append(parameterTypes[i].getSimpleName());
			}
			sb.append("]\n");
		}
		System.out.println(sb.toString());
	}

}