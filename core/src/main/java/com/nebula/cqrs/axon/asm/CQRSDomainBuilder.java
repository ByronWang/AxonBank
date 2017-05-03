package com.nebula.cqrs.axon.asm;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.core.asm.AsmBuilder;
import com.nebula.cqrs.core.asm.ConvertFromParamsToClassMethodVisitor;
import com.nebula.cqrs.core.asm.Field;
import com.nebula.cqrs.core.asm.Method;

public class CQRSDomainBuilder extends ClassVisitor {
	private final Type implDomainType;
	private final DomainDefinition domainDefinition;

	private final List<Command> commands = new ArrayList<>();
	private final List<Event> events = new ArrayList<>();

	@Override
	public String toString() {
		return "TypeMaker [commands=" + commands + ", events=" + events + ", domain=" + domainDefinition + "]";
	}

	public CQRSDomainBuilder(ClassVisitor cv, DomainDefinition domainDefinition) {
		super(Opcodes.ASM5, cv);
		this.domainDefinition = domainDefinition;
		this.implDomainType = domainDefinition.implDomainType;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		if (is(access, ACC_STATIC)) { // SAGA
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return methodVisitor;
		} else if (is(access, ACC_PUBLIC)) {// Command
			String methodName = name;
			String actionName;
			boolean ctorMethod;
			String commandName;
			if ("<init>".equals(name)) {
				ctorMethod = true;
				actionName = "create";
			} else {
				ctorMethod = false;
				actionName = methodName;
			}

			commandName = toCamelUpper(actionName);

			Type type = domainDefinition.typeOf(commandName + "Command");

			Type returnType = Type.getReturnType(desc);

			Command command = new Command(actionName, methodName, commandName, ctorMethod, type, returnType);

			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			CommandMethodVisitor commandMethodVisitor = new CommandMethodVisitor(api, methodVisitor, access, command, desc, signature, exceptions);

			this.events.add(commandMethodVisitor.event);
			this.commands.add(commandMethodVisitor.command);

			return commandMethodVisitor;
		} else if (name.startsWith("on")) {// Event
			String originMethodName = name;
			Method method = domainDefinition.menthods.get(originMethodName);

			String newMethodName = "on";
			boolean innerEvent = true;
			String eventName = toCamelUpper(name.substring(2));

			Type eventType = domainDefinition.typeOf(eventName + "Event");
			Event event = new Event(eventName, originMethodName, newMethodName, innerEvent, eventType);

			Field[] params = method.params;
			event.methodParams = method.params;

			ConvertFromParamsToClassMethodVisitor eventMethodVisitor = new ConvertFromParamsToClassMethodVisitor(cv, access, newMethodName, desc, signature,
					exceptions, eventType, params);

			this.events.add(event);
			return eventMethodVisitor;
		} else {
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return methodVisitor;
		}

	}

	@Override
	public void visitEnd() {
		for (Event event : events) {
			List<Field> fields = new ArrayList<>();
			if (event.methodParams.length == 0 || !(event.methodParams[0].name == domainDefinition.identifierField.name
					&& event.methodParams[0].type.getInternalName().equals(domainDefinition.identifierField.type.getInternalName()))) {
				fields.add(domainDefinition.identifierField);
				event.withoutID = true;
			}
			for (int i = 0; i < event.methodParams.length; i++) {
				fields.add(event.methodParams[i]);
			}
			event.fields = fields.toArray(new Field[0]);
		}
		for (Event event : events) {
			if (event.innerEvent) {

			} else {
				if (event.realEvent != null) {
					for (Event eventSuper : events) {
						if (eventSuper.type.getClassName().equals(event.type.getClassName())) {
							event.fields = eventSuper.fields;
							event.methodParams = eventSuper.methodParams;
						}
					}
				}
				makeApplyEventMethod(event);
			}
		}
		for (Command command : commands) {
			List<Field> fields = new ArrayList<>();
			if (command.methodParams.length == 0 || !(command.methodParams[0].name == domainDefinition.identifierField.name
					&& command.methodParams[0].type.getInternalName().equals(domainDefinition.identifierField.type.getInternalName()))) {
				fields.add(domainDefinition.identifierField);
				command.withoutID = true;
			}
			for (int i = 0; i < command.methodParams.length; i++) {
				fields.add(command.methodParams[i]);
			}
			if (fields.get(0).name == domainDefinition.identifierField.name
					&& fields.get(0).type.getInternalName().equals(domainDefinition.identifierField.type.getInternalName())) {
				fields.get(0).identifier = true;
			}
			command.fields = fields.toArray(new Field[0]);
		}

		domainDefinition.commands = this.commands;
		domainDefinition.events = this.events;

		AxonAsmBuilder.visitDefine_toString_withAllFields(cv, implDomainType, domainDefinition.fields);
		super.visitEnd();
	}

	private void makeApplyEventMethod(Event event) {
		MethodVisitor mv;
		{
			// Call event
			Type typeEvent = event.type;

			// List<Field> parameters = event.params;

			Type[] params = new Type[event.methodParams.length];
			for (int i = 0; i < event.methodParams.length; i++) {
				params[i] = event.methodParams[i].type;
			}
			final String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, params);

			mv = super.visitMethod(ACC_PRIVATE + ACC_FINAL, "apply" + event.eventName, methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(51, l0);
			mv.visitTypeInsn(NEW, typeEvent.getInternalName());
			mv.visitInsn(DUP);

			if (event.withoutID) {
				AxonAsmBuilder.visitGetField(mv, 0, implDomainType, domainDefinition.identifierField);
			}

			for (int i = 0; i < event.methodParams.length; i++) {
				Field parameter = event.methodParams[i];
				mv.visitVarInsn(parameter.type.getOpcode(ILOAD), i + 1);
			}

			Type[] eventParams = new Type[event.fields.length];
			for (int i = 0; i < event.fields.length; i++) {
				eventParams[i] = event.fields[i].type;
			}
			String eventMethodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, eventParams);

			mv.visitMethodInsn(INVOKESPECIAL, typeEvent.getInternalName(), "<init>", eventMethodDescriptor, false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply",
					"(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(52, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", implDomainType.getDescriptor(), null, l0, l2, 0);
			for (int i = 0; i < event.methodParams.length; i++) {
				Field field = event.methodParams[i];
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l2, i + 1);
			}
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
	}

	public static String toCamelUpper(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public static String toCamelLower(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	class CommandMethodVisitor extends MethodVisitor {
		final Command command;
		final Event event;
		final List<Event> otherEvents = new ArrayList<>();

		int parameters = 0;

		public CommandMethodVisitor(int api, MethodVisitor mv, int access, Command command, String desc, String signature, String[] exceptions) {
			super(api, mv);
			this.command = command;

			String eventName = command.commandName + "Finished";
			String originMethodName = null;
			String newMethodName = null;
			boolean innerEvent = false;
			String simpleClassName = eventName + "Event";
			Type type = domainDefinition.typeOf(simpleClassName);
			this.event = new Event(eventName, originMethodName, newMethodName, innerEvent, type);

			Method method = domainDefinition.menthods.get(command.methodName);
			command.methodParams = method.params;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			Method method = domainDefinition.menthods.get(name);
			event.methodParams = method.params;

			if (owner.equals(implDomainType.getInternalName()) && name.startsWith("on")) {
				// set super name
				event.realEvent = domainDefinition.typeOf(toCamelUpper(name.substring(2)) + "Event");

				super.visitMethodInsn(opcode, owner, "apply" + event.eventName, desc, itf);

				AsmBuilder.visitPrintObject(mv, 0);
			} else {

				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}

	static boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

}
