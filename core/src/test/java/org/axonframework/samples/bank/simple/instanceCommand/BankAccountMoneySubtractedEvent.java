package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;

public abstract class BankAccountMoneySubtractedEvent
{
  private final String id;
  private final long amount;
  
  public String toString()
  {
    return "BankAccountMoneySubtractedEvent(" + "id=" + getId() + "," + "amount=" + getAmount() + ")";
  }
  
  @ConstructorProperties({"id", "amount"})
  public BankAccountMoneySubtractedEvent(String id, long amount)
  {
    this.id = id;
    this.amount = amount;
  }
  
  public long getAmount()
  {
    return this.amount;
  }
  
  public String getId()
  {
    return this.id;
  }
}
