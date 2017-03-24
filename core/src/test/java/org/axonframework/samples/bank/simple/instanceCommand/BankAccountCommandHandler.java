package org.axonframework.samples.bank.simple.instanceCommand;

import java.util.function.Consumer;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;

public class BankAccountCommandHandler
{
  private Repository<BankAccount> repository;
  private EventBus eventBus;
  
  public BankAccountCommandHandler(Repository<BankAccount> repository, EventBus eventBus)
  {
    this.repository = repository;
    this.eventBus = eventBus;
  }
  
  @CommandHandler
  public void handle(BankAccount_CtorCommand command)
    throws Exception
  {
    this.repository.newInstance(new BankAccountCommandHandler.InnerBankAccount_CtorCommand(this, command));
  }
  
  @CommandHandler
  public void handle(final BankAccountDepositCommand command)
  {
    Aggregate<BankAccount> bankAccountAggregate = this.repository.load(command.getId());
    bankAccountAggregate.execute(new InnerBankAccountDepositCommand(command));
  }
  
  @CommandHandler
  public void handle(final BankAccountDebitCommand command)
  {
    Aggregate<BankAccount> bankAccountAggregate = this.repository.load(command.getId());
    bankAccountAggregate.execute(new InnerBankAccountDebitCommand(command));
  }
  
  @CommandHandler
  public void handle(final BankAccountWithdrawCommand command)
  {
    Aggregate<BankAccount> bankAccountAggregate = this.repository.load(command.getId());
    bankAccountAggregate.execute(new InnerBankAccountWithdrawCommand(command));
  }
  
  @CommandHandler
  public void handle(final BankAccountReturnMoneyCommand command)
  {
    Aggregate<BankAccount> bankAccountAggregate = this.repository.load(command.getId());
    bankAccountAggregate.execute(new InnerBankAccountReturnMoneyCommand(command));
  }
  
  @CommandHandler
  public void handle(final BankAccountCreditCommand command)
  {
    Aggregate<BankAccount> bankAccountAggregate = this.repository.load(command.getId());
    bankAccountAggregate.execute(new InnerBankAccountCreditCommand(command));
  }
  
  class InnerBankAccountCreditCommand
    implements Consumer<BankAccount>
  {
    InnerBankAccountCreditCommand(BankAccountCreditCommand paramBankAccountCreditCommand) {}
    
    public void accept(BankAccount bankAccount)
    {
      bankAccount.credit(command.getAmount());
    }
  }
  
  class InnerBankAccountReturnMoneyCommand
    implements Consumer<BankAccount>
  {
    InnerBankAccountReturnMoneyCommand(BankAccountReturnMoneyCommand paramBankAccountReturnMoneyCommand) {}
    
    public void accept(BankAccount bankAccount)
    {
      bankAccount.returnMoney(command.getAmount());
    }
  }
  
  class InnerBankAccountWithdrawCommand
    implements Consumer<BankAccount>
  {
    InnerBankAccountWithdrawCommand(BankAccountWithdrawCommand paramBankAccountWithdrawCommand) {}
    
    public void accept(BankAccount bankAccount)
    {
      bankAccount.withdraw(command.getAmount());
    }
  }
  
  class InnerBankAccountDebitCommand
    implements Consumer<BankAccount>
  {
    InnerBankAccountDebitCommand(BankAccountDebitCommand paramBankAccountDebitCommand) {}
    
    public void accept(BankAccount bankAccount)
    {
      bankAccount.debit(command.getAmount());
    }
  }
  
  class InnerBankAccountDepositCommand
    implements Consumer<BankAccount>
  {
    InnerBankAccountDepositCommand(BankAccountDepositCommand paramBankAccountDepositCommand) {}
    
    public void accept(BankAccount bankAccount)
    {
      bankAccount.deposit(command.getAmount());
    }
  }
}
