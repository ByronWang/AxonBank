package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class BankAccountReturnMoneyCommand
{
  @TargetAggregateIdentifier
  private final String id;
  private final long amount;
  
  public String toString()
  {
    return "BankAccountReturnMoneyCommand(" + "id=" + getId() + "," + "amount=" + getAmount() + ")";
  }
  
  @ConstructorProperties({"id", "amount"})
  public BankAccountReturnMoneyCommand(String id, long amount)
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
