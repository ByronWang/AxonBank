package com.nebula.tinyasm.ana.generatedsources.BankAccount.api;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class BankTransferCreateCommand
{
  @TargetAggregateIdentifier
  private String bankTransferId;
  private String sourceAxonBankAccountId;
  private String destinationAxonBankAccountId;
  private long amount;
  
  public BankTransferCreateCommand(String bankTransferId, String sourceAxonBankAccountId, String destinationAxonBankAccountId, long amount)
  {
    this.bankTransferId = bankTransferId;
    this.sourceAxonBankAccountId = sourceAxonBankAccountId;
    this.destinationAxonBankAccountId = destinationAxonBankAccountId;
    this.amount = amount;
  }
  
  public String getBankTransferId()
  {
    return this.bankTransferId;
  }
  
  public String getSourceAxonBankAccountId()
  {
    return this.sourceAxonBankAccountId;
  }
  
  public String getDestinationAxonBankAccountId()
  {
    return this.destinationAxonBankAccountId;
  }
  
  public long getAmount()
  {
    return this.amount;
  }
  
  public String toString(String bankTransferId, String sourceAxonBankAccountId, String destinationAxonBankAccountId, long amount)
  {
    return "BankTransferCreateCommand(" + "bankTransferId=" + this.bankTransferId + "," + "sourceAxonBankAccountId=" + this.sourceAxonBankAccountId + "," + "destinationAxonBankAccountId=" + this.destinationAxonBankAccountId + "," + "amount=" + this.amount + ")";
  }
}
