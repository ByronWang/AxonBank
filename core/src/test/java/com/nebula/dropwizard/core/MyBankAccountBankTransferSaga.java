package com.nebula.dropwizard.core;

import org.axonframework.commandhandling.CommandBus;

public class MyBankAccountBankTransferSaga
{
  private CommandBus commandBus;
  private String sourceAxonBankAccountId;
  private String destinationAxonBankAccountId;
  private long amount;
  
  public void onsourceDebit(MyBankAccountSourceDebitSucceedEvent paramMyBankAccountSourceDebitSucceedEvent){
	  
  }
}
