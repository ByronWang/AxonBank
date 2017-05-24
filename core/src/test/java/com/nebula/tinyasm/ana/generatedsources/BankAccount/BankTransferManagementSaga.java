package com.nebula.tinyasm.ana.generatedsources.BankAccount;

import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferCreatedEvent;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferDestinationCreditCommand;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferDestinationCreditCompletedEvent;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferDestinationCreditFailedEvent;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferMarkCompletedCommand;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferMarkFailedCommand;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferSourceDebitCommand;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferSourceDebitCompletedEvent;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferSourceDebitFailedEvent;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferSourceReturnMoneyCommand;
import javax.inject.Inject;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;

public class BankTransferManagementSaga {
	private transient CommandBus commandBus;
	private String sourceAxonBankAccountId;
	private String destinationAxonBankAccountId;
	private long amount;

	@Inject
	public void setCommandBus(CommandBus commandBus) {
		this.commandBus = commandBus;
	}

	@StartSaga
	@SagaEventHandler(associationProperty = "bankTransferId")
	public void on(BankTransferCreatedEvent event) {
		this.sourceAxonBankAccountId = event.getSourceAxonBankAccountId();
		this.destinationAxonBankAccountId = event.getDestinationAxonBankAccountId();
		this.amount = event.getAmount();
		this.commandBus.dispatch(GenericCommandMessage
		        .asCommandMessage(new BankTransferSourceDebitCommand(this.sourceAxonBankAccountId, event.getBankTransferId(), this.amount)));
	}

	@SagaEventHandler(associationProperty = "bankTransferId")
	public void on(BankTransferSourceDebitCompletedEvent event) {
		this.commandBus.dispatch(GenericCommandMessage
		        .asCommandMessage(new BankTransferDestinationCreditCommand(this.destinationAxonBankAccountId, event.getBankTransferId(), this.amount)));
	}

	@SagaEventHandler(associationProperty = "bankTransferId")
	public void on(BankTransferDestinationCreditCompletedEvent event) {
		this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(new BankTransferMarkCompletedCommand(event.getBankTransferId())));
	}

	@SagaEventHandler(associationProperty = "bankTransferId")
	public void on(BankTransferDestinationCreditFailedEvent event) {
		this.commandBus.dispatch(GenericCommandMessage
		        .asCommandMessage(new BankTransferSourceReturnMoneyCommand(this.sourceAxonBankAccountId, event.getBankTransferId(), this.amount)));
		this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(new BankTransferMarkFailedCommand(event.getBankTransferId())));
	}

	@SagaEventHandler(associationProperty = "bankTransferId")
	public void on(BankTransferSourceDebitFailedEvent event) {
		this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(new BankTransferMarkFailedCommand(event.getBankTransferId())));
	}
}
