package org.axonframework.samples.bankcqrssrc;

public class MyBankAccountCreateFinishedEvent
  extends MyBankAccountCreatedEvent
{
  public MyBankAccountCreateFinishedEvent(String axonBankAccountId, long overdraftLimit)
  {
    super(axonBankAccountId, overdraftLimit);
  }
}
