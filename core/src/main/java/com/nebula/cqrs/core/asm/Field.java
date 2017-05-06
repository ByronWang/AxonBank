package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Type;

public class Field {
	public Field(String name, Type type) {
		this(name, type, null);
	}
	public Field(String name, Type type, String signature) {
		this.name = name;
		this.type = type;
		this.signature = signature;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + "]";
	}

	public String name;
	final public Type type;
	final String signature;
	public boolean identifier;
}