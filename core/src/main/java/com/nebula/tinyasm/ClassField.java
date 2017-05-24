package com.nebula.tinyasm;

import java.util.List;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public class ClassField extends Field {
	public ClassField(int access, String name, Type type, String signature, Object defaultValue) {
		super(name, type);
		this.signature = signature;
		this.access = access;
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + "]";
	}

	final int access;
	final String signature;
	public boolean identifier;
	Object defaultValue;

	static Type[] typesOf(List<ClassField> fields) {
		Type[] types = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			types[i] = fields.get(i).type;
		}
		return types;
	}
}