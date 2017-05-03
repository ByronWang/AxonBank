package com.nebula.cqrs.axon.pojo;

import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.Field;

public class Event {
	private Event realEvent;

	public Event getRealEvent() {
		return realEvent;
	}
	public void setRealEvent(Event realEvent) {
		this.realEvent = realEvent;
		this.withoutID = realEvent.withoutID;
		this.methodParams = realEvent.methodParams;
		this.fields = realEvent.fields;
	}

	@Override
	public String toString() {
		return "Event [fields=" + fields + ", parameters=" + methodParams + "]";
	}

	public String originMethodName;
	public String newMethodName;
	
	public String eventName;
	public Type type;

	public boolean innerEvent = false;


	public boolean withoutID = false;
	public Field[] methodParams;
	public Field[] fields;

	public Event(String eventName, String originMethodName, String newMethodName, boolean innerEvent, Type type) {
		super();
		this.eventName = eventName;
		this.originMethodName = originMethodName;
		this.newMethodName = newMethodName;
		this.innerEvent = innerEvent;
		this.type = type;
	}

}