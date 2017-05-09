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

package com.nebula.cqrs.core.asm;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import com.nebula.cqrs.core.CqrsEntity;

@CqrsEntity
@Aggregate
public class MyBankAccount {

	@AggregateIdentifier
	private String axonBankAccountId;
	private long overdraftLimit;
	private long balance;

	@SuppressWarnings("unused")
	private MyBankAccount() {
	}

	// BankAccountCreateCommand
	public MyBankAccount(String axonBankAccountId, long overdraftLimit) {
		onCreated(axonBankAccountId, overdraftLimit);// BankAccountCreatedEvent
	}

	// BankAccountMoneyDepositCommand
	public boolean deposit(long amount) {
		onMoneyAdded(amount);// BankAccountMoneyDepositedEvent
		return true;
	}

	// BankAccountWithdrawMoneyCommand
	public boolean withdraw(long amount) {
		if (amount <= balance + overdraftLimit) {
			onMoneySubtracted(amount);// BankAccountMoneyWithdrawnEvent
			return true;
		} else {
			return false;
		}
	}

	// public static boolean bankTransfer(MyBankAccount source, MyBankAccount
	// destination, long amount) {// BankTransferCreatedEvent
	// boolean sourceDebitSucceed = source.debit(amount);//
	// BankTransferSourceDebitCommand
	// if (sourceDebitSucceed) {// BankTransferSourceDebitedEvent
	// boolean destinationCreditSucceed = destination.credit(amount);//
	// BankTransferDestinationCreditCommand
	// if (destinationCreditSucceed) {// BankTransferDestinationCreditedEvent
	// return true;// BankTransferMarkCompletedCommand
	// } else {// BankTransferDestinationCreditedRejectedEvent
	// source.returnMoney(amount);// BankTransferSourceReturnMoneyCommand
	// return false;// BankTransferMarkFailedCommand
	// }
	// } else {// BankTransferSourceDebitRejectedEvent
	// return false;
	// }
	// }
	//
	// // BankTransferSourceDebitCommand
	// private boolean debit(long amount) {
	// if (amount <= balanceInCents + overdraftLimit) {
	// onMoneySubtracted(amount);// BankTransferSourceDebitedEvent
	// return true;
	// } else {
	// return false;// BankTransferSourceDebitRejectedEvent
	// }
	// }
	//
	// // BankTransferDestinationCreditCommand
	// private boolean credit(long amount) {
	// onMoneyAdded(amount);// BankTransferDestinationCreditedEvent
	// return true;
	// }
	//
	// // BankTransferSourceReturnMoneyCommand
	// private boolean returnMoney(long amount) {
	// onMoneyAdded(amount);// BankTransferSourceReturnedMoneyOfFailedEvent
	// return true;
	// }

	// BankAccountCreatedEvent
	private void onCreated(String axonBankAccountId, long overdraftLimit) {
		this.axonBankAccountId = axonBankAccountId;
		this.overdraftLimit = overdraftLimit;
		this.balance = 0;
	}

	// BankAccountMoneyAddedEvent
	private void onMoneyAdded(long amount) {
		long newbalance = this.balance + amount;
		this.balance = newbalance;
	}

	// BankAccountMoneySubtractedEvent
	private void onMoneySubtracted(long amount) {
		balance -= amount;
	}
}