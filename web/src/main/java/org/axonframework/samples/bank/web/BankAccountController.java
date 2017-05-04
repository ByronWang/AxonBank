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

package org.axonframework.samples.bank.web;

import java.util.UUID;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.samples.bank.api.bankaccount.BankAccountCreateCommand;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyDepositCommand;
import org.axonframework.samples.bank.api.bankaccount.BankAccountWithdrawMoneyCommand;
import org.axonframework.samples.bank.query.bankaccount.BankAccountEntry;
import org.axonframework.samples.bank.query.bankaccount.BankAccountRepository;
import org.axonframework.samples.bank.web.dto.BankAccountDto;
import org.axonframework.samples.bank.web.dto.DepositDto;
import org.axonframework.samples.bank.web.dto.WithdrawalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
@MessageMapping("/bank-accounts")
public class BankAccountController {
	private final static Logger LOGGER = LoggerFactory.getLogger(BankAccountController.class);

	private CommandBus commandBus;
	private BankAccountRepository bankAccountRepository;

	@Autowired
	public BankAccountController(CommandBus commandBus, BankAccountRepository bankAccountRepository) {
		this.commandBus = commandBus;
		this.bankAccountRepository = bankAccountRepository;
	}

	@SubscribeMapping
	public Iterable<BankAccountEntry> all() {
		return bankAccountRepository.findAllByOrderByIdAsc();
	}

    @SubscribeMapping("/{id}")
    public BankAccountEntry get(@DestinationVariable String id) {
        return bankAccountRepository.findOne(id);
    }

	@MessageMapping("/create")
	public void create(BankAccountDto bankAccountDto) {
		LOGGER.debug("create with dto : {}", bankAccountDto);
		String id = UUID.randomUUID().toString();
		BankAccountCreateCommand command = new BankAccountCreateCommand(id, bankAccountDto.getOverdraftLimit());
		commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
	}

	@MessageMapping("/withdraw")
	public void withdraw(WithdrawalDto depositDto) {
		LOGGER.debug("deposit with dto : {}", depositDto);
		BankAccountWithdrawMoneyCommand command = new BankAccountWithdrawMoneyCommand(depositDto.getAxonBankAccountId(), depositDto.getAmount());
		commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
	}

	@MessageMapping("/deposit")
	public void deposit(DepositDto depositDto) {
		LOGGER.debug("withdraw with dto : {}", depositDto);
		BankAccountMoneyDepositCommand command = new BankAccountMoneyDepositCommand(depositDto.getAxonBankAccountId(), depositDto.getAmount());
		commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
	}
}
