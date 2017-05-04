package org.axonframework.samples.bankcqrssrc.generatedsources;

public class MyBankAccountWithdrawFinishedEvent
  extends MyBankAccountMoneySubtractedEvent
{
  public MyBankAccountWithdrawFinishedEvent(String axonBankAccountId, long amount)
  {
    super(axonBankAccountId, amount);
  }
}
