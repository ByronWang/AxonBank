package org.axonframework.samples.bankcqrssrc;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class MyBankAccountDepositCommand
{
  @TargetAggregateIdentifier
  private String axonBankAccountId;
  private long amount;
  
  public String toString()
  {
    return "MyBankAccountDepositCommand(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "amount=" + this.amount + ")";
  }
  
  public MyBankAccountDepositCommand(String axonBankAccountId, long amount)
  {
    this.axonBankAccountId = axonBankAccountId;
    this.amount = amount;
  }
  
  public long getAmount()
  {
    return this.amount;
  }
  
  public String getAxonBankAccountId()
  {
    return this.axonBankAccountId;
  }
}
