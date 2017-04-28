package com.nebula.cqrs.axon.pojo;

import org.objectweb.asm.Type;

public class Event {
	public Type realEvent;

	@Override
	public String toString() {
		return "Event [fields=" + fields + ", parameters=" + methodParams + "]";
	}

	public String originMethodName;
	public String newMethodName;
	public String eventName;
	public Field[] methodParams;

	public boolean innerEvent = false;

	// public String simpleClassName;
	public Field[] fields;

	public Event(String eventName, String originMethodName, String newMethodName, boolean innerEvent, Type type) {
		super();
		this.eventName = eventName;
		this.originMethodName = originMethodName;
		this.newMethodName = newMethodName;
		this.innerEvent = innerEvent;
		this.type = type;
	}

	public Type type;

	public boolean withoutID = false;
}