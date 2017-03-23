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
	Type typeDomain;

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
		typeDomain = Type.getObjectType(name);
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
			String methodName = name;
			boolean ctorMethod = false;
			String commandName;
			if ("<init>".equals(name)) {
				ctorMethod = true;
				commandName = domain.name + toCamelUpper("_Ctor");
			} else {
				ctorMethod = false;
				commandName = domain.name + toCamelUpper(methodName);
			}
			String simpleClassName = commandName + "Command";;

			Type type = typeOf(simpleClassName);

			Command command = new Command(methodName, commandName,ctorMethod, simpleClassName, type);

			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			CommandMethodVisitor commandMethodVisitor = new CommandMethodVisitor(api, methodVisitor, access, command, desc, signature, exceptions);

			this.events.add(commandMethodVisitor.event);
			this.commands.add(commandMethodVisitor.command);

			return commandMethodVisitor;
		} else if (name.startsWith("on")) {// Event
			String originMethodName = name;
			String newMethodName = "on";
			boolean innerEvent = true;
			String eventName = toCamelUpper(name.substring(2));
			String simpleClassName = domain.name + eventName + "Event";
			Type type = typeOf(simpleClassName);

			Event event = new Event(eventName, originMethodName, newMethodName, innerEvent, simpleClassName, type);

			String newDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { type });

			MethodVisitor methodVisitor = super.visitMethod(access, newMethodName, newDescriptor, signature, exceptions);
			EventMethodVisitor eventMethodVisitor = new EventMethodVisitor(api, methodVisitor, access, event, desc, signature, exceptions);

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
			if (event.methodParams.length == 0
					|| !(event.methodParams[0].name == fieldID.name && event.methodParams[0].type.getInternalName().equals(fieldID.type.getInternalName()))) {
				event.fields.add(fieldID);
				event.withoutID = true;
			}
			for (int i = 0; i < event.methodParams.length; i++) {
				event.fields.add(event.methodParams[i]);
			}
		}
		for (Event event : events) {
			if (event.innerEvent) {

			} else {
				if (event.superName != null) {
					for (Event eventSuper : events) {
						if (eventSuper.eventName.equals(event.superName)) {
							event.fields = eventSuper.fields;
							event.methodParams = eventSuper.methodParams;
						}
					}
				}
				makeApplyEventMethod(event);
			}
		}
		for (Command command : commands) {
			if (command.methodParams.length == 0 || !(command.methodParams[0].name == fieldID.name
					&& command.methodParams[0].type.getInternalName().equals(fieldID.type.getInternalName()))) {
				command.fields.add(fieldID);
				command.withoutID = true;
			}
			for (int i = 0; i < command.methodParams.length; i++) {
				command.fields.add(command.methodParams[i]);
			}
		}

		super.visitEnd();
	}

	public Type typeOf(String name) {
		return Type.getObjectType(fullnameOf(name).replace('.', '/'));
	}

	public String fullnameOf(String name) {
		// Call event
		String typename = typeDomain.getClassName();
		String packageName = typename.substring(0, typename.lastIndexOf('.'));
		return packageName + "." + name;
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

			mv = super.visitMethod(ACC_PRIVATE + ACC_FINAL, "apply" + event.simpleClassName, methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(51, l0);
			mv.visitTypeInsn(NEW, typeEvent.getInternalName());
			mv.visitInsn(DUP);

			if (event.withoutID) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, typeDomain.getInternalName(), fieldID.name, fieldID.type.getDescriptor());
			}

			for (int i = 0; i < event.methodParams.length; i++) {
				Field parameter = event.methodParams[i];
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
			mv.visitLocalVariable("this", typeDomain.getDescriptor(), null, l0, l2, 0);
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

	public static String toSimpleName(String name) {
		int index = name.lastIndexOf('.');
		if (index < 0) index = name.lastIndexOf('/');

		return name.substring(index + 1);
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
			Type type = typeOf(simpleClassName);
			this.event = new Event(eventName, originMethodName, newMethodName, innerEvent, simpleClassName, type);

			Method method = methods.get(command.methodName);
			command.methodParams = method.params;
		}

		// mv.visitInsn(ICONST_0);
		// mv.visitInsn(IRETURN);

		int lastOpcode = 0;

		@Override
		public void visitInsn(int opcode) {
			if (opcode == IRETURN && lastOpcode == ICONST_0) {

				String eventName = command.commandName + "Rejected";
				String originMethodName = null;
				String newMethodName = null;
				boolean innerEvent = false;
				String simpleClassName = eventName + "Event";
				Type typeEvent = typeOf(simpleClassName);
				Event eventRejected = new Event(eventName, originMethodName, newMethodName, innerEvent, simpleClassName, typeEvent);
				eventRejected.methodParams = new Field[0];
				CQRSBuilder.this.events.add(eventRejected);

				mv.visitVarInsn(ALOAD, 0);
				String desc = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {});
				super.visitMethodInsn(INVOKEVIRTUAL, typeDomain.getInternalName(), "apply" + toCamelUpper(eventRejected.simpleClassName), desc, false);
			}

			super.visitInsn(opcode);
			lastOpcode = opcode;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			Method method = methods.get(name);
			event.methodParams = method.params;

			if (owner.equals(typeDomain.getInternalName()) && name.startsWith("on")) {
				// set super name
				event.superName = domain.name + toCamelUpper(name.substring(2)) + "Event";

				super.visitMethodInsn(opcode, owner, "apply" + event.simpleClassName, desc, itf);

			} else {

				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}

	class EventMethodVisitor extends MethodVisitor {
		final Event event;
		List<Integer> stack = new ArrayList<>();

		int parameters = 0;

		public EventMethodVisitor(int api, MethodVisitor mv, int access, Event event, String desc, String signature, String[] exceptions) {
			super(api, mv);
			this.event = event;

			Method method = methods.get(event.originMethodName);
			event.methodParams = method.params;
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			if (0 < var && var <= event.methodParams.length) {
				super.visitVarInsn(ALOAD, 1);
				Field field = event.methodParams[var - 1];
				String descriptor = Type.getMethodDescriptor(field.type, new Type[] {});
				super.visitMethodInsn(INVOKEVIRTUAL, event.type.getInternalName(), "get" + toCamelUpper(field.name), descriptor, false);
			} else {
				super.visitVarInsn(opcode, var);
			}
		}

		boolean doneVisitLocalVariable = false;

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			if (index == 0) {
				super.visitLocalVariable(name, desc, signature, start, end, index);
			} else if (index <= event.methodParams.length) {
				event.methodParams[index - 1].name = name;
				if (!doneVisitLocalVariable) {
					String parameterName = CQRSBuilder.toCamelLower(event.eventName);
					super.visitLocalVariable(parameterName, event.type.getDescriptor(), signature, start, end, 1);
					doneVisitLocalVariable = true;
				}
			} else {
				super.visitLocalVariable(name, desc, signature, start, end, index - event.methodParams.length + 1);
			}
		}

		boolean doneVisitParameter = false;

		@Override
		public void visitParameter(String name, int access) {
			event.methodParams[parameters++].name = name;
			if (!doneVisitParameter) {
				String parameterName = CQRSBuilder.toCamelLower(event.eventName);
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
		@Override
		public String toString() {
			return "Command [simpleClassName=" + simpleClassName + ", fields=" + fields + ", parameters=" + methodParams + "]";
		}

		final String methodName;
		final String commandName;
		final String simpleClassName;

		Field[] methodParams;

		boolean ctorMethod = false;

		List<Field> fields = new ArrayList<>();

		public Command(String methodName, String commandName, boolean ctorMethod, String simpleClassName, Type type) {
			super();
			this.methodName = methodName;
			this.commandName = commandName;
			this.ctorMethod = ctorMethod;
			this.simpleClassName = simpleClassName;
			this.type = type;
		}

		Type type;

		boolean withoutID = false;
	}

	class Event {
		String superName;

		@Override
		public String toString() {
			return "Command [simpleClassName=" + simpleClassName + ", fields=" + fields + ", parameters=" + methodParams + "]";
		}

		String originMethodName;
		String newMethodName;
		String eventName;
		Field[] methodParams;

		boolean innerEvent = false;

		String simpleClassName;
		List<Field> fields = new ArrayList<>();

		public Event(String eventName, String originMethodName, String newMethodName, boolean innerEvent, String simpleClassName, Type type) {
			super();
			this.eventName = eventName;
			this.originMethodName = originMethodName;
			this.newMethodName = newMethodName;
			this.innerEvent = innerEvent;
			this.simpleClassName = simpleClassName;
			this.type = type;
		}

		Type type;

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
