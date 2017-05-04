package org.axonframework.samples.bankcqrssrc;

public class MyBankAccountDepositFinishedEvent
  extends MyBankAccountMoneyAddedEvent
{
  public MyBankAccountDepositFinishedEvent(String axonBankAccountId, long amount)
  {
    super(axonBankAccountId, amount);
  }
}
