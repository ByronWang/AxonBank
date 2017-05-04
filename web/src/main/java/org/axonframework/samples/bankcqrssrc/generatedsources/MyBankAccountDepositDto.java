package org.axonframework.samples.bankcqrssrc;

public class MyBankAccountDepositDto
{
  private String axonBankAccountId;
  private long amount;
  
  public String toString()
  {
    return "MyBankAccountDepositDto(" + "axonBankAccountId=" + this.axonBankAccountId + "," + "amount=" + this.amount + ")";
  }
  
  public MyBankAccountDepositDto(String axonBankAccountId, long amount)
  {
    this.axonBankAccountId = axonBankAccountId;
    this.amount = amount;
  }
  
  public MyBankAccountDepositDto() {}
  
  public long getAmount()
  {
    return this.amount;
  }
  
  public String getAxonBankAccountId()
  {
    return this.axonBankAccountId;
  }
}
