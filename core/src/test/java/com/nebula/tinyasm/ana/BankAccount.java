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

package com.nebula.tinyasm.ana;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import com.nebula.cqrs.core.CqrsEntity;

@CqrsEntity
@Aggregate
public class BankAccount {

	@AggregateIdentifier
	private String axonBankAccountId;
	private long overdraftLimit;
	private long balance;

	@SuppressWarnings("unused")
	private BankAccount() {
	}

	// BankAccountCreateCommand
	public BankAccount(String axonBankAccountId, long overdraftLimit) {
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

	public static boolean bankTransfer(BankAccount source, BankAccount destination, long amount) {
		/* On BankTransferCreatedEvent */ {

			/* BankTransferSourceDebitCommand */if (source.debit(amount)) {
				/* On BankTransferSourceDebitedEvent */ {
					// BankTransferDestinationCreditCommand
					if (destination.credit(amount)) {
						/* On destination.credit succeed */ {
							return true;// BankTransferMarkCompletedCommand
						}
					} else {
						/* On destination.credit fail */ {
							/* BankTransferSourceReturnMoneyCommand */source.returnMoney(amount);
							/* On source.returnMoney finished */ {
								return false;// BankTransferMarkFailedCommand
							}
						}
					}
				}
			} else {// BankTransferSourceDebitRejectedEvent
				return false;
			}
		}
	}

	// BankTransferSourceDebitCommand
	boolean debit(long amount) {
		if (amount <= balance + overdraftLimit) {
			onMoneySubtracted(amount);// BankTransferSourceDebitedEvent
			return true;
		} else {
			return false;// BankTransferSourceDebitRejectedEvent
		}
	}

	// BankTransferDestinationCreditCommand
	boolean credit(long amount) {
		onMoneyAdded(amount);// BankTransferDestinationCreditedEvent
		return true;
	}

	// BankTransferSourceReturnMoneyCommand
	boolean returnMoney(long amount) {
		onMoneyAdded(amount);// BankTransferSourceReturnedMoneyOfFailedEvent
		return true;
	}

	// BankAccountCreatedEvent
	private void onCreated(String axonBankAccountId, long overdraftLimit) {
		this.axonBankAccountId = axonBankAccountId;
		this.overdraftLimit = overdraftLimit;
		this.balance = 0;
	}

	// BankAccountMoneyAddedEvent
	private void onMoneyAdded(long amount) {
		balance += amount;
	}

	// BankAccountMoneySubtractedEvent
	private void onMoneySubtracted(long amount) {
		balance -= amount;
	}
}