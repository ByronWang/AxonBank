package org.axonframework.samples.bankcqrssrc.generatedsources;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class MyBankAccountCreateCommand
{
  @TargetAggregateIdentifier
  private String axonBankAccountId;
  private long overdraftLimit;
  
  public String toString()
  {
    return "MyBankAccountCreateCommand(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "overdraftLimit=" + this.overdraftLimit + ")";
  }
  
  public MyBankAccountCreateCommand(String axonBankAccountId, long overdraftLimit)
  {
    this.axonBankAccountId = axonBankAccountId;
    this.overdraftLimit = overdraftLimit;
  }
  
  public long getOverdraftLimit()
  {
    return this.overdraftLimit;
  }
  
  public String getAxonBankAccountId()
  {
    return this.axonBankAccountId;
  }
}
