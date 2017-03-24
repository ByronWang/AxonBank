package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;

public abstract class BankAccountDebitRejectedEvent
{
  private final String id;
  
  public String toString()
  {
    return "BankAccountDebitRejectedEvent(" + "id=" + getId() + ")";
  }
  
  @ConstructorProperties({"id"})
  public BankAccountDebitRejectedEvent(String id)
  {
    this.id = id;
  }
  
  public String getId()
  {
    return this.id;
  }
}
