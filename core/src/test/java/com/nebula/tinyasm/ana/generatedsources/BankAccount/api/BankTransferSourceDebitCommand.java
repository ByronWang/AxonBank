package com.nebula.tinyasm.ana.generatedsources.BankAccount.api;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class BankTransferSourceDebitCommand
{
  @TargetAggregateIdentifier
  private String axonBankAccountId;
  private String bankTransferId;
  private long amount;
  
  public BankTransferSourceDebitCommand(String axonBankAccountId, String bankTransferId, long amount)
  {
    this.axonBankAccountId = axonBankAccountId;
    this.bankTransferId = bankTransferId;
    this.amount = amount;
  }
  
  public String getAxonBankAccountId()
  {
    return this.axonBankAccountId;
  }
  
  public String getBankTransferId()
  {
    return this.bankTransferId;
  }
  
  public long getAmount()
  {
    return this.amount;
  }
  
  public String toString(String axonBankAccountId, String bankTransferId, long amount)
  {
    return "BankTransferSourceDebitCommand(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "bankTransferId=" + this.bankTransferId + "," + "amount=" + this.amount + ")";
  }
}
