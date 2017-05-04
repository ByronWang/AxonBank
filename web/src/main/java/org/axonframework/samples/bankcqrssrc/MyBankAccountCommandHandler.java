package org.axonframework.samples.bankcqrssrc;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;

public class MyBankAccountCommandHandler
{
  private Repository<MyBankAccountImpl> repository;
  private EventBus eventBus;
  
  public MyBankAccountCommandHandler(Repository<MyBankAccountImpl> repository, EventBus eventBus)
  {
    this.repository = repository;this.eventBus = eventBus;
  }
  
  @CommandHandler
  public void handle(MyBankAccountCreateCommand command)
    throws Exception
  {
    this.repository.newInstance(new InnerCreate(command));
  }
  
  @CommandHandler
  public void handle(MyBankAccountDepositCommand command)
  {
    Aggregate<MyBankAccountImpl> aggregate = this.repository.load(command.getAxonBankAccountId());aggregate.execute(new InnerDeposit(command));
  }
  
  @CommandHandler
  public void handle(MyBankAccountWithdrawCommand command)
  {
    Aggregate<MyBankAccountImpl> aggregate = this.repository.load(command.getAxonBankAccountId());aggregate.execute(new InnerWithdraw(command));
  }
  
  class InnerDeposit
    implements Consumer<MyBankAccountImpl>
  {
    MyBankAccountDepositCommand command;
    
    InnerDeposit(MyBankAccountDepositCommand command)
    {
      this.command = command;
    }
    
    public void accept(MyBankAccountImpl domain)
    {
      domain.deposit(this.command.getAmount());
    }
  }
  
  class InnerWithdraw
    implements Consumer<MyBankAccountImpl>
  {
    MyBankAccountWithdrawCommand command;
    
    InnerWithdraw(MyBankAccountWithdrawCommand command)
    {
      this.command = command;
    }
    
    public void accept(MyBankAccountImpl domain)
    {
      domain.withdraw(this.command.getAmount());
    }
  }
  
  class InnerCreate
    implements Callable<MyBankAccountImpl>
  {
    MyBankAccountCreateCommand command;
    
    InnerCreate(MyBankAccountCreateCommand command)
    {
      this.command = command;
    }
    
    public MyBankAccountImpl call()
      throws Exception
    {
      return new MyBankAccountImpl(this.command.getAxonBankAccountId(), this.command.getOverdraftLimit());
    }
  }
}
