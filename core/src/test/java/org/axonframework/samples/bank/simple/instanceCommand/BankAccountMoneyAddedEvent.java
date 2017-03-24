package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;

public abstract class BankAccountMoneyAddedEvent
{
  private final String id;
  private final long amount;
  
  public String toString()
  {
    return "BankAccountMoneyAddedEvent(" + "id=" + getId() + "," + "amount=" + getAmount() + ")";
  }
  
  @ConstructorProperties({"id", "amount"})
  public BankAccountMoneyAddedEvent(String id, long amount)
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
