package com.nebula.cqrs.axon.asm;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.core.asm.Field;
import com.nebula.cqrs.core.asm.Method;

public class AnalyzeEventsClassVisitor extends ClassVisitor {
	private final DomainDefinition domainDefinition;

	private final Map<String, Event> realEvents = new HashMap<>();

	public AnalyzeEventsClassVisitor(ClassVisitor cv, DomainDefinition domainDefinition) {
		super(Opcodes.ASM5, cv);
		this.domainDefinition = domainDefinition;
	}

	public AnalyzeEventsClassVisitor(DomainDefinition domainDefinition) {
		super(Opcodes.ASM5);
		this.domainDefinition = domainDefinition;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (is(access, ACC_PRIVATE) && name.startsWith("on")) {// Event
			String originMethodName = name;
			String newMethodName = "on";
			boolean innerEvent = true;
			String eventName = toCamelUpper(name.substring(2));

			Type eventType = domainDefinition.typeOf(eventName + "Event");
			Event event = new Event(eventName, originMethodName, newMethodName, innerEvent, eventType);

			Method method = domainDefinition.menthods.get(originMethodName);
			event.methodParams = method.params;

			List<Field> fields = new ArrayList<>();
			if (event.methodParams.length == 0 || !equal(event.methodParams[0], domainDefinition.identifierField)) {
				fields.add(0, domainDefinition.identifierField);
				event.withoutID = true;
			}
			for (int i = 0; i < event.methodParams.length; i++) {
				fields.add(event.methodParams[i]);
			}
			event.fields = fields.toArray(new Field[0]);

			this.realEvents.put(eventName, event);
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	public Map<String, Event> finished() {
		return realEvents;
	}

	private boolean equal(Field field, Field identifierField) {
		return field.name == identifierField.name && field.type.getInternalName().equals(identifierField.type.getInternalName());
	}

	public static String toCamelUpper(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public static String toCamelLower(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	static boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

}
