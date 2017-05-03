package com.nebula.cqrs.axon.pojo;

import java.util.Map;

import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.Field;
import com.nebula.cqrs.core.asm.Method;

public class DomainDefinition {
	private final String defineName;
	public final Type implDomainType;
	private final Type defineType;
	private String packageName;
	

	public DomainDefinition(String simpleName, Type defineType, Type implDomainType) {
		super();
		this.defineName = simpleName;
		this.implDomainType = implDomainType;
		this.defineType = defineType;
		String className = defineType.getClassName();
		packageName = className.substring(0, className.lastIndexOf('.'));
	}

	public static String toCamelUpper(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public static String toCamelLower(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	public String nameFor(String item, String name) {
		return defineName + name + item;
	}

	public String classnameFor(String item, String name) {
		return this.defineType.getClassName() + name + item;
	}

	public String classnameFor(String item, String name1, String name2) {
		return this.defineType.getClassName() + name1 + name2 + item;
	}

	public Type typeOf(String name) {
		return Type.getObjectType(fullnameOf(name).replace('.', '/'));
	}

	public String fullnameOf(String name) {
		return packageName + "." + defineName + name;
	}

	public Field[] fields;
	public Field identifierField;
	public Command[] commands;
	public Event[] events;
	public Map<String, Method> menthods;
}