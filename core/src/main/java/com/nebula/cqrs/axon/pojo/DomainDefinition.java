package com.nebula.cqrs.axon.pojo;

import java.util.Map;

import org.objectweb.asm.Type;

public class DomainDefinition {
	public final String name;
	public final Type type;

	public DomainDefinition(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
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