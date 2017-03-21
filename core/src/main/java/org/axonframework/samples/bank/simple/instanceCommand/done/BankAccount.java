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

package org.axonframework.samples.bank.simple.instanceCommand.done;

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

//	public long getOverdraftLimit() {
//		return overdraftLimit;
//	}
//
//	public long getBalanceInCents() {
//		return balanceInCents;
//	}

	public void setId(String id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private BankAccount() {
	}

	public BankAccount(String bankAccountId, long overdraftLimit) {
		onCreated(bankAccountId, overdraftLimit);// BankAccountCreatedEvent
	}

	// BankAccountCreateCommand
	public BankAccount create(String bankAccountId, long overdraftLimit) {
		return new BankAccount(bankAccountId, overdraftLimit);
	}

	// BankAccountMoneyDepositCommand
	public boolean deposit(long amount) {
		onMoneyAdded(amount);// BankAccountMoneyDepositedEvent
		return true;
	}

	// BankAccountWithdrawMoneyCommand
	public boolean withdraw(long amount) {
		if (amount <= balanceInCents + overdraftLimit) {
			onMoneySubtracted(amount);// BankAccountMoneyWithdrawnEvent
			return true;
		} else {
			return false;
		}
	}

	public static boolean bankTransfer(BankAccount source, BankAccount destination, long amount) {// BankTransferCreatedEvent
		boolean sourceDebitSucceed = source.debit(amount);// BankTransferSourceDebitCommand
		if (sourceDebitSucceed) {// BankTransferSourceDebitedEvent
			boolean destinationCreditSucceed = destination.credit(amount);// BankTransferDestinationCreditCommand
			if (destinationCreditSucceed) {// BankTransferDestinationCreditedEvent
				return true;// BankTransferMarkCompletedCommand
			} else {// BankTransferDestinationCreditedRejectedEvent
				source.returnMoney(amount);// BankTransferSourceReturnMoneyCommand
				return false;// BankTransferMarkFailedCommand
			}
		} else {// BankTransferSourceDebitRejectedEvent
			return false;
		}
	}

	// BankTransferSourceDebitCommand
	public boolean debit(long amount) {
		if (amount <= balanceInCents + overdraftLimit) {
			onMoneySubtracted(amount);// BankTransferSourceDebitedEvent
			return true;
		} else {
			return false;// BankTransferSourceDebitRejectedEvent
		}
	}

	// BankTransferDestinationCreditCommand
	public boolean credit(long amount) {
		onMoneyAdded(amount);// BankTransferDestinationCreditedEvent
		return true;
	}

	// BankTransferSourceReturnMoneyCommand
	public boolean returnMoney(long amount) {
		onMoneyAdded(amount);// BankTransferSourceReturnedMoneyOfFailedEvent
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