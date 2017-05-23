package com.nebula.tinyasm.builder;

import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.api.ClassBody;

public interface Context {
	ClassBody add(String name, ClassBody cb);

	ClassBody add(ClassBody cb);

	ClassBody get(String name);

	ClassBody get(Type type);

	DomainDefinition getDomainDefinition();
}