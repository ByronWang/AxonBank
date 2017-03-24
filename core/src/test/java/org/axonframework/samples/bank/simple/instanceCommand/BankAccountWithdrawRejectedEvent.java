package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;

public abstract class BankAccountWithdrawRejectedEvent
{
  private final String id;
  
  public String toString()
  {
    return "BankAccountWithdrawRejectedEvent(" + "id=" + getId() + ")";
  }
  
  @ConstructorProperties({"id"})
  public BankAccountWithdrawRejectedEvent(String id)
  {
    this.id = id;
  }
  
  public String getId()
  {
    return this.id;
  }
}
