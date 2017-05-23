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

package com.nebula.tinyasm.ana.source;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class BankTransfer {

	@AggregateIdentifier
	private String bankTransferId;
	private String sourceAxonBankAccountId;
	private String destinationAxonBankAccountId;
	private long amount;
	private Status status;

	@SuppressWarnings("unused")
	protected BankTransfer() {
	}

	@CommandHandler
	public BankTransfer(BankTransferCreateCommand command) {
		apply(new BankTransferCreatedEvent(command.getBankTransferId(), command.getSourceBankAccountId(), command.getDestinationBankAccountId(),
		        command.getAmount()));
	}

	@EventHandler
	public void on(BankTransferCreatedEvent event) throws Exception {
		this.bankTransferId = event.getBankTransferId();
		this.sourceAxonBankAccountId = event.getSourceAxonBankAccountId();
		this.destinationAxonBankAccountId = event.getDestinationAxonBankAccountId();
		this.amount = event.getAmount();
		this.status = Status.STARTED;
	}

	@CommandHandler
	public void handle(BankTransferMarkCompletedCommand command) {
		apply(new BankTransferCompletedEvent(command.getBankTransferId()));
	}

	@EventHandler
	public void on(BankTransferCompletedEvent event) {
		this.status = Status.COMPLETED;
	}

	@CommandHandler
	public void handle(BankTransferMarkFailedCommand command) {
		apply(new BankTransferFailedEvent(command.getBankTransferId()));
	}

	@EventHandler
	public void on(BankTransferFailedEvent event) {
		this.status = Status.FAILED;
	}

	private enum Status {
		STARTED, FAILED, COMPLETED
	}
}