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

package com.nebula.cqrs.axon;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

public class BankAccountCommandHandlerTest {

	private Class<?> clzDomain;
	Class<?> clzHandle;
	String packageName;
	CQRSBuilder cqrs;

	private FixtureConfiguration<?> testFixture;

	// static boolean classLoaded = false;

	@Before
	public void setUp() throws Exception {

		String domainClassName = "org.axonframework.samples.bank.cqrs.BankAccount";
		packageName = domainClassName.substring(0, domainClassName.lastIndexOf('.'));
		cqrs = new CQRSBuilder();
		cqrs.makeDomainCQRSHelper(domainClassName);

		clzDomain = cqrs.loadClass(domainClassName);
		clzHandle = cqrs.loadClass(domainClassName + "CommandHandler");

		testFixture = new AggregateTestFixture<>(clzDomain);

		Object commandHandler = createCommandHandler(clzHandle, testFixture.getRepository(), testFixture.getEventBus());

		testFixture.registerAnnotatedCommandHandler(commandHandler);
		testFixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
	}
	//
	// @Test(expected = JSR303ViolationException.class)
	// public void testCreateBankAccount_RejectNegativeOverdraft() throws
	// Exception {
	// testFixture.givenNoPriorActivity().when(make("BankAccount_CtorCommand",
	// UUID.randomUUID().toString(), -1000L));
	// }

	@Test
	public void testCreateBankAccount() throws Exception {
		String id = "bankAccountId";

		testFixture.givenNoPriorActivity().when(make("BankAccount_CtorCommand", id, 0L)).expectEvents(make("BankAccount_CtorFinishedEvent", id, 0L));
	}

	@Test
	public void testDepositMoney() throws Exception {
		String id = "bankAccountId";

		testFixture.given(make("BankAccount_CtorFinishedEvent", id, 0L)).when(make("BankAccountDepositCommand", id, 1000L))
				.expectEvents(make("BankAccountDepositFinishedEvent", id, 1000L));

		// testFixture.given(new BankAccountCreatedEvent(id, 0))
		// .when(new BankAccountMoneyDepositCommand(id, 1000))
		// .expectEvents(new BankAccountMoneyDepositedEvent(id, 1000));
	}

	@Test
	public void testWithdrawMoney() throws Exception {
		String id = "bankAccountId";

		testFixture.given(make("BankAccount_CtorFinishedEvent", id, 0L), make("BankAccountDepositFinishedEvent", id, 50L))
				.when(make("BankAccountWithdrawCommand", id, 50L)).expectEvents(make("BankAccountWithdrawFinishedEvent", id, 50L));

		//
		// testFixture.given(new BankAccountCreatedEvent(id, 0), new
		// BankAccountMoneyDepositedEvent(id, 50)).when(new
		// BankAccountWithdrawMoneyCommand(id, 50))
		// .expectEvents(new BankAccountMoneyWithdrawnEvent(id, 50));
	}

	@Test
	public void testWithdrawMoney_RejectWithdrawal() throws Exception {
		String id = "bankAccountId";

		testFixture.given(make("BankAccount_CtorFinishedEvent", id, 0L), make("BankAccountDepositFinishedEvent", id, 50L))
				.when(make("BankAccountWithdrawCommand", id, 51L)).expectEvents();
	}

	private static Object createCommandHandler(Class<?> clz, Repository<?> repository, EventBus eventBus) throws Exception {
		Constructor<?> c = clz.getConstructor(Repository.class, EventBus.class);
		return c.newInstance(repository, eventBus);
	}

	private Object make(String name, Object... parameters) throws Exception {
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Class<?> clz = parameters[i].getClass();
			Field[] fields = clz.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				Field field = fields[j];
				if ("TYPE".equals(field.getName())) {
					clz = (Class<?>) field.get(clz);
					break;
				}
			}
			parameterTypes[i] = clz;
		}

		Class<?> clzCommand = cqrs.loadClass(packageName + "." + name);
		return clzCommand.getConstructor(parameterTypes).newInstance(parameters);
	}
}