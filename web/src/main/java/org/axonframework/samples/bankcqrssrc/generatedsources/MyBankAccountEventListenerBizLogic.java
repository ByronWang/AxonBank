package org.axonframework.samples.bankcqrssrc;

public class MyBankAccountEventListenerBizLogic
{
  public void on(MyBankAccountCreatedEvent event, MyBankAccountEntry entry)
  {
    entry.setAxonBankAccountId(event.getAxonBankAccountId());
    entry.setOverdraftLimit(event.getOverdraftLimit());
    entry.setBalance(0L);
  }
  
  public void on(MyBankAccountMoneyAddedEvent event, MyBankAccountEntry entry)
  {
    MyBankAccountEntry tmp2_0 = entry;tmp2_0.setBalance(tmp2_0.getBalance() + event.getAmount());
  }
  
  public void on(MyBankAccountMoneySubtractedEvent event, MyBankAccountEntry entry)
  {
    MyBankAccountEntry tmp2_0 = entry;tmp2_0.setBalance(tmp2_0.getBalance() - event.getAmount());
  }
}
