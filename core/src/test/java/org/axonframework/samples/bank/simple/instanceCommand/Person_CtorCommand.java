package org.axonframework.samples.bank.simple.instanceCommand;

import java.beans.ConstructorProperties;

public abstract class Person_CtorCommand
{
  private final String id;
  private final String name;
  private final long age;
  
  @ConstructorProperties({"id", "name", "age"})
  public Person_CtorCommand(String id, String name, long age)
  {
    this.id = id;
    this.name = name;
    this.age = age;
  }
  
  public long getAge()
  {
    return this.age;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getId()
  {
    return this.id;
  }
}
