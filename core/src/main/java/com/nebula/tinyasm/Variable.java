package com.nebula.tinyasm;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

public class Variable extends ClassField {
	Label startFrom;

	public Variable(ClassField field, Label startFrom) {
		super(field.access, field.name, field.type, field.signature);
		this.startFrom = startFrom;
	}

	public Variable(String name, Type type) {
		this(name, type, null);
	}

	public Variable(String name, Type type, String signature) {
		super(0, name, type, signature);
	}

	public Variable(String name, Type type, String signature, Label startFrom) {
		super(0, name, type, signature);
		this.startFrom = startFrom;
	}
	public Object value;
}