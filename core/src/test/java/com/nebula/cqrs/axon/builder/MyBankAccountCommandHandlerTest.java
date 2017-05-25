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

package com.nebula.cqrs.axon.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.util.AsmBuilder;

public class MyBankAccountCommandHandlerTest {

	private Class<?> clzDomain;
	Class<?> clzHandle;
	String packageName;
	DomainBuilder domainBuilder;
	DomainDefinition domainDefinition;
	private FixtureConfiguration<?> testFixture;

	// static boolean classLoaded = false;

	@Before
	public void setUp() throws Exception {

		Type domainType = Type.getType(BankAccount.class);

		ClassReader cr = new ClassReader(BankAccount.class.getName());

		String srcDomainName = AsmBuilder.toSimpleName(domainType.getClassName());

		domainBuilder = new DomainBuilder(srcDomainName, domainType, cr);

		domainDefinition = domainBuilder.getDomainDefinition();
		
		domainBuilder.visit(new CQRSEventClassListener());
		domainBuilder.visit(new CQRSCommandClassListener());

		// SagaClassListener saga = new SagaClassListener();
		// domainBuilder.visit(saga);

		domainBuilder.finished();
		
		

		clzDomain = domainBuilder.classLoader.loadClass(domainDefinition.topLeveltypeOf("impl").getClassName());
		clzHandle = domainBuilder.classLoader.loadClass(domainDefinition.topLeveltypeOf("CommandHandler").getClassName());

		testFixture = new AggregateTestFixture<>(clzDomain);

		Object commandHandler = createCommandHandler(clzHandle, testFixture.getRepository(), testFixture.getEventBus());

		testFixture.registerAnnotatedCommandHandler(commandHandler);
		testFixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
	}
	//
	// @Test(expected = JSR303ViolationException.class)
	// public void testCreateMyBankAccount_RejectNegativeOverdraft() throws
	// Exception {
	// testFixture.givenNoPriorActivity().when(make("MyBankAccountCreateCommand",
	// UUID.randomUUID().toString(), -1000L));
	// }
	@Test
	public void testNothing() throws Exception {
}
	
	@Test
	public void testCreateMyBankAccount() throws Exception {
		String id = "MyBankAccountId";

		testFixture.givenNoPriorActivity().when(make("CreateCommand", id, 0L)).expectEvents(make("CreateFinishedEvent", id, 0L));
	}

	@Test
	public void testDepositMoney() throws Exception {
		String id = "MyBankAccountId";

		testFixture.given(make("CreateFinishedEvent", id, 0L)).when(make("DepositCommand", id, 1000L)).expectEvents(make("DepositFinishedEvent", id, 1000L));

		// testFixture.given(new MyBankAccountCreatedEvent(id, 0))
		// .when(new MyBankAccountMoneyDepositCommand(id, 1000))
		// .expectEvents(new MyBankAccountMoneyDepositedEvent(id, 1000));
	}

	@Test
	public void testWithdrawMoney() throws Exception {
		String id = "MyBankAccountId";

		testFixture.given(make("CreateFinishedEvent", id, 0L), make("DepositFinishedEvent", id, 50L)).when(make("WithdrawCommand", id, 50L))
		        .expectEvents(make("WithdrawFinishedEvent", id, 50L));

		//
		// testFixture.given(new MyBankAccountCreatedEvent(id, 0), new
		// MyBankAccountMoneyDepositedEvent(id, 50)).when(new
		// MyBankAccountWithdrawMoneyCommand(id, 50))
		// .expectEvents(new MyBankAccountMoneyWithdrawnEvent(id, 50));
	}

	@Test
	public void testWithdrawMoney_RejectWithdrawal() throws Exception {
		String id = "MyBankAccountId";

		testFixture.given(make("CreateFinishedEvent", id, 0L), make("DepositFinishedEvent", id, 50L)).when(make("WithdrawCommand", id, 51L)).expectEvents();
	}

	private Object createCommandHandler(Class<?> clz, Repository<?> repository, EventBus eventBus) throws Exception {
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

		Class<?> clzCommand = domainBuilder.classLoader.loadClass(domainDefinition.typeOf(name).getClassName());
		return clzCommand.getConstructor(parameterTypes).newInstance(parameters);
	}
}