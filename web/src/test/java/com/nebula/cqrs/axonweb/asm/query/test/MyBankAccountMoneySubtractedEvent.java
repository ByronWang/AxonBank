package com.nebula.cqrs.axonweb.asm.query.test;

public abstract class MyBankAccountMoneySubtractedEvent
{
  private String axonBankAccountId;
  private long amount;
  
  public String toString()
  {
    return "MyBankAccountMoneySubtractedEvent(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "amount=" + this.amount + ")";
  }
  
  public MyBankAccountMoneySubtractedEvent(String axonBankAccountId, long amount)
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
