package org.axonframework.samples.bank.simple.instanceCommand;

public class BankAccountCreditFinishedEvent
  extends BankAccountMoneyAddedEvent
{
  public BankAccountCreditFinishedEvent(String id, long amount)
  {
    super(id, amount);
  }
}
