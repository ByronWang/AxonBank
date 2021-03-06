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

package org.axonframework.samples.bank.command;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.samples.bank.api.bankaccount.BankAccountCreatedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankTransferDestinationCreditedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyAddedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyDepositedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankTransferSourceReturnedMoneyOfFailedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneySubtractedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyWithdrawnEvent;
import org.axonframework.samples.bank.api.bankaccount.BankTransferSourceDebitRejectedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankTransferSourceDebitedEvent;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class BankAccount {

    @AggregateIdentifier
    private String id;
    private long overdraftLimit;
    private long balanceInCents;

    @SuppressWarnings("unused")
    private BankAccount() {
    }

    public BankAccount(String bankAccountId, long overdraftLimit) {
        apply(new BankAccountCreatedEvent(bankAccountId, overdraftLimit));
    }

    public void deposit(long amount) {
        apply(new BankAccountMoneyDepositedEvent(id, amount));
    }

    public void withdraw(long amount) {
        if (amount <= balanceInCents + overdraftLimit) {
            apply(new BankAccountMoneyWithdrawnEvent(id, amount));
        }
    }

    public void debit(long amount, String bankTransferId) {
        if (amount <= balanceInCents + overdraftLimit) {
            apply(new BankTransferSourceDebitedEvent(id, amount, bankTransferId));
        }
        else {
            apply(new BankTransferSourceDebitRejectedEvent(bankTransferId));
        }
    }

    public void credit(long amount, String bankTransferId) {
        apply(new BankTransferDestinationCreditedEvent(id, amount, bankTransferId));
    }

    public void returnMoney(long amount) {
        apply(new BankTransferSourceReturnedMoneyOfFailedEvent(id, amount));
    }

    @EventHandler
    public void on(BankAccountCreatedEvent event) {
        this.id = event.getId();
        this.overdraftLimit = event.getOverdraftLimit();
        this.balanceInCents = 0;
    }

    @EventHandler
    public void on(BankAccountMoneyAddedEvent event) {
        balanceInCents += event.getAmount();
    }

    @EventHandler
    public void on(BankAccountMoneySubtractedEvent event) {
        balanceInCents -= event.getAmount();
    }
}