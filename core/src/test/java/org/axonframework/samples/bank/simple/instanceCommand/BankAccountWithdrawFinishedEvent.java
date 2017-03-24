package org.axonframework.samples.bank.simple.instanceCommand;

public class BankAccountWithdrawFinishedEvent
  extends BankAccountMoneySubtractedEvent
{
  public BankAccountWithdrawFinishedEvent(String id, long amount)
  {
    super(id, amount);
  }
}
