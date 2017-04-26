package com.nebula.cqrs.axon.asm;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.axon.pojo.Field;
import com.nebula.cqrs.axon.pojo.Method;
import com.nebula.cqrs.core.CqrsEntity;
import com.nebula.cqrs.core.asm.AsmBuilder;

public class CQRSDomainBuilder extends ClassVisitor {
	private final Type typeDomain;
	private final DomainDefinition domainDefinition;

	private final List<Command> commands = new ArrayList<>();
	private final List<Event> events = new ArrayList<>();
	private final List<Field> fields = new ArrayList<>();
	private Field newfieldID;

	public void setKeyField(Field field) {
		if (newfieldID != null && newfieldID != field) {
			newfieldID.idField = false;
		}

		field.idField = true;
		this.newfieldID = field;
	}

	@Override
	public String toString() {
		return "TypeMaker [commands=" + commands + ", events=" + events + ", domain=" + domainDefinition + "]";
	}

	public CQRSDomainBuilder(int api, ClassVisitor cv, DomainDefinition domainDefinition) {
		super(api, cv);
		this.domainDefinition = domainDefinition;
		this.typeDomain = domainDefinition.type;
	}

	public CQRSDomainBuilder(int api, DomainDefinition domainDefinition) {
		this(api, null, domainDefinition);
	}

	public DomainDefinition finished() {
		return domainDefinition;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		String cqrs = Type.getDescriptor(CqrsEntity.class);
		if(!cqrs.equals(desc)){
			return super.visitAnnotation(desc, visible);			
		}else{
			return null;
		}
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		Field field = new Field(name, Type.getType(desc));
		if (fields.size() == 0) {
			this.setKeyField(field);
		}
		fields.add(field);
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
				CQRSDomainBuilder.this.setKeyField(field);
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

			commandName = domainDefinition.name + toCamelUpper(actionName);
			String simpleClassName = commandName + "Command";;

			Type type = typeOf(simpleClassName);

			Type returnType = Type.getReturnType(desc);

			Command command = new Command(actionName, methodName, commandName, ctorMethod, simpleClassName, type, returnType);

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
			String simpleClassName = domainDefinition.name + eventName + "Event";
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
			List<Field> fields = new ArrayList<>();
			if (event.methodParams.length == 0 || !(event.methodParams[0].name == newfieldID.name
					&& event.methodParams[0].type.getInternalName().equals(newfieldID.type.getInternalName()))) {
				fields.add(newfieldID);
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
			if (command.methodParams.length == 0 || !(command.methodParams[0].name == newfieldID.name
					&& command.methodParams[0].type.getInternalName().equals(newfieldID.type.getInternalName()))) {
				fields.add(newfieldID);
				command.withoutID = true;
			}
			for (int i = 0; i < command.methodParams.length; i++) {
				fields.add(command.methodParams[i]);
			}
			if (fields.get(0).name == newfieldID.name && fields.get(0).type.getInternalName().equals(newfieldID.type.getInternalName())) {
				fields.get(0).idField = true;
			}
			command.fields = fields.toArray(new Field[0]);
		}

		domainDefinition.commands = this.commands.toArray(new Command[0]);
		domainDefinition.events = this.events.toArray(new Event[0]);
		domainDefinition.fields = this.fields.toArray(new Field[0]);

		AxonAsmBuilder.visitDefine_toString_withAllFields(cv, this.typeDomain, domainDefinition.fields);
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
				mv.visitFieldInsn(GETFIELD, typeDomain.getInternalName(), newfieldID.name, newfieldID.type.getDescriptor());
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

			Method method = domainDefinition.menthods.get(command.methodName);
			command.methodParams = method.params;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			Method method = domainDefinition.menthods.get(name);
			event.methodParams = method.params;

			if (owner.equals(typeDomain.getInternalName()) && name.startsWith("on")) {
				// set super name
				event.realEvent = typeOf(domainDefinition.name + toCamelUpper(name.substring(2)) + "Event");

				super.visitMethodInsn(opcode, owner, "apply" + event.simpleClassName, desc, itf);

				AsmBuilder.visitPrintObject(mv, 0);
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

			Method method = domainDefinition.menthods.get(event.originMethodName);
			event.methodParams = method.params;
			{
				AnnotationVisitor av0;
				av0 = visitAnnotation("Lorg/axonframework/eventhandling/EventHandler;", true);
				av0.visitEnd();
			}
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
					String parameterName = CQRSDomainBuilder.toCamelLower(event.eventName);
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
				String parameterName = CQRSDomainBuilder.toCamelLower(event.eventName);
				super.visitParameter(parameterName, 0);
				doneVisitParameter = true;
			}
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == Opcodes.RETURN) {
				AsmBuilder.visitPrintObject(mv, 0);
			}
			super.visitInsn(opcode);
		}
	}

	static boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

}
