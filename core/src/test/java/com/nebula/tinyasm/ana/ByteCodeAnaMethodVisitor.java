package com.nebula.tinyasm.ana;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.SALOAD;
import static org.objectweb.asm.Opcodes.SASTORE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.MyClassLoader;
import com.nebula.cqrs.axon.asm.AnalyzeEventsClassVisitor;
import com.nebula.cqrs.axon.asm.AnalyzeFieldClassVisitor;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.Variable;
import com.nebula.tinyasm.ana.Block.BlockType;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AnalyzeMethodParamsClassVisitor;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.MethodInfo;

class ByteCodeAnaMethodVisitor extends MethodVisitor {
	MyClassLoader classLoader = new MyClassLoader();

	public static void main(String[] args) throws IOException {
		Type domainType = Type.getType(MyBankAccount.class);

		ClassReader cr = new ClassReader(MyBankAccount.class.getName());

		String srcDomainName = AsmBuilder.toSimpleName(domainType.getClassName());

		DomainDefinition domainDefinition = analyzeDomain(srcDomainName, domainType);

		AnalyzeMethodParamsClassVisitor analyzeMethodParamsClassVisitor = new AnalyzeMethodParamsClassVisitor();
		cr.accept(analyzeMethodParamsClassVisitor, ClassReader.SKIP_FRAMES);
		ByteCodeAnaClassVisitor anaClassVisitor = new ByteCodeAnaClassVisitor(domainDefinition);
		cr.accept(anaClassVisitor, ClassReader.EXPAND_FRAMES);
	}

	static private DomainDefinition analyzeDomain(String srcDomainName, Type srcDomainType) throws IOException {
		final DomainDefinition domainDefinition;
		domainDefinition = new DomainDefinition(srcDomainName, srcDomainType);
		ClassReader cr = new ClassReader(srcDomainType.getClassName());

		{
			AnalyzeMethodParamsClassVisitor analyzeMethodParamsClassVisitor = new AnalyzeMethodParamsClassVisitor();
			AnalyzeFieldClassVisitor analyzeFieldClassVisitor = new AnalyzeFieldClassVisitor(analyzeMethodParamsClassVisitor);
			cr.accept(analyzeFieldClassVisitor, 0);
			domainDefinition.menthods = analyzeMethodParamsClassVisitor.getMethods();
			domainDefinition.fields = analyzeFieldClassVisitor.finished().toArray(new Field[0]);
			for (Field field : domainDefinition.fields) {
				if (field.identifier) {
					domainDefinition.identifierField = field;
				}
			}
		}

		{
			AnalyzeEventsClassVisitor analyzeEventsClassVisitor = new AnalyzeEventsClassVisitor(domainDefinition);
			cr.accept(analyzeEventsClassVisitor, 0);
			domainDefinition.realEvents = analyzeEventsClassVisitor.finished();
		}

		return domainDefinition;
	}

	int blockIndex = 0;

	Stack<Block> blockStack = new Stack<>();
	Stack<Variable> stack = new Stack<>();

	int[] localsOfVar;
	Variable NA = new Variable("NA", Type.VOID_TYPE);

	String sagaName = null;

	String sagaObjectName = null;

	List<Variable> variablesList;

	Type sagaObjectType;
	Type sagaType;

	public ByteCodeAnaMethodVisitor(DomainDefinition domainDefinition, MethodVisitor mv, MethodInfo methodInfo, int access, String name, String desc,
	        String signature) {
		super(ASM5, mv);
		this.sagaName = name + "ManagementSaga";
		this.sagaObjectName = name;
		this.variablesList = methodInfo.locals;
		this.localsOfVar = Types.computerLocalsVariable(this.variablesList);

		// ClassBuilder.make(domainType)

		sagaObjectType = domainDefinition.typeOf(name);
		sagaType = domainDefinition.typeOf(name+"Saga");

		List<Field> datasFields = new ArrayList<>();

		for (Field field : methodInfo.params) {
			if (field.type.getClassName().startsWith("java/") || field.type.getDescriptor().length() == 1) {
				datasFields.add(field);
			} else {
				datasFields.add(new Field(field.name + AsmBuilder.toCamelUpper(domainDefinition.identifierField.name), domainDefinition.identifierField.type));
				// TODO to deal other type
			}
		}

		Field idField = new Field(name + "Id", Type.getType(String.class));
		List<Field> datasFieldsWithID = new ArrayList<>();
		datasFieldsWithID.add(idField);
		datasFieldsWithID.addAll(datasFields);

		// List<Field> realFields = new ArrayList<>();
		// realFields.addAll(datasFields);

		sagaObjectClassBody = ClassBuilder.make(sagaObjectType).field(AggregateIdentifier.class, idField).field(datasFields);
		sagaClassBody = ClassBuilder.make(sagaType).field("commandBus", CommandBus.class).field(datasFields);

		Type createdCommand = domainDefinition.typeOf(name + "CreateCommand");
		makeEvent(createdCommand, idField, datasFields);

		Type createdEvent = domainDefinition.typeOf(name + "CreatedEvent");
		makeEvent(createdEvent, idField, datasFields);

		sagaObjectClassBody.publicMethod("<init>").code(mc -> {
			mc.initObject();
		});
		sagaObjectClassBody.publicMethod("<init>").parameter("command", createdCommand).code(mc -> {
			mc.initObject();
			mc.newInstace(createdEvent);
			mc.dup();
			for (Field field : datasFieldsWithID) {
				mc.object("command").get(field);
			}
			mc.type(createdEvent).invokeSpecial("<init>", datasFieldsWithID);
			mc.type(AggregateLifecycle.class).invokeStatic("apply", createdEvent);

		});

		sagaObjectClassBody.publicMethod("on").parameter("event", createdEvent).code(mc -> {
			for (Field field : datasFieldsWithID) {
				mc.loadThis();
				mc.object("event").get(field);
				mc.type(mc.thisType()).putTo(field);
			}
		});

		blockStack.push(new Block("on" + AsmBuilder.toSimpleName(sagaObjectType.getClassName()) + blockIndex++, 0));

		System.out.print("[" + blockStack.peek().name + "] ");
		System.out.println(" make " + createdEvent + " ");

		System.out.print("[" + blockStack.peek().name + "] ");
		System.out.println(sagaName + " -> on(" + createdEvent + ") {");
	}

	ClassBody sagaObjectClassBody;
	ClassBody sagaClassBody;

	void makeEvent(Type type, Field idField, List<Field> fields) {
		byte[] createdEventCode = ClassBuilder.make(type).field(TargetAggregateIdentifier.class, idField).field(fields).publicInitAllFields()
		        .defineAllPropetyGet().publicToStringWithAllFields().toByteArray();
		classLoader.define(type.getClassName(), createdEventCode);
	}

	void makeCommand(Type type, Field idField, List<Field> fields) {
		byte[] createdEventCode = ClassBuilder.make(type).field(idField).field(fields).publicInitAllFields().defineAllPropetyGet().publicToStringWithAllFields()
		        .toByteArray();
		classLoader.define(type.getClassName(), createdEventCode);
	}

	void closeCurrent() {
		Block block = blockStack.pop();
		pop(stack.size() - block.startStackIndex);
		System.out.print("[" + block.name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("} ");

		switch (block.blockType) {
		case IFBLOCK:
			if (block.elseLabel != null) {
				System.out.print("[" + block.name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
				System.out.println(" else { ");
				block = blockStack.push(new Block(block.name, block.elseLabel, BlockType.ELSEBLOCK, block.startStackIndex));
			} else {
				if (blockStack.size() > 0) {
					System.out.print("[" + block.name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
					System.out.println(" else { ");
					block = blockStack.push(new Block(block.name, blockStack.peek().label, BlockType.VITUALBLOCK, block.startStackIndex));
				}
			}
			break;
		case ELSEBLOCK:
			break;
		case VITUALBLOCK:
			if (blockStack.size() > 0) {
				closeCurrent();
			}
			break;
		case METHODBLOCK:
		default:
			break;
		}
		printStack();
	}

	private Variable pop(int size) {
		Variable var = null;
		for (int i = 0; i < size; i++) {
			var = stack.pop();
		}
		return var;
	}

	private Variable pop(Type type) {
		return pop(type.getSize());
	}

	private void printStack() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t\t\t\t\t\t\t<<");
		for (Variable var : stack) {
			if (var == NA) {
				sb.append("[],");
			} else {
				sb.append(var.name + "|" + var.type + ",");
			}
		}
		sb.append(">>");
		System.out.println(sb.toString());
	}

	private void push(int size) {
		for (int i = 0; i < size; i++) {
			stack.push(NA);
		}
	}

	private void push(String name, Type type) {
		stack.push(new Variable(name, type));
		push(type.getSize() - 1);
	}

	@Override
	public void visitEnd() {
		closeCurrent();
		super.visitEnd();

		byte[] sageObjectCode = sagaObjectClassBody.end().toByteArray();
		classLoader.define(sagaObjectType.getClassName(), sageObjectCode);

		byte[] sageCode = sagaClassBody.end().toByteArray();
		classLoader.define(sagaType.getClassName(), sageCode);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("visitFieldInsn " + name + " ");

		if (opcode == GETSTATIC || opcode == GETFIELD) {
			push("", Type.getType(desc));
		} else if (opcode == PUTSTATIC || opcode == PUTFIELD) {
			pop(Type.getType(desc));
		}
		super.visitFieldInsn(opcode, owner, name, desc);
		printStack();
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("visitIincInsn " + var + " ");
		super.visitIincInsn(var, increment);
		printStack();
	}

	@Override
	public void visitInsn(int opcode) {
		System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("visitInsn");

		int cnt = Types.SIZE[opcode];
		if (cnt > 0) {
			push(cnt);
		} else {
			pop(-cnt);
		}
		super.visitInsn(opcode);
		printStack();
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("visitIntInsn");
		int cnt = Types.SIZE[opcode];
		if (cnt > 0) {
			push(cnt);
		} else {
			pop(-cnt);
		}
		super.visitIntInsn(opcode, operand);
		printStack();
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		int cnt = Types.SIZE[opcode];
		if (cnt > 0) {
			push(cnt);
		} else {
			pop(-cnt);
		}
		if (opcode == GOTO) {
			Block block = blockStack.peek();
			block.elseLabel = label;
		} else {
			System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
			System.out.println("if {" + label);
			blockStack.push(new Block("inner" + blockIndex++, label, stack.size()));
			super.visitJumpInsn(opcode, label);
		}
		printStack();
	}

	@Override
	public void visitLabel(Label label) {
		// System.out.println("label ");
		if (blockStack.size() > 0 && label == blockStack.peek().label) {
			closeCurrent();
		}
		super.visitLabel(label);
		printStack();
	}

	@Override
	public void visitLdcInsn(Object cst) {
		System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("visitLdcInsn " + cst + " ");

		push("", Type.getType(cst.getClass()));

		super.visitLdcInsn(cst);
		printStack();
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("visitMethodInsn " + name + " command");

		if (opcode == INVOKESTATIC) {
			Type[] params = Type.getArgumentTypes(desc);
			for (int i = params.length - 1; i >= 0; i--) {
				pop(params[i]);
			}
			Type returnType = Type.getReturnType(desc);
			if (returnType != Type.VOID_TYPE) {
				push("", returnType);
			}
		} else {
			Type ownerType = Type.getType("L" + owner + ";");

			Type[] params = Type.getArgumentTypes(desc);
			for (int i = params.length - 1; i >= 0; i--) {
				pop(params[i]);
			}
			Variable varOwner = pop(ownerType);
			Type returnType = Type.getReturnType(desc);
			if (returnType != Type.VOID_TYPE) {
				push(varOwner.name + "_" + name, returnType);
			}
		}
		super.visitMethodInsn(opcode, owner, name, desc, itf);
		printStack();
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("visitTypeInsn");
		if (opcode == NEW) {
			push("", Type.getType("L" + type + ";"));
		}
		super.visitTypeInsn(opcode, type);
		printStack();
	}

	@Override
	public void visitVarInsn(int opcode, int varLocal) {
		System.out.print("[" + blockStack.peek().name + "] " + ByteCodeAnaClassVisitor.repeat("\t", blockStack.size()));
		System.out.println("visitVarInsn " + varLocal + " ");
		if (ILOAD <= opcode && opcode <= SALOAD) {
			Variable var = variablesList.get(localsOfVar[varLocal]);
			push(var.name, var.type);
		}
		if (ISTORE <= opcode && opcode <= SASTORE) {
			Variable var = variablesList.get(localsOfVar[varLocal]);
			pop(var.type);
		}
		super.visitVarInsn(opcode, varLocal);
		printStack();
	}
}