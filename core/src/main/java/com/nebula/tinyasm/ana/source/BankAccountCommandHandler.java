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

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.AggregateNotFoundException;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankAccountCommandHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(BankAccountCommandHandler.class);
	private Repository<BankAccount> repository;
	private EventBus eventBus;

	public BankAccountCommandHandler(Repository<BankAccount> repository, EventBus eventBus) {
		this.repository = repository;
		this.eventBus = eventBus;
	}

	@CommandHandler
	public void handle(BankAccountCreateCommand command) throws Exception {
		LOGGER.debug("Create with command : {}", command);
		repository.newInstance(() -> new BankAccount(command.getBankAccountId(), command.getOverdraftLimit()));
	}

	@CommandHandler
	public void handle(BankAccountMoneyDepositCommand command) {
		LOGGER.debug("Deposit with command : {}", command);
		Aggregate<BankAccount> bankAccountAggregate = repository.load(command.getBankAccountId());
		bankAccountAggregate.execute(bankAccount -> bankAccount.deposit(command.getAmountOfMoney()));
	}

	@CommandHandler
	public void handle(BankAccountWithdrawMoneyCommand command) {

		LOGGER.debug("Withdraw with command : {}", command);
		LOGGER.debug("this.repository.load with key {}", command.getBankAccountId());
		Aggregate<BankAccount> bankAccountAggregate = repository.load(command.getBankAccountId());
		LOGGER.debug("after this.repository.load with key {}", command.getBankAccountId());
		LOGGER.debug("aggregate : {}", bankAccountAggregate);
		LOGGER.debug("start aggregate.execute(new InnerWithdraw(command)) {}", command);
		bankAccountAggregate.execute(bankAccount -> bankAccount.withdraw(command.getAmountOfMoney()));
		LOGGER.debug("after aggregate.execute(new InnerWithdraw(command)) {}", command);
		LOGGER.debug("aggregate : {}", bankAccountAggregate);

	}

	@CommandHandler
	public void handle(BankTransferSourceDebitCommand command) {
		try {
			Aggregate<BankAccount> bankAccountAggregate = repository.load(command.getBankAccountId());
			bankAccountAggregate.execute(bankAccount -> bankAccount.debit(command.getAmount(), command.getBankTransferId()));
		} catch (AggregateNotFoundException exception) {
			eventBus.publish(asEventMessage(new BankTransferSourceNotFoundEvent(command.getBankTransferId())));
		}
	}

	@CommandHandler
	public void handle(BankTransferDestinationCreditCommand command) {
		try {
			Aggregate<BankAccount> bankAccountAggregate = repository.load(command.getBankAccountId());
			bankAccountAggregate.execute(bankAccount -> bankAccount.credit(command.getAmount(), command.getBankTransferId()));

		} catch (AggregateNotFoundException exception) {
			eventBus.publish(asEventMessage(new BankTransferDestinationCreditFailedEvent(command.getBankTransferId())));
		}
	}

	@CommandHandler
	public void handle(BankTransferSourceReturnMoneyCommand command) {
		Aggregate<BankAccount> bankAccountAggregate = repository.load(command.getBankAccountId());
		bankAccountAggregate.execute(bankAccount -> bankAccount.returnMoney(command.getAmount()));
	}
}
