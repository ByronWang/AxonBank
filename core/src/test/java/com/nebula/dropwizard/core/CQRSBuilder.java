package com.nebula.dropwizard.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Type;

import com.nebula.dropwizard.core.CQRSAnalyzerClassVisitor.Method;

public class CQRSBuilder extends ClassVisitor {
	Type type;

	List<Command> commands = new ArrayList<>();
	List<Event> events = new ArrayList<>();
	Domain domain;
	Field fieldID;
	Map<String, Method> methods;

	@Override
	public String toString() {
		return "TypeMaker [commands=" + commands + ", events=" + events + ", domain=" + domain + "]";
	}

	public CQRSBuilder(int api, ClassVisitor cv, Map<String, Method> methods) {
		super(api, cv);
		this.methods = methods;
	}

	public CQRSBuilder(int api, Map<String, Method> methods) {
		this(api, null, methods);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		type = Type.getObjectType(name);
		domain = new Domain(name.substring(name.lastIndexOf('/') + 1));
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		Field field = new Field(name, Type.getType(desc));
		if (domain.data.size() == 0) {
			this.fieldID = field;
		}
		domain.data.add(field);
		FieldVisitor fieldVisitor = super.visitField(access, name, desc, signature, value);
		return new CustomFieldVisitor(api, fieldVisitor, field, access, name, desc, signature, value);
	}

	class CustomFieldVisitor extends FieldVisitor {
		Field field;

		public CustomFieldVisitor(int api, FieldVisitor fv, Field field, int access, String name, String desc, String signature, Object value) {
			super(api, fv);
			this.field = field;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			Type type = Type.getType(desc);
			if (type.getInternalName() == Type.getType(AggregateIdentifier.class).getInternalName()) {
				CQRSBuilder.this.fieldID = this.field;
			}
			return super.visitAnnotation(desc, visible);
		}

	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		if (is(access, ACC_STATIC)) { // SAGA
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return methodVisitor;
		} else if (is(access, ACC_PUBLIC)) {// Command
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			String newName = name;
			if ("<init>".equals(name)) {
				newName = "Create";
			}
			CommandMethodVisitor commandMethodVisitor = new CommandMethodVisitor(api, methodVisitor, access, name, newName, desc, signature, exceptions);
			this.events.add(commandMethodVisitor.event);
			this.commands.add(commandMethodVisitor.command);

			return commandMethodVisitor;
		} else if (name.startsWith("on")) {// Event
			String eventName = domain.name + toCamel(name.substring(2)) + "Event";
			Type typeEvent = typeOf(eventName);
			String newDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { typeEvent });
			MethodVisitor methodVisitor = super.visitMethod(access, "on", newDescriptor, signature, exceptions);
			EventMethodVisitor eventMethodVisitor = new EventMethodVisitor(api, methodVisitor, access, name, eventName, typeEvent, desc, signature, exceptions);
			this.events.add(eventMethodVisitor.event);
			return eventMethodVisitor;
		} else {
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return methodVisitor;
		}

	}

	@Override
	public void visitEnd() {
		for (Event event : events) {
			if (event.params.length == 0
					|| !(event.params[0].name == fieldID.name && event.params[0].type.getInternalName().equals(fieldID.type.getInternalName()))) {
				event.fields.add(fieldID);
				event.withoutID = true;
			}
			for (int i = 0; i < event.params.length; i++) {
				event.fields.add(event.params[i]);
			}
		}
		for (Event event : events) {
			if (event.innerEvent) {

			} else {
				if (event.superName != null) {
					for (Event eventSuper : events) {
						if (eventSuper.name.equals(event.superName)) {
							event.fields = eventSuper.fields;
							event.params = eventSuper.params;
						}
					}
				}
				makeApplyEventMethod(event);
			}
		}
		for (Command command : commands) {
			if (command.params.length == 0
					|| !(command.params[0].name == fieldID.name && command.params[0].type.getInternalName().equals(fieldID.type.getInternalName()))) {
				command.fields.add(fieldID);
				command.withoutID = true;
			}
			for (int i = 0; i < command.params.length; i++) {
				command.fields.add(command.params[i]);
			}
		}

		super.visitEnd();
	}

	Type typeOf(String name) {
		// Call event
		String typename = type.getInternalName();
		String packageName = typename.substring(0, typename.lastIndexOf('/'));
		return Type.getObjectType(packageName + "/" + name);
	}

	private void makeApplyEventMethod(Event event) {
		MethodVisitor mv;
		{
			// Call event
			Type typeEvent = typeOf(event.name);

			// List<Field> parameters = event.params;

			Type[] params = new Type[event.params.length];
			for (int i = 0; i < event.params.length; i++) {
				params[i] = event.params[i].type;
			}
			final String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, params);

			mv = super.visitMethod(ACC_PRIVATE + ACC_FINAL, "apply" + toCamel(event.name), methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(51, l0);
			mv.visitTypeInsn(NEW, typeEvent.getInternalName());
			mv.visitInsn(DUP);

			if (event.withoutID) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, type.getInternalName(), fieldID.name, fieldID.type.getDescriptor());
			}

			for (int i = 0; i < event.params.length; i++) {
				Field parameter = event.params[i];
				mv.visitVarInsn(parameter.type.getOpcode(ILOAD), i + 1);
			}

			Type[] eventParams = new Type[event.fields.size()];
			for (int i = 0; i < event.fields.size(); i++) {
				eventParams[i] = event.fields.get(i).type;
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
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l2, 0);
			for (int i = 0; i < event.params.length; i++) {
				Field field = event.params[i];
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l2, i + 1);
			}
			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
	}

	static String toCamel(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	class CommandMethodVisitor extends MethodVisitor {
		final Command command;
		final Event event;
		final List<Event> otherEvents = new ArrayList<>();

		int parameters = 0;

		final String commandName;

		public CommandMethodVisitor(int api, MethodVisitor mv, int access, String name, String commandName, String desc, String signature, String[] exceptions) {
			super(api, mv);
			this.commandName = commandName;
			this.command = new Command(domain.name + toCamel(commandName) + "Command");
			this.event = new Event(domain.name + toCamel(commandName) + "FinishedEvent");

			Method method = methods.get(name);
			command.params = method.params;
		}

		public CommandMethodVisitor(int api, int access, String name, String newName, String desc, String signature, String[] exceptions) {
			this(api, null, access, name, newName, desc, signature, exceptions);
		}

		// mv.visitInsn(ICONST_0);
		// mv.visitInsn(IRETURN);

		int lastOpcode = 0;

		@Override
		public void visitInsn(int opcode) {
			if (opcode == IRETURN && lastOpcode == ICONST_0) {
				Event eventRejected = new Event(domain.name + toCamel(commandName) + "RejectedEvent");
				eventRejected.params = new Field[0];
				CQRSBuilder.this.events.add(eventRejected);

				mv.visitVarInsn(ALOAD, 0);
				String desc = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {});
				super.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), "apply" + toCamel(eventRejected.name), desc, false);
			}

			super.visitInsn(opcode);
			lastOpcode = opcode;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			Method method = methods.get(name);
			event.params = method.params;

			if (owner.equals(type.getInternalName()) && name.startsWith("on")) {
				// set super name
				event.superName = domain.name + toCamel(name.substring(2)) + "Event";

				super.visitMethodInsn(opcode, owner, "apply" + toCamel(event.name), desc, itf);

			} else {

				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}

	class EventMethodVisitor extends MethodVisitor {
		final Event event;
		List<Integer> stack = new ArrayList<>();

		int parameters = 0;

		public EventMethodVisitor(int api, MethodVisitor mv, int access, String name, String eventName, Type typeEvent, String oldDesc, String signature,
				String[] exceptions) {
			super(api, mv);
			this.event = new Event(eventName);
			this.event.type = typeEvent;
			this.event.innerEvent = true;

			Method method = methods.get(name);
			event.params = method.params;
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			if (0 < var && var <= event.params.length) {
				// super.visitVarInsn(opcode, var);
				super.visitVarInsn(ALOAD, 1);
				Field field = event.params[var - 1];
				String descriptor = Type.getMethodDescriptor(field.type, new Type[] {});
				super.visitMethodInsn(INVOKEVIRTUAL, event.type.getInternalName(), "get" + toCamel(field.name), descriptor, false);
			} else {
				super.visitVarInsn(opcode, var);
			}
		}

		boolean doneVisitLocalVariable = false;

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			if (index == 0) {
				super.visitLocalVariable(name, desc, signature, start, end, index);
			} else if (index <= event.params.length) {
				event.params[index - 1].name = name;
				if (!doneVisitLocalVariable) {
					String parameterName = Character.toLowerCase(event.name.charAt(0)) + event.name.substring(1);
					super.visitLocalVariable(parameterName, event.type.getDescriptor(), signature, start, end, 1);
					doneVisitLocalVariable = true;
				}
			} else {
				super.visitLocalVariable(name, desc, signature, start, end, index - event.params.length + 1);
			}
		}

		boolean doneVisitParameter = false;

		@Override
		public void visitParameter(String name, int access) {
			event.params[parameters++].name = name;
			if (!doneVisitParameter) {
				String parameterName = Character.toLowerCase(event.name.charAt(0)) + event.name.substring(1);
				super.visitParameter(parameterName, 0);
				doneVisitParameter = true;
			}
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}

	static boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	static class Field {
		public Field(String name, Type type) {
			super();
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return "Field [name=" + name + ", type=" + type + "]";
		}

		String name;
		Type type;
	}

	class Command {
		public Command(String name) {
			super();
			this.name = name;
		}

		@Override
		public String toString() {
			return "Command [name=" + name + ", fields=" + fields + ", parameters=" + params + "]";
		}

		String name;
		List<Field> fields = new ArrayList<>();
		Field[] params;
		boolean withoutID = false;
	}

	class Event {
		String name;
		String superName;
		Type type;
		boolean innerEvent = false;

		public Event(String name) {
			super();
			this.name = name;
		}

		@Override
		public String toString() {
			return "Event [name=" + name + ", superName=" + superName + ", type=" + type + ", innerEvent=" + innerEvent + ", params=" + params + "]";
		}

		List<Field> fields = new ArrayList<>();
		Field[] params;
		boolean withoutID = false;
	}

	class Domain {
		String name;

		public Domain(String name) {
			super();
			this.name = name;
		}

		List<Field> data = new ArrayList<>();
	}

}
