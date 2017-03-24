package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class BankAccountCreditCommand
{
  @TargetAggregateIdentifier
  private final String id;
  private final long amount;
  
  public String toString()
  {
    return "BankAccountCreditCommand(" + "id=" + getId() + "," + "amount=" + getAmount() + ")";
  }
  
  @ConstructorProperties({"id", "amount"})
  public BankAccountCreditCommand(String id, long amount)
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
