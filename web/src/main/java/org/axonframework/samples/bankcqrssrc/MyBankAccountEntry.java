package org.axonframework.samples.bankcqrssrc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class MyBankAccountEntry
{
  @Id
  @GeneratedValue
  private long id;
  private String axonBankAccountId;
  private long overdraftLimit;
  private long balance;
  
  public long getId()
  {
    return this.id;
  }
  
  public String getAxonBankAccountId()
  {
    return this.axonBankAccountId;
  }
  
  public long getOverdraftLimit()
  {
    return this.overdraftLimit;
  }
  
  public long getBalance()
  {
    return this.balance;
  }
  
  public String toString()
  {
    return "MyBankAccountEntry(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "overdraftLimit=" + this.overdraftLimit + "," + "balance=" + this.balance + ")";
  }
  
  public void setId(long id)
  {
    this.id = id;
  }
  
  public void setAxonBankAccountId(String axonBankAccountId)
  {
    this.axonBankAccountId = axonBankAccountId;
  }
  
  public void setOverdraftLimit(long overdraftLimit)
  {
    this.overdraftLimit = overdraftLimit;
  }
  
  public void setBalance(long balance)
  {
    this.balance = balance;
  }
  
  public MyBankAccountEntry() {}
  
  public MyBankAccountEntry(String axonBankAccountId, long overdraftLimit, long balance)
  {
    this.axonBankAccountId = axonBankAccountId;
    this.overdraftLimit = overdraftLimit;
    this.balance = balance;
  }
}
