package org.axonframework.samples.bankcqrssrc;

public abstract class MyBankAccountCreatedEvent
{
  private String axonBankAccountId;
  private long overdraftLimit;
  
  public String toString()
  {
    return "MyBankAccountCreatedEvent(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "overdraftLimit=" + this.overdraftLimit + ")";
  }
  
  public MyBankAccountCreatedEvent(String axonBankAccountId, long overdraftLimit)
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
