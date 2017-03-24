package org.axonframework.samples.bank.simple.instanceCommand;

public class BankAccountDebitFinishedEvent
  extends BankAccountMoneySubtractedEvent
{
  public BankAccountDebitFinishedEvent(String id, long amount)
  {
    super(id, amount);
  }
}
