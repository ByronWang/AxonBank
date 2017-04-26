package com.nebula.cqrs.axon.pojo;

import java.util.Map;

import org.objectweb.asm.Type;

public class DomainDefinition {
	public final String name;
	public final Type type;
	public final Type srcDomainType;

	public DomainDefinition(String name, Type srcDomainType, Type type) {
		super();
		this.name = name;
		this.type = type;
		this.srcDomainType = srcDomainType;
	}

	public static String toCamelUpper(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public static String toCamelLower(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}
	
	public Field[] fields;
	public Command[] commands;
	public Event[] events;
	public Map<String, Method> menthods;
}