package com.nebula.cqrs.core.asm;

import java.util.Arrays;

public class Method {
	public String name;
	public Field[] params;

	public Method(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "Method [name=" + name + ", params=" + Arrays.toString(params) + "]\n";
	}
}