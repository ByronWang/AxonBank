package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class BankAccountDebitCommand
{
  @TargetAggregateIdentifier
  private final String id;
  private final long amount;
  
  public String toString()
  {
    return "BankAccountDebitCommand(" + "id=" + getId() + "," + "amount=" + getAmount() + ")";
  }
  
  @ConstructorProperties({"id", "amount"})
  public BankAccountDebitCommand(String id, long amount)
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
