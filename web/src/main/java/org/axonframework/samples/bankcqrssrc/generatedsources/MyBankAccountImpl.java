package org.axonframework.samples.bankcqrssrc.generatedsources;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class MyBankAccountImpl
{
  @AggregateIdentifier
  private String axonBankAccountId;
  private long overdraftLimit;
  private long balance;
  
  public String toString()
  {
    return "MyBankAccountImpl(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "overdraftLimit=" + this.overdraftLimit + "," + "balance=" + this.balance + ")";
  }
  
  public MyBankAccountImpl(String axonBankAccountId, long overdraftLimit)
  {
    applyCreateFinished(axonBankAccountId, overdraftLimit);
  }
  
  public boolean deposit(long amount)
  {
    applyDepositFinished(amount);
    return true;
  }
  
  private final void applyWithdrawFinished(long amount)
  {
    AggregateLifecycle.apply(new MyBankAccountWithdrawFinishedEvent(this.axonBankAccountId, amount));
  }
  
  private final void applyCreateFinished(String axonBankAccountId, long overdraftLimit)
  {
    AggregateLifecycle.apply(new MyBankAccountCreateFinishedEvent(axonBankAccountId, overdraftLimit));
  }
  
  private final void applyDepositFinished(long amount)
  {
    AggregateLifecycle.apply(new MyBankAccountDepositFinishedEvent(this.axonBankAccountId, amount));
  }
  
  public boolean withdraw(long amount)
  {
    if (amount <= this.balance + this.overdraftLimit)
    {
      applyWithdrawFinished(amount);
      return true;
    }
    return false;
  }
  
  @EventHandler
  private void on(MyBankAccountCreatedEvent event)
  {
    this.axonBankAccountId = event.getAxonBankAccountId();
    this.overdraftLimit = event.getOverdraftLimit();
    this.balance = 0L;
  }
  
  @EventHandler
  private void on(MyBankAccountMoneyAddedEvent event)
  {
    this.balance += event.getAmount();
  }
  
  @EventHandler
  private void on(MyBankAccountMoneySubtractedEvent event)
  {
    this.balance -= event.getAmount();
  }
  
  private MyBankAccountImpl() {}
}
