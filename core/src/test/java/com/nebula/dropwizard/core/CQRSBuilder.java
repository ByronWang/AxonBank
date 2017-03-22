package com.nebula.dropwizard.core;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Type;


public class CQRSBuilder extends ClassVisitor {
	Type type;

	List<Command> commands = new ArrayList<>();
	List<Event> events = new ArrayList<>();
	Domain domain;

	@Override
	public String toString() {
		return "TypeMaker [commands=" + commands + ", events=" + events + ", domain=" + domain + "]";
	}

	public CQRSBuilder(int api, ClassVisitor cv) {
		super(api, cv);
	}

	public CQRSBuilder(int api) {
		super(api);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		type = Type.getObjectType(name);
		domain = new Domain(name.substring(name.lastIndexOf('/') + 1));
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		domain.data.add(new Field(name, Type.getType(desc)));
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		if (is(access, ACC_STATIC)) { // SAGA
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return methodVisitor;
		} else if (is(access, ACC_PUBLIC)) {// Command
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			if ("<init>".equals(name)) {
				name = "Create";
			}
			CommandMethodVisitor commandMethodVisitor = new CommandMethodVisitor(api, methodVisitor, access, name, desc, signature, exceptions);
			this.events.add(commandMethodVisitor.event);
			this.commands.add(commandMethodVisitor.command);
			
			
			return commandMethodVisitor;
		} else if (name.startsWith("on")) {// Event
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			EventMethodVisitor eventMethodVisitor = new EventMethodVisitor(api, methodVisitor, access, name, desc, signature, exceptions);
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
			if (event.superName != null) {
				for (Event eventSuper : events) {
					if(eventSuper.name.equals(event.superName)){
						event.data = eventSuper.data;
					}
				}
				makeApplyEventMethod(event);
			}
		}
		
		
		super.visitEnd();
	}

	private void makeApplyEventMethod(Event event){
		MethodVisitor mv;
		{
			// Call event
			String typename = type.getInternalName();
			String packageName = typename.substring(0, typename.lastIndexOf('/'));
			String eventInternalName = packageName + "/" + event.name;
			
			List<Field> fields = event.data;
			
			Type[] params = new Type[fields.size()];

			for (int i = 0; i < fields.size(); i++) {
				params[i] = fields.get(i).type;
			}

			String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, params);		
			
			mv = super.visitMethod(ACC_PRIVATE, "apply" + toCamel(event.name), methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(51, l0);
			mv.visitTypeInsn(NEW, eventInternalName);
			mv.visitInsn(DUP);	
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitVarInsn(field.type.getOpcode(ILOAD), i+1);			
			}
			mv.visitMethodInsn(INVOKESPECIAL, eventInternalName, "<init>", methodDescriptor, false);
			mv.visitMethodInsn(INVOKESTATIC, "org/axonframework/commandhandling/model/AggregateLifecycle", "apply", "(Ljava/lang/Object;)Lorg/axonframework/commandhandling/model/ApplyMore;", false);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(52, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l2, 0);
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l2, i + 1);
			}
			mv.visitMaxs(5, 3);
			mv.visitEnd();
			}
	}

	static String toCamel(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	// class CommandMethodVisitor extends MethodVisitor {
	// final Command command;
	// final Event event;
	// List<Field> parametes = new ArrayList<>();
	// List<Integer> stack = new ArrayList<>();
	//
	// int parameters = 0;
	//
	// public CommandMethodVisitor(int api, MethodVisitor mv, int access, String
	// name, String desc, String signature, String[] exceptions) {
	// super(api, mv);
	// this.command = new Command(domain.name + toCamel(name) + "Command");
	// this.event = new Event(domain.name + toCamel(name) + "FinishedEvent");
	//
	// Type[] types = Type.getArgumentTypes(desc);
	// for (Type type : types) {
	// Field argument = new Field(null, type);
	// parametes.add(argument);
	// command.data.add(argument);
	// }
	// }
	//
	// public CommandMethodVisitor(int api, int access, String name, String
	// desc, String signature, String[] exceptions) {
	// this(api, null, access, name, desc, signature, exceptions);
	// }
	//
	// @Override
	// public void visitParameter(String name, int access) {
	// super.visitParameter(name, access);
	// parametes.get(parameters++).name = name;
	// }
	//
	// @Override
	// public void visitLocalVariable(String name, String desc, String
	// signature, Label start, Label end, int index) {
	// if(index > 0 && index <= parametes.size()){
	// parametes.get(index-1).name = name;
	// }
	// super.visitLocalVariable(name, desc, signature, start, end, index);
	// }
	//
	// @Override
	// public void visitMethodInsn(int opcode, String owner, String name, String
	// desc, boolean itf) {
	// if (owner.equals(typeDescriptor) && name.startsWith("on")) {
	// event.superName = domain.name + toCamel(name.substring(2)) + "Event";
	// }
	//
	// Type[] types = Type.getArgumentTypes(desc);
	// for (int i = 0; i < types.length; i++) {
	// Field argument = new Field("i" + i, types[i]);
	// event.data.add(argument);
	// }
	//
	// super.visitMethodInsn(opcode, owner, name, desc, itf);
	// }
	//
	// }
	class CommandMethodVisitor extends MethodVisitor {
		final Command command;
		final Event event;
		List<Field> parametes = new ArrayList<>();
		List<Integer> stack = new ArrayList<>();

		int parameters = 0;

		public CommandMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
			super(api, mv);
			this.command = new Command(domain.name + toCamel(name) + "Command");
			this.event = new Event(domain.name + toCamel(name) + "FinishedEvent");

			Type[] types = Type.getArgumentTypes(desc);

			for (Type type : types) {
				Field argument = new Field(null, type);
				parametes.add(argument);
				command.data.add(argument);
			}
		}

		public CommandMethodVisitor(int api, int access, String name, String desc, String signature, String[] exceptions) {
			this(api, null, access, name, desc, signature, exceptions);
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			if (index > 0 && index <= parametes.size()) {
				parametes.get(index - 1).name = name;
			}
			super.visitLocalVariable(name, desc, signature, start, end, index);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			Type[] types = Type.getArgumentTypes(desc);
			for (int i = 0; i < types.length; i++) {
				Field argument = new Field("i" + i, types[i]);
				event.data.add(argument);
			}

			if (owner.equals(type.getInternalName()) && name.startsWith("on")) {
				// set super name
				event.superName = domain.name + toCamel(name.substring(2)) + "Event";

				super.visitMethodInsn(opcode, owner, "apply" + toCamel(event.name), desc, itf);

			} else {

				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}

		}

		@Override
		public void visitParameter(String name, int access) {
			super.visitParameter(name, access);
			parametes.get(parameters++).name = name;
		}
		
		
	}

	
	
	class EventMethodVisitor extends MethodVisitor {
		final Event event;
		List<Field> parametes = new ArrayList<>();
		List<Integer> stack = new ArrayList<>();

		int parameters = 0;

		public EventMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
			super(api, mv);
			this.event = new Event(domain.name + toCamel(name.substring(2)) + "Event");

			Type[] types = Type.getArgumentTypes(desc);

			for (Type type : types) {
				Field argument = new Field(null, type);
				parametes.add(argument);
				event.data.add(argument);
			}
		}

		public EventMethodVisitor(int api, int access, String name, String desc, String signature, String[] exceptions) {
			this(api, null, access, name, desc, signature, exceptions);
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			if (index > 0 && index <= parametes.size()) {
				parametes.get(index - 1).name = name;
			}
			super.visitLocalVariable(name, desc, signature, start, end, index);
		}

		@Override
		public void visitParameter(String name, int access) {
			super.visitParameter(name, access);
			parametes.get(parameters++).name = name;
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}

	static boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	class Field {
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
			return "Command [name=" + name + ", data=" + data + "]\n";
		}

		String name;
		List<Field> data = new ArrayList<>();
	}

	class Event {
		String name;
		String superName;

		public Event(String name) {
			super();
			this.name = name;
		}

		@Override
		public String toString() {
			return "Event [name=" + name + ", superName=" + superName + ", data=" + data + "]\n";
		}

		List<Field> data = new ArrayList<>();
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
