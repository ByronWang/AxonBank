package com.nebula.dropwizard.core;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class TypeMaker extends ClassVisitor {
	class Field {
		public Field(String name, String type) {
			super();
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return "Field [name=" + name + ", type=" + type + "]";
		}

		String name;
		String type;
	}

	class Command {
		public Command(String name) {
			super();
			this.name = name;
		}

		@Override
		public String toString() {
			return "Command [name=" + name + ", data=" + data + "]";
		}

		String name;
		List<Field> data = new ArrayList<>();
	}

	class Event {
		String name;

		public Event(String name) {
			super();
			this.name = name;
		}

		@Override
		public String toString() {
			return "Event [name=" + name + ", data=" + data + "]";
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

	List<Command> commands = new ArrayList<>();
	List<Event> events = new ArrayList<>();
	Domain domain;

	@Override
	public String toString() {
		return "TypeMaker [commands=" + commands + ", events=" + events + ", domain=" + domain + "]";
	}

	public TypeMaker(int api, ClassVisitor cv) {
		super(api, cv);
	}

	public TypeMaker(int api) {
		super(api);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		domain = new Domain(name.substring(name.lastIndexOf('/') + 1));
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public void visitSource(String source, String debug) {
		// TODO Auto-generated method stub
		super.visitSource(source, debug);
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.visitOuterClass(owner, name, desc);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// TODO Auto-generated method stub
		return super.visitAnnotation(desc, visible);
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		// TODO Auto-generated method stub
		return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		// TODO Auto-generated method stub
		super.visitAttribute(attr);
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		// TODO Auto-generated method stub
		super.visitInnerClass(name, outerName, innerName, access);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		domain.data.add(new Field(name, desc));
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		super.visitMethod(access, name, desc, signature, exceptions);
		TypeMakerMethodVisitor methodVisitor = new TypeMakerMethodVisitor(api, access, name, desc, signature, exceptions);
		this.events.add(methodVisitor.event);
		this.commands.add(methodVisitor.command);
		return methodVisitor;
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		super.visitEnd();
	}

	class TypeMakerMethodVisitor extends MethodVisitor {
		final Command command;
		final Event event;
		List<Field> parametes = new ArrayList<>();
		List<Integer> stack = new ArrayList<>();

		int parameters = 0;

		public TypeMakerMethodVisitor(int api, int access, String name, String desc, String signature, String[] exceptions) {
			super(api);
			this.command = new Command(domain.name + "_" + name + "Command");
			this.event = new Event(domain.name + "_" + name + "Event");

			Type[] types = Type.getArgumentTypes(desc);
			// try {
			for (Type type : types) {
				Field argument = new Field(name, type.getClassName());
				parametes.add(argument);
				// if
				// (Class.forName("nebula.define.Primary").isAssignableFrom(Class.forName(type.getClassName())))
				// {
				command.data.add(argument);
				// }
			}
			// } catch (ClassNotFoundException e) {
			// e.printStackTrace();
			// }
		}

		@Override
		public void visitParameter(String name, int access) {
			super.visitParameter(name, access);
			parametes.get(parameters++).name = name;
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			// TODO Auto-generated method stub
			return super.visitAnnotationDefault();
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			// TODO Auto-generated method stub
			return super.visitAnnotation(desc, visible);
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// TODO Auto-generated method stub
			return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			// TODO Auto-generated method stub
			return super.visitParameterAnnotation(parameter, desc, visible);
		}

		@Override
		public void visitAttribute(Attribute attr) {
			// TODO Auto-generated method stub
			super.visitAttribute(attr);
		}

		@Override
		public void visitCode() {
			// TODO Auto-generated method stub
			super.visitCode();
		}

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
			// TODO Auto-generated method stub
			super.visitFrame(type, nLocal, local, nStack, stack);
		}

		@Override
		public void visitInsn(int opcode) {
			// TODO Auto-generated method stub
			super.visitInsn(opcode);
		}

		@Override
		public void visitIntInsn(int opcode, int operand) {
			// TODO Auto-generated method stub
			super.visitIntInsn(opcode, operand);
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			super.visitVarInsn(opcode, var);
			if (opcode == Opcodes.ALOAD) {
				stack.add(var);
			}
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			// TODO Auto-generated method stub
			super.visitTypeInsn(opcode, type);
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			// TODO Auto-generated method stub
			super.visitFieldInsn(opcode, owner, name, desc);
			if (opcode == Opcodes.PUTFIELD) {
				if (stack.get(stack.size() - 2) == 0) {
					Field field = new Field(name, desc);
					event.data.add(field);
				}
			}
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			// TODO Auto-generated method stub
			super.visitMethodInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			// TODO Auto-generated method stub
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}

		@Override
		public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
			// TODO Auto-generated method stub
			super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			// TODO Auto-generated method stub
			super.visitJumpInsn(opcode, label);
		}

		@Override
		public void visitLabel(Label label) {
			// TODO Auto-generated method stub
			super.visitLabel(label);
		}

		@Override
		public void visitLdcInsn(Object cst) {
			// TODO Auto-generated method stub
			super.visitLdcInsn(cst);
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			// TODO Auto-generated method stub
			super.visitIincInsn(var, increment);
		}

		@Override
		public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
			// TODO Auto-generated method stub
			super.visitTableSwitchInsn(min, max, dflt, labels);
		}

		@Override
		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
			// TODO Auto-generated method stub
			super.visitLookupSwitchInsn(dflt, keys, labels);
		}

		@Override
		public void visitMultiANewArrayInsn(String desc, int dims) {
			// TODO Auto-generated method stub
			super.visitMultiANewArrayInsn(desc, dims);
		}

		@Override
		public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// TODO Auto-generated method stub
			return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
		}

		@Override
		public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
			// TODO Auto-generated method stub
			super.visitTryCatchBlock(start, end, handler, type);
		}

		@Override
		public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// TODO Auto-generated method stub
			return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			// TODO Auto-generated method stub
			super.visitLocalVariable(name, desc, signature, start, end, index);
		}

		@Override
		public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc,
				boolean visible) {
			// TODO Auto-generated method stub
			return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
		}

		@Override
		public void visitLineNumber(int line, Label start) {
			// TODO Auto-generated method stub
			super.visitLineNumber(line, start);
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			// TODO Auto-generated method stub
			super.visitMaxs(maxStack, maxLocals);
		}

		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
			super.visitEnd();
		}

	}

}
