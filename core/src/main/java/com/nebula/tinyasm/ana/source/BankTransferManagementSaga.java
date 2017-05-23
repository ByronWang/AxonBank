package com.nebula.tinyasm.ana.source;

import org.axonframework.commandhandling.GenericCommandMessage;

import javax.inject.Inject;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

@Saga
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
	
	@EndSaga
	@SagaEventHandler(associationProperty = "bankTransferId")
	public void on(BankTransferDestinationCreditCompletedEvent event) {
		this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(new BankTransferMarkCompletedCommand(event.getBankTransferId())));
	}
	

	@SagaEventHandler(associationProperty = "bankTransferId")
	@EndSaga
	public void on(BankTransferDestinationCreditFailedEvent event) {
		this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(new BankTransferSourceReturnMoneyCommand(this.sourceAxonBankAccountId, this.amount)));
		this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(new BankTransferMarkFailedCommand(event.getBankTransferId())));
	}

	@SagaEventHandler(associationProperty = "bankTransferId")
	@EndSaga
	public void on(BankTransferSourceNotFoundEvent event) {
		this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(new BankTransferMarkFailedCommand(event.getBankTransferId())));
	}
	
	@SagaEventHandler(associationProperty = "bankTransferId")
	@EndSaga
	public void on(BankTransferSourceDebitFailedEvent event) {
		this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(new BankTransferMarkFailedCommand(event.getBankTransferId())));
	}
}