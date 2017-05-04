package org.axonframework.samples.bankcqrssrc.generatedsources;

public class MyBankAccountDepositFinishedEvent
  extends MyBankAccountMoneyAddedEvent
{
  public MyBankAccountDepositFinishedEvent(String axonBankAccountId, long amount)
  {
    super(axonBankAccountId, amount);
  }
}
