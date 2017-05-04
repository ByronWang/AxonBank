package org.axonframework.samples.bankcqrssrc.generatedsources;

import javax.persistence.EntityManager;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBankAccountAxonConfig
{
  @Autowired
  private AxonConfiguration axonConfiguration;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private EventBus eventBus;
  
  @Bean
  public MyBankAccountCommandHandler initMyBankAccountCommandHandler()
  {
    return new MyBankAccountCommandHandler(this.axonConfiguration.repository(MyBankAccountImpl.class), this.eventBus);
  }
}
