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

package org.axonframework.samples.bank.simple.instanceCommand;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class Person {

	@AggregateIdentifier
	private String id;
	private String name;
	private long age;

	@SuppressWarnings("unused")
	private Person() {
	}

	public Person(String id, String name,long age) {
		onCreated(id, name, age);
	}

	void onCreated(String id, String name,long age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}
}