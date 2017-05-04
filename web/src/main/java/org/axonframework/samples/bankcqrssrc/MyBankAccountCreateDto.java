package org.axonframework.samples.bankcqrssrc;

public class MyBankAccountCreateDto
{
  private long overdraftLimit;
  
  public String toString()
  {
    return "MyBankAccountCreateDto(" + "overdraftLimit=" + this.overdraftLimit + ")";
  }
  
  public MyBankAccountCreateDto(long overdraftLimit)
  {
    this.overdraftLimit = overdraftLimit;
  }
  
  public MyBankAccountCreateDto() {}
  
  public long getOverdraftLimit()
  {
    return this.overdraftLimit;
  }
}
