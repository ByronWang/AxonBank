package org.axonframework.samples.bankcqrssrc.generatedsources;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
public class MyBankAccountEventListener
  extends MyBankAccountEventListenerBizLogic
{
  private MyBankAccountRepository repository;
  private SimpMessageSendingOperations messagingTemplate;
  
  @Autowired
  public MyBankAccountEventListener(MyBankAccountRepository repository, SimpMessageSendingOperations messagingTemplate)
  {
    this.repository = repository;this.messagingTemplate = messagingTemplate;
  }
  
  private void broadcastUpdates()
  {
    Iterable<MyBankAccountEntry> bankAccountEntries = this.repository.findAll();this.messagingTemplate.convertAndSend("/topic/bank-accounts.updates", bankAccountEntries);
  }
  
  @EventHandler
  public void on(MyBankAccountCreatedEvent event)
  {
    MyBankAccountEntry bankAccountEntry = new MyBankAccountEntry();on(event, bankAccountEntry);this.repository.save(bankAccountEntry);broadcastUpdates();
  }
  
  @EventHandler
  public void on(MyBankAccountMoneyAddedEvent event)
  {
    MyBankAccountEntry bankAccountEntry = (MyBankAccountEntry)this.repository.findOneByAxonBankAccountId(event.getAxonBankAccountId());on(event, bankAccountEntry);this.repository.save(bankAccountEntry);broadcastUpdates();
  }
  
  @EventHandler
  public void on(MyBankAccountMoneySubtractedEvent event)
  {
    MyBankAccountEntry bankAccountEntry = (MyBankAccountEntry)this.repository.findOneByAxonBankAccountId(event.getAxonBankAccountId());on(event, bankAccountEntry);this.repository.save(bankAccountEntry);broadcastUpdates();
  }
}
