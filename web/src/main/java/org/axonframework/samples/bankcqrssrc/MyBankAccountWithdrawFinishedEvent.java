package org.axonframework.samples.bankcqrssrc;

public class MyBankAccountWithdrawFinishedEvent
  extends MyBankAccountMoneySubtractedEvent
{
  public MyBankAccountWithdrawFinishedEvent(String axonBankAccountId, long amount)
  {
    super(axonBankAccountId, amount);
  }
}
