package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;

public abstract class BankAccountCreatedEvent
{
  private final String id;
  private final long overdraftLimit;
  
  public String toString()
  {
    return "BankAccountCreatedEvent(" + "id=" + getId() + "," + "overdraftLimit=" + getOverdraftLimit() + ")";
  }
  
  @ConstructorProperties({"id", "overdraftLimit"})
  public BankAccountCreatedEvent(String id, long overdraftLimit)
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
