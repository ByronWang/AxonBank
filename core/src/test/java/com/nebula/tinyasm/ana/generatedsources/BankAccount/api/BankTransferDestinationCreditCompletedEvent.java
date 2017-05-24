package com.nebula.tinyasm.ana.generatedsources.BankAccount.api;

public class BankTransferDestinationCreditCompletedEvent
{
  private String axonBankAccountId;
  private String bankTransferId;
  private long amount;
  
  public BankTransferDestinationCreditCompletedEvent(String axonBankAccountId, String bankTransferId, long amount)
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
    return "BankTransferDestinationCreditCompletedEvent(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "bankTransferId=" + this.bankTransferId + "," + "amount=" + this.amount + ")";
  }
}
