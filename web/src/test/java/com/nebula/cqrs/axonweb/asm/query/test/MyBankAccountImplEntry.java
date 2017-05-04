package com.nebula.cqrs.axonweb.asm.query.test;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class MyBankAccountImplEntry
{
  @AggregateIdentifier
  private String axonBankAccountId;
  private long overdraftLimit;
  private long balance;
public String getAxonBankAccountId() {
	return axonBankAccountId;
}
public void setAxonBankAccountId(String axonBankAccountId) {
	this.axonBankAccountId = axonBankAccountId;
}
public long getOverdraftLimit() {
	return overdraftLimit;
}
public void setOverdraftLimit(long overdraftLimit) {
	this.overdraftLimit = overdraftLimit;
}
public long getBalance() {
	return balance;
}
@Override
public String toString() {
	return "MyBankAccountImplEntry [axonBankAccountId=" + axonBankAccountId + ", overdraftLimit=" + overdraftLimit + ", balance=" + balance + "]";
}
public void setBalance(long balance) {
	this.balance = balance;
}
  
}
