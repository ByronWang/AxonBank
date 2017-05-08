package com.nebula.cqrs.core.asm.wrap;

import java.util.List;

import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.Field;

public class ClassField extends Field {
	public ClassField(String name, Type type) {
		this(name, type, null);
	}

	public ClassField(String name, Type type, String signature) {
		super(name, type);
		this.signature = signature;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + "]";
	}

	final String signature;
	public boolean identifier;

	static Type[] typesOf(List<ClassField> fields) {
		Type[] types = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			types[i] = fields.get(i).type;
		}
		return types;
	}
}