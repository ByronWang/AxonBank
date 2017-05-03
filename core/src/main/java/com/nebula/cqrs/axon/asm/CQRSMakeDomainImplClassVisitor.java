package com.nebula.cqrs.axon.asm;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.commandhandling.model.ApplyMore;
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

public class CQRSMakeDomainImplClassVisitor extends ClassVisitor {
	private final Type implDomainType;
	private final DomainDefinition domainDefinition;

	private final Map<String, Command> commands = new HashMap<>();
	private final Map<String, Event> virtualEvents = new HashMap<>();

	@Override
	public String toString() {
		return "TypeMaker [commands=" + commands + ", virtualEvents=" + virtualEvents + ", domain=" + domainDefinition + "]";
	}

	public CQRSMakeDomainImplClassVisitor(ClassVisitor cv, DomainDefinition domainDefinition) {
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

			commandName = AsmBuilder.toCamelUpper(actionName);

			Type type = domainDefinition.typeOf(commandName + "Command");

			Type returnType = Type.getReturnType(desc);

			Command command = new Command(actionName, methodName, commandName, ctorMethod, type, returnType);

			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			CommandMethodVisitor commandMethodVisitor = new CommandMethodVisitor(api, methodVisitor, access, command, desc, signature, exceptions);

			this.virtualEvents.put(commandMethodVisitor.succeedEvent.eventName, commandMethodVisitor.succeedEvent);
			this.commands.put(commandMethodVisitor.command.commandName, commandMethodVisitor.command);

			return commandMethodVisitor;
		} else if (name.startsWith("on")) {// Event
			String newMethodName = "on";
			String eventName = AsmBuilder.toCamelUpper(name.substring(2));

			Event event = domainDefinition.realEvents.get(eventName);
			ConvertFromParamsToClassMethodVisitor eventMethodVisitor = new ConvertFromParamsToClassMethodVisitor(cv, access, newMethodName, desc, signature,
					exceptions, event.type, event.methodParams);

			return eventMethodVisitor;
		} else {
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return methodVisitor;
		}

	}

	@Override
	public void visitEnd() {
		// TODO
		for (Event event : virtualEvents.values()) {
			makeApplyEventMethod(event);
		}
		for (Command command : commands.values()) {
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
		domainDefinition.virtualEvents = this.virtualEvents;

		AxonAsmBuilder.visitDefine_toString_withAllFields(cv, implDomainType, domainDefinition.fields);
		super.visitEnd();
	}

	private void makeApplyEventMethod(Event event) {
		MethodVisitor mv;
		{
			// Call event
			Type virtualEventType = event.type;

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
			{

				AxonAsmBuilder.visitNewObject(mv, virtualEventType);
				mv.visitInsn(DUP);

				if (event.withoutID) {
					AxonAsmBuilder.visitGetField(mv, 0, implDomainType, domainDefinition.identifierField);
				}

				for (int i = 0; i < event.methodParams.length; i++) {
					Field parameter = event.methodParams[i];
					mv.visitVarInsn(parameter.type.getOpcode(ILOAD), i + 1);
				}

				AxonAsmBuilder.visitInvokeSpecial(mv, virtualEventType, "<init>", event.fields);
				AsmBuilder.visitInvokeStatic(mv, AggregateLifecycle.class, ApplyMore.class, "apply", Object.class);

				mv.visitInsn(POP);

				mv.visitInsn(RETURN);
			}
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

	class CommandMethodVisitor extends MethodVisitor {
		final Command command;
		final Event succeedEvent;
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
			this.succeedEvent = new Event(eventName, originMethodName, newMethodName, innerEvent, type);

			Method method = domainDefinition.menthods.get(command.methodName);
			command.methodParams = method.params;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			Method method = domainDefinition.menthods.get(name);
			succeedEvent.methodParams = method.params;

			if (owner.equals(implDomainType.getInternalName()) && name.startsWith("on")) {
				String realEventName = AsmBuilder.toCamelUpper(name.substring(2));
				// set super name
				Event relEvent = domainDefinition.realEvents.get(realEventName);
				relEvent.ctorMethod = command.ctorMethod;// TODO
				succeedEvent.setRealEvent(relEvent);

				super.visitMethodInsn(opcode, owner, "apply" + succeedEvent.eventName, desc, itf);

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
