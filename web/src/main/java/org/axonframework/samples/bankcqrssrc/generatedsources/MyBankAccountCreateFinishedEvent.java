package org.axonframework.samples.bankcqrssrc.generatedsources;

public class MyBankAccountCreateFinishedEvent
  extends MyBankAccountCreatedEvent
{
  public MyBankAccountCreateFinishedEvent(String axonBankAccountId, long overdraftLimit)
  {
    super(axonBankAccountId, overdraftLimit);
  }
}
