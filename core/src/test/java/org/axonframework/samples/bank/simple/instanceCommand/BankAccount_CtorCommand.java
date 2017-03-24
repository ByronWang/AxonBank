package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class BankAccount_CtorCommand
{
  @TargetAggregateIdentifier
  private final String id;
  private final long overdraftLimit;
  
  public String toString()
  {
    return "BankAccount_CtorCommand(" + "id=" + getId() + "," + "overdraftLimit=" + getOverdraftLimit() + ")";
  }
  
  @ConstructorProperties({"id", "overdraftLimit"})
  public BankAccount_CtorCommand(String id, long overdraftLimit)
  {
    this.id = id;
    this.overdraftLimit = overdraftLimit;
  }
  
  public long getOverdraftLimit()
  {
    return this.overdraftLimit;
  }
  
  public String getId()
  {
    return this.id;
  }
}
