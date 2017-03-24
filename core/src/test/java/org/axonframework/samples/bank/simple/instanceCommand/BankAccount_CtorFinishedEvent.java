package org.axonframework.samples.bank.simple.instanceCommand;

public class BankAccount_CtorFinishedEvent
  extends BankAccountCreatedEvent
{
  public BankAccount_CtorFinishedEvent(String id, long overdraftLimit)
  {
    super(id, overdraftLimit);
  }
}
