package com.nebula.cqrs.axon.pojo;

import java.util.Map;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.MethodInfo;

public class DomainDefinition {
	private final String defineName;
	public final Type implDomainType;
	private final Type defineType;
	private String packageName;

	public DomainDefinition(String simpleName, Type defineType) {
		super();
		this.defineName = simpleName;
		this.defineType = defineType;
		String className = defineType.getClassName();
		packageName = className.substring(0, className.lastIndexOf('.'));
		this.implDomainType = typeOf("Impl");
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

	public Type typeOf(String... names) {
		String retName = names[0];
		for (int i = 1; i < names.length; i++) {
			retName += toCamelUpper(names[i]);
		}
		String name = packageName + ".generatedsources." + defineName + "." + toCamelUpper(retName);
		return Type.getObjectType(name.replace('.', '/'));
	}

	public Type apitypeOf(String... names) {
		String retName = names[0];
		for (int i = 1; i < names.length; i++) {
			retName += toCamelUpper(names[i]);
		}
		String name = packageName + ".generatedsources." + defineName + ".api." + toCamelUpper(retName);
		return Type.getObjectType(name.replace('.', '/'));
	}

	public String fullnameOf(String name) {
		return packageName + ".generatedsources." + defineName + toCamelUpper(name);
	}

	public Field[] fields;
	public Field identifierField;
	public Map<String, Command> commands;
	public Map<String, Event> realEvents;
	public Map<String, Event> virtualEvents;
	public Map<String, MethodInfo> menthods;
}