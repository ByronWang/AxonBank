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

package org.axonframework.samples.bank.simple.staticCommand;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class BankAccount {

	@AggregateIdentifier
	private String id;
	private long overdraftLimit;
	private long balanceInCents;

	public String getId() {
		return id;
	}

	public long getOverdraftLimit() {
		return overdraftLimit;
	}

	public long getBalanceInCents() {
		return balanceInCents;
	}

	@SuppressWarnings("unused")
	private BankAccount() {
	}

	private BankAccount(String bankAccountId, long overdraftLimit) {
		onCreated(bankAccountId, overdraftLimit);// BankAccountCreatedEvent
	}

	// BankAccountCreateCommand
	public static BankAccount create(String bankAccountId, long overdraftLimit) {
		return new BankAccount(bankAccountId, overdraftLimit);
	}

	// BankAccountMoneyDepositCommand
	public static boolean deposit(BankAccount bankAccount, long amount) {
		bankAccount.onMoneyAdded(amount);// BankAccountMoneyDepositedEvent
		return true;
	}

	// BankAccountWithdrawMoneyCommand
	public static boolean withdraw(BankAccount bankAccount, long amount) {
		if (amount <= bankAccount.getBalanceInCents() + bankAccount.getOverdraftLimit()) {
			bankAccount.onMoneySubtracted(amount);// BankAccountMoneyWithdrawnEvent
			return true;
		} else {
			return false;
		}
	}

	public static boolean bankTransfer(BankAccount source, BankAccount destination, long amount) {// BankTransferCreatedEvent
		boolean sourceDebitSucceed = debit(source, amount);// BankTransferSourceDebitCommand
		if (sourceDebitSucceed) {// BankTransferSourceDebitedEvent
			boolean destinationCreditSucceed = credit(destination, amount);// BankTransferDestinationCreditCommand
			if (destinationCreditSucceed) {// BankTransferDestinationCreditedEvent
				return true;// BankTransferMarkCompletedCommand
			} else {// BankTransferDestinationCreditedRejectedEvent
				returnMoney(source, amount);// BankTransferSourceReturnMoneyCommand
				return false;// BankTransferMarkFailedCommand
			}
		} else {// BankTransferSourceDebitRejectedEvent
			return false;
		}
	}

	//BankTransferSourceDebitCommand
	public static boolean debit(BankAccount bankAccount, long amount) {
		if (amount <= bankAccount.getBalanceInCents() + bankAccount.overdraftLimit) {
			bankAccount.onMoneySubtracted(amount);// BankTransferSourceDebitedEvent
			return true;
		} else {
			return false;// BankTransferSourceDebitRejectedEvent
		}
	}
	//BankTransferDestinationCreditCommand
	public static boolean credit(BankAccount bankAccount, long amount) {
		bankAccount.onMoneyAdded(amount);// BankTransferDestinationCreditedEvent
		return true;
	}

	//BankTransferSourceReturnMoneyCommand
	public static boolean returnMoney(BankAccount bankAccount, long amount) {
		bankAccount.onMoneyAdded(amount);// BankTransferSourceReturnedMoneyOfFailedEvent
		return true;
	}

	// BankAccountCreatedEvent
	void onCreated(String id, long overdraftLimit) {
		this.id = id;
		this.overdraftLimit = overdraftLimit;
		this.balanceInCents = 0;
	}

	// BankAccountMoneyAddedEvent
	void onMoneyAdded(long amount) {
		balanceInCents += amount;
	}

	// BankAccountMoneySubtractedEvent
	void onMoneySubtracted(long amount) {
		balanceInCents -= amount;
	}
}