package com.nebula.cqrs.axon.pojo;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

public class Command {
	@Override
	public String toString() {
		return "Command [simpleClassName=" + simpleClassName + ", fields=" + fields + ", parameters=" + methodParams + "]";
	}

	public final String methodName;
	public final String actionName;
	public final String commandName;
	public final String simpleClassName;

	public Field[] methodParams;

	public boolean ctorMethod = false;

	public List<Field> fields = new ArrayList<>();

	public Command(String actionName, String methodName, String commandName, boolean ctorMethod, String simpleClassName, Type type, Type returnType) {
		super();
		this.actionName = actionName;
		this.methodName = methodName;
		this.commandName = commandName;
		this.ctorMethod = ctorMethod;
		this.simpleClassName = simpleClassName;
		this.type = type;
		this.returnType = returnType;
	}

	public Type type;
	public final Type returnType;

	public boolean withoutID = false;
}