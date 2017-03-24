package org.axonframework.samples.bank.simple.instanceCommand;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class BankAccount
{
  @AggregateIdentifier
  private String id;
  private long overdraftLimit;
  private long balanceInCents;
  
  public BankAccount(String id, long overdraftLimit)
  {
    applyBankAccount_CtorFinishedEvent(id, overdraftLimit);
  }
  
  public boolean deposit(long amount)
  {
    applyBankAccountDepositFinishedEvent(amount);
    return true;
  }
  
  public boolean withdraw(long amount)
  {
    if (amount <= this.balanceInCents + this.overdraftLimit)
    {
      applyBankAccountWithdrawFinishedEvent(amount);
      return true;
    }
    applyBankAccountWithdrawRejectedEvent();return false;
  }
  
  private final void applyBankAccount_CtorFinishedEvent(String id, long overdraftLimit)
  {
    AggregateLifecycle.apply(new BankAccount_CtorFinishedEvent(id, overdraftLimit));
  }
  
  private final void applyBankAccountDepositFinishedEvent(long amount)
  {
    AggregateLifecycle.apply(new BankAccountDepositFinishedEvent(this.id, amount));
  }
  
  private final void applyBankAccountWithdrawFinishedEvent(long amount)
  {
    AggregateLifecycle.apply(new BankAccountWithdrawFinishedEvent(this.id, amount));
  }
  
  private final void applyBankAccountWithdrawRejectedEvent()
  {
    AggregateLifecycle.apply(new BankAccountWithdrawRejectedEvent(this.id));
  }
  
  private final void applyBankAccountDebitFinishedEvent(long amount)
  {
    AggregateLifecycle.apply(new BankAccountDebitFinishedEvent(this.id, amount));
  }
  
  private final void applyBankAccountDebitRejectedEvent()
  {
    AggregateLifecycle.apply(new BankAccountDebitRejectedEvent(this.id));
  }
  
  private final void applyBankAccountCreditFinishedEvent(long amount)
  {
    AggregateLifecycle.apply(new BankAccountCreditFinishedEvent(this.id, amount));
  }
  
  private final void applyBankAccountReturnMoneyFinishedEvent(long amount)
  {
    AggregateLifecycle.apply(new BankAccountReturnMoneyFinishedEvent(this.id, amount));
  }
  
  public static boolean bankTransfer(BankAccount source, BankAccount destination, long amount)
  {
    boolean sourceDebitSucceed = source.debit(amount);
    if (sourceDebitSucceed)
    {
      boolean destinationCreditSucceed = destination.credit(amount);
      if (destinationCreditSucceed) {
        return true;
      }
      source.returnMoney(amount);
      return false;
    }
    return false;
  }
  
  public boolean debit(long amount)
  {
    if (amount <= this.balanceInCents + this.overdraftLimit)
    {
      applyBankAccountDebitFinishedEvent(amount);
      return true;
    }
    applyBankAccountDebitRejectedEvent();return false;
  }
  
  public boolean credit(long amount)
  {
    applyBankAccountCreditFinishedEvent(amount);
    return true;
  }
  
  public boolean returnMoney(long amount)
  {
    applyBankAccountReturnMoneyFinishedEvent(amount);
    return true;
  }
  
  @EventHandler
  void on(BankAccountCreatedEvent created)
  {
    this.id = created.getId();
    this.overdraftLimit = created.getOverdraftLimit();
    this.balanceInCents = 0L;
  }
  
  @EventHandler
  void on(BankAccountMoneyAddedEvent moneyAdded)
  {
    this.balanceInCents += moneyAdded.getAmount();
  }
  
  @EventHandler
  void on(BankAccountMoneySubtractedEvent moneySubtracted)
  {
    this.balanceInCents -= moneySubtracted.getAmount();
  }
  
  private BankAccount() {}
}
