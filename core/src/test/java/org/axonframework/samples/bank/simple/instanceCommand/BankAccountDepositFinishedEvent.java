package org.axonframework.samples.bank.simple.instanceCommand;

public class BankAccountDepositFinishedEvent
  extends BankAccountMoneyAddedEvent
{
  public BankAccountDepositFinishedEvent(String id, long amount)
  {
    super(id, amount);
  }
}
