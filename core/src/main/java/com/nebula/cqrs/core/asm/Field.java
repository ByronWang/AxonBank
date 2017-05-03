package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Type;

public class Field {
	public Field(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + "]";
	}

	public String name;
	public Type type;
	public boolean identifier;
}