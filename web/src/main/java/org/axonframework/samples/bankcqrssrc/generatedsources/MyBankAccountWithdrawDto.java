package org.axonframework.samples.bankcqrssrc.generatedsources;

public class MyBankAccountWithdrawDto
{
  private String axonBankAccountId;
  private long amount;
  
  public String toString()
  {
    return "MyBankAccountWithdrawDto(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "amount=" + this.amount + ")";
  }
  
  public MyBankAccountWithdrawDto(String axonBankAccountId, long amount)
  {
    this.axonBankAccountId = axonBankAccountId;
    this.amount = amount;
  }
  
  public MyBankAccountWithdrawDto() {}
  
  public long getAmount()
  {
    return this.amount;
  }
  
  public String getAxonBankAccountId()
  {
    return this.axonBankAccountId;
  }
}
