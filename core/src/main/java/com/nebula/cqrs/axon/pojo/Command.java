package com.nebula.cqrs.axon.pojo;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public class Command {
	@Override
	public String toString() {
		return "Command [fields=" + fields + ", parameters=" + methodParams + "]";
	}

	public final String methodName;
	public final String actionName;
	public final String commandName;
	// private final String simpleClassName;

	public Field[] methodParams;

	public boolean ctorMethod = false;

	public Field[] fields;

	public Command(String actionName, String methodName, String commandName, boolean ctorMethod, Type type, Type returnType) {
		super();
		this.actionName = actionName;
		this.methodName = methodName;
		this.commandName = commandName;
		this.ctorMethod = ctorMethod;
		this.type = type;
		this.returnType = returnType;
	}

	public Type type;
	public final Type returnType;

	public boolean withoutID = false;
}