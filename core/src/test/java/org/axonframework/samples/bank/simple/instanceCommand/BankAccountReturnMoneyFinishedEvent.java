package org.axonframework.samples.bank.simple.instanceCommand;

public class BankAccountReturnMoneyFinishedEvent
  extends BankAccountMoneyAddedEvent
{
  public BankAccountReturnMoneyFinishedEvent(String id, long amount)
  {
    super(id, amount);
  }
}
