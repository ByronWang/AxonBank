package com.nebula.cqrs.axonweb.asm.query.test;

public abstract class MyBankAccountMoneyAddedEvent
{
  private String axonBankAccountId;
  private long amount;
  
  public String toString()
  {
    return "MyBankAccountMoneyAddedEvent(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "amount=" + this.amount + ")";
  }
  
  public MyBankAccountMoneyAddedEvent(String axonBankAccountId, long amount)
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
