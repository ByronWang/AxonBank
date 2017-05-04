package com.nebula.cqrs.axonweb.asm.query.test;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class MyBankAccountImpl {
	@AggregateIdentifier
	private String axonBankAccountId;
	private long overdraftLimit;
	private long balance;

	@EventHandler
	void on(MyBankAccountCreatedEvent created) {
		this.axonBankAccountId = created.getAxonBankAccountId();
		this.overdraftLimit = created.getOverdraftLimit();
		this.balance = 0L;
		int i = 0;
		i = i + 10;
		this.balance = this.balance + i;
		System.out.println("balance : " + this.balance);
	}

	@EventHandler
	void on(MyBankAccountMoneyAddedEvent moneyAdded) {
		this.balance += moneyAdded.getAmount();
		System.out.println(this);
	}

	@EventHandler
	void on(MyBankAccountMoneySubtractedEvent moneySubtracted) {
		this.balance -= moneySubtracted.getAmount();
		int i = 0;
		i = i + 10;
		this.balance = this.balance + i;
		System.out.println("balance : " + this.balance);
	}

	private MyBankAccountImpl() {
	}
}
