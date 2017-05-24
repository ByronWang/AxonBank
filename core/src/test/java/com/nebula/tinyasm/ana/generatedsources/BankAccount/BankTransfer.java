package com.nebula.tinyasm.ana.generatedsources.BankAccount;

import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferCompletedEvent;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferCreateCommand;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferCreatedEvent;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferFailedEvent;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferMarkCompletedCommand;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferMarkFailedCommand;
import com.nebula.tinyasm.ana.generatedsources.BankAccount.api.BankTransferStatus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventhandling.EventHandler;

public class BankTransfer
{
  @AggregateIdentifier
  private String bankTransferId;
  private String sourceAxonBankAccountId;
  private String destinationAxonBankAccountId;
  private long amount;
  private BankTransferStatus status;
  
  public BankTransfer() {}
  
  @CommandHandler
  public BankTransfer(BankTransferCreateCommand command)
  {
    AggregateLifecycle.apply(new BankTransferCreatedEvent(command.getBankTransferId(), command.getSourceAxonBankAccountId(), command.getDestinationAxonBankAccountId(), command.getAmount()));
  }
  
  @EventHandler
  public void on(BankTransferCreatedEvent event)
  {
    this.bankTransferId = event.getBankTransferId();
    this.sourceAxonBankAccountId = event.getSourceAxonBankAccountId();
    this.destinationAxonBankAccountId = event.getDestinationAxonBankAccountId();
    this.amount = event.getAmount();
    this.status = BankTransferStatus.Started;
  }
  
  @CommandHandler
  public void handle(BankTransferMarkCompletedCommand command)
  {
    AggregateLifecycle.apply(new BankTransferCompletedEvent(command.getBankTransferId()));
  }
  
  @EventHandler
  public void on(BankTransferCompletedEvent event)
  {
    this.status = BankTransferStatus.Completed;
  }
  
  @CommandHandler
  public void handle(BankTransferMarkFailedCommand command)
  {
    AggregateLifecycle.apply(new BankTransferFailedEvent(command.getBankTransferId()));
  }
  
  @EventHandler
  public void on(BankTransferFailedEvent event)
  {
    this.status = BankTransferStatus.Failed;
  }
}
