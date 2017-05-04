package org.axonframework.samples.bankcqrssrc.generatedsources;

import java.util.UUID;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping({"/bank-accounts"})
public class MyBankAccountController
{
  private CommandBus commandBus;
  private MyBankAccountRepository bankAccountRepository;
  
  @Autowired
  public MyBankAccountController(CommandBus commandBus, MyBankAccountRepository bankAccountRepository)
  {
    this.commandBus = commandBus;this.bankAccountRepository = bankAccountRepository;
  }
  
  @SubscribeMapping
  public Iterable<MyBankAccountEntry> all()
  {
    return this.bankAccountRepository.findAllByOrderByIdAsc();
  }
  
  @SubscribeMapping({"/{id}"})
  public MyBankAccountEntry get(@DestinationVariable String id)
  {
    return (MyBankAccountEntry)this.bankAccountRepository.findOne(id);
  }
  
  @SubscribeMapping({"/create"})
  public void create(MyBankAccountCreateDto dto)
  {
    String axonBankAccountId = UUID.randomUUID().toString();MyBankAccountCreateCommand command = new MyBankAccountCreateCommand(axonBankAccountId, dto.getOverdraftLimit());this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
  }
  
  @SubscribeMapping({"/deposit"})
  public void deposit(MyBankAccountDepositDto dto)
  {
    MyBankAccountDepositCommand command = new MyBankAccountDepositCommand(dto.getAxonBankAccountId(), dto.getAmount());this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
  }
  
  @SubscribeMapping({"/withdraw"})
  public void withdraw(MyBankAccountWithdrawDto dto)
  {
    MyBankAccountWithdrawCommand command = new MyBankAccountWithdrawCommand(dto.getAxonBankAccountId(), dto.getAmount());this.commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
  }
}
