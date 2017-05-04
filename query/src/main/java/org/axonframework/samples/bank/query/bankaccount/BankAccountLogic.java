package org.axonframework.samples.bank.query.bankaccount;

import org.axonframework.samples.bank.api.bankaccount.BankAccountCreatedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneyAddedEvent;
import org.axonframework.samples.bank.api.bankaccount.BankAccountMoneySubtractedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankAccountLogic {
	private final static Logger LOGGER = LoggerFactory.getLogger(BankAccountLogic.class);

	protected void on(BankAccountCreatedEvent event, BankAccountEntry entry) {
		entry.setAxonBankAccountId(event.getId());
		entry.setOverdraftLimit(event.getOverdraftLimit());
		entry.setBalance(0);
		LOGGER.debug("after event : {}", event);
		LOGGER.debug("entry become : {}", entry);
	}

	protected void on(BankAccountMoneyAddedEvent event, BankAccountEntry entry) {
		entry.setBalance(entry.getBalance() + event.getAmount());
		LOGGER.debug("after event : {}", event);
		LOGGER.debug("entry become : {}", entry);
	}

	protected void on(BankAccountMoneySubtractedEvent event, BankAccountEntry entry) {
		entry.setBalance(entry.getBalance() - event.getAmount());
		LOGGER.debug("after event : {}", event);
		LOGGER.debug("entry become : {}", entry);
	}

}
