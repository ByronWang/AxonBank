package org.axonframework.samples.bankcqrssrc.generatedsources;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public abstract interface MyBankAccountRepository
  extends CrudRepository<MyBankAccountEntry, String>
{
  public abstract Iterable<MyBankAccountEntry> findAllByOrderByIdAsc();
  
  public abstract MyBankAccountEntry findOneByAxonBankAccountId(String paramString);
}
