package com.nebula.tinyasm.ana;

import static com.nebula.tinyasm.util.AsmBuilder.toCamelUpper;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SALOAD;
import static org.objectweb.asm.Opcodes.SASTORE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.samples.bank.api.banktransfer.BankTransferMarkCompletedCommand;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nebula.cqrs.axon.ClassLoaderDebuger;
import com.nebula.cqrs.axon.MyClassLoader;
import com.nebula.cqrs.axon.asm.AnalyzeEventsClassVisitor;
import com.nebula.cqrs.axon.asm.AnalyzeFieldClassVisitor;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.Variable;
import com.nebula.tinyasm.ana.Block.BlockType;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.api.ClassMethodCode;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AnalyzeMethodParamsClassVisitor;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.MethodInfo;

class ByteCodeAnaMethodVisitor extends MethodVisitor {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByteCodeAnaMethodVisitor.class);

	Map<String, Class<?>> definedTypes = new HashMap<>();

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

	int blockIndex = 0;

	Stack<Block> blockStack = new Stack<>();

	MyClassLoader classLoader111 = new ClassLoaderDebuger();
	// Block currentBlock;
	DomainDefinition domainDefinition;

	boolean done = false;

	int[] localsOfVar;

	int loop = 0;

	String methodName = null;
	Variable NA = new Variable("NA", Type.VOID_TYPE);
	ClassBody sagaClassBody;
	Field sagaIdField;
	String sagaName = null;

	ClassBody sagaObjectClassBody;

	Type sagaObjectType;

	Type sagaType;

	// ClassMethodCode sageStartEventHandle;
	Stack<Variable> stack = new Stack<>();
	// Block topBlock;
	List<Variable> variablesList;
	Type returnType;
	List<Field> datasFieldsWithID;
	List<Field> datasFields;

	public ByteCodeAnaMethodVisitor(DomainDefinition domainDefinition, MethodVisitor mv, MethodInfo methodInfo, int access, String name, String desc,
	        String signature) {
		super(ASM5, mv);
		this.methodName = name;
		this.variablesList = methodInfo.locals;
		this.localsOfVar = Types.computerLocalsVariable(this.variablesList);

		this.domainDefinition = domainDefinition;
		sagaObjectType = domainDefinition.typeOf(methodName);
		sagaType = domainDefinition.typeOf(methodName + "Saga");

		datasFields = new ArrayList<>();

		for (Field field : methodInfo.params) {
			if (field.type.getClassName().startsWith("java/") || field.type.getDescriptor().length() == 1) {
				datasFields.add(field);
			} else {
				datasFields.add(new Field(field.name + AsmBuilder.toCamelUpper(domainDefinition.identifierField.name), domainDefinition.identifierField.type));
				// TODO to deal other type
			}
		}

		sagaIdField = new Field(methodName + "Id", Type.getType(String.class));
		datasFieldsWithID = new ArrayList<>();
		datasFieldsWithID.add(sagaIdField);
		datasFieldsWithID.addAll(datasFields);

		// List<Field> realFields = new ArrayList<>();
		// realFields.addAll(datasFields);
		returnType = Type.getReturnType(desc);

		//
		// @StartSaga
		// @SagaEventHandler(associationProperty = "bankTransferId")
		// public void on(BankTransferCreatedEvent event) {
		// this.sourceBankAccountId = event.getSourceBankAccountId();
		// this.destinationBankAccountId = event.getDestinationBankAccountId();
		// this.amount = event.getAmount();
		//
		// BankTransferSourceDebitCommand command = new
		// BankTransferSourceDebitCommand(event.getSourceBankAccountId(),
		// event.getBankTransferId(),
		// event.getAmount());
		// commandBus.dispatch(asCommandMessage(command));
		// }

		// System.out.print("[] ");
		// System.out.println(" make " + createdEvent + " ");
		//
		// System.out.print("[] ");
		// System.out.println(sagaName + " -> on(" + createdEvent + ") {");
	}

	private void makeEndSaga(DomainDefinition domainDefinition, List<Field> datasFieldsWithID, String resultString, int resultValue) {
		Type commandType = domainDefinition.typeOf(this.methodName, "Mark", resultString, "Command");
		Type eventType = domainDefinition.typeOf(this.methodName, "Mark", resultString, "Event");

		byte[] commandCode = ClassBuilder.make(commandType).field(sagaIdField).readonlyPojo().toByteArray();
		defineClass(commandType.getClassName(), commandCode);

		byte[] eventCode = ClassBuilder.make(eventType).field(sagaIdField).readonlyPojo().toByteArray();
		defineClass(eventType.getClassName(), eventCode);

		sagaObjectClassBody.publicMethod("handle").annotation(CommandHandler.class).parameter("command", commandType).code(mc -> {
			mc.newInstace(eventType);
			mc.dup();
			mc.object("command").getProperty(sagaIdField);
			mc.type(eventType).invokeSpecial("<init>", sagaIdField);
			mc.type(AggregateLifecycle.class).invokeStatic("apply", eventType);
		});

		sagaObjectClassBody.publicMethod("on").annotation(EventHandler.class).parameter("event", eventType).code(mc -> {
			mc.loadThis();
			mc.insn(ICONST_0 + resultValue);
			mc.type(mc.thisType()).putTo("status", boolean.class);
		});
	}

	void blockCloseCurrent() {
		makeBlockEnd();

		Block previousBlock = blockStack.pop();
		pop(stack.size() - previousBlock.startStackIndex);

		LOGGER.debug("[]{}****}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()));

		switch (previousBlock.blockType) {
		case IFBLOCK:
			if (previousBlock.elseLabel != null) {

				blockStartOfElse(previousBlock.name + "Else", previousBlock.elseLabel, BlockType.ELSEBLOCK, previousBlock);
			} else {
				if (blockStack.size() > 0) {
					blockStartOfElse(previousBlock.name + "ElseVirtual", previousBlock.elseLabel, BlockType.VITUALBLOCK, previousBlock);
				}
			}
			break;
		case ELSEBLOCK:
			break;
		case VITUALBLOCK:
			if (blockStack.size() > 0) {
				blockCloseCurrent();
			}
			break;
		case METHODBLOCK:
		default:
			break;
		}
		printStack();
	}

	private void blockStartOfElse(String name, Label labelClose, Block.BlockType blockType, Block ifBlock) {
		LOGGER.debug("[]{}else{****", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()));
		Block parentBlock = blockStack.peek();

		Label thisLabelclose = labelClose;
		if (labelClose == null) {
			thisLabelclose = parentBlock.labelClose;
		}

		Block nextBlock = blockStack.push(new Block(name, thisLabelclose, blockType, ifBlock.startStackIndex));
		makeBlockBeginOfResult(parentBlock, nextBlock);
	}

	private void blockStartOfResult(String name, Label labelClose) {
		LOGGER.debug("[]{}if{****", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()));
		Block parentBlock = blockStack.peek();
		Label thisLabelclose = labelClose;
		if (labelClose == null) {
			thisLabelclose = parentBlock.labelClose;
		}

		Block nextBlock = blockStack.push(new Block(name, thisLabelclose, stack.size()));
		makeBlockBeginOfResult(parentBlock, nextBlock);
	}

	private void blockStartOfRoot(String name, List<Field> datasFields, Type createdEvent) {
		LOGGER.debug("[]{}root{****", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()));
		Block nextBlock = blockStack.push(new Block(name, null, stack.size()));
		makeBlockBeginOfRoot(nextBlock, datasFields, createdEvent);
	}

	private Block currentBlock() {
		return blockStack.peek();
	}

	private void makeBlockBeginOfResult(Block parentBlock, Block block) {
		String result = null;

		if (block.blockType == BlockType.IFBLOCK) {
			result = "Succeed";
		} else {
			result = "Fail";
		}

		final Type eventType = domainDefinition.typeOf(parentBlock.commandName, result, "Event");
		byte[] eventCode = ClassBuilder.make(eventType).fields(parentBlock.eventFields).readonlyPojo().toByteArray();
		defineClass(eventType.getClassName(), eventCode);
		makeBlockEnd();
		block.code = sagaClassBody.publicMethod("on").annotation(SagaEventHandler.class, "associationProperty", sagaIdField.name).parameter("event", eventType)
		        .begin();
	}

	private void makeBlockBeginOfRoot(Block block, List<Field> datasFields, Type createdEvent) {
		block.code = sagaClassBody.publicMethod("on").annotation(StartSaga.class).annotation(SagaEventHandler.class, "associationProperty", sagaIdField.name)
		        .parameter("event", createdEvent).begin().block(mc -> {
			        for (Field field : datasFields) {
				        mc.loadThis();
				        mc.object("event").getProperty(field);
				        mc.type(mc.thisType()).putTo(field);
			        }
		        });
	}

	@Override
	public void visitCode() {

		sagaObjectClassBody = ClassBuilder.make(sagaObjectType).field(AggregateIdentifier.class, sagaIdField).fields(datasFields).field("status", returnType);
		sagaClassBody = ClassBuilder.make(sagaType).field("commandBus", CommandBus.class).fields(datasFields);
		Type createdCommand = domainDefinition.typeOf(methodName + "CreateCommand");
		Type createdEvent = domainDefinition.typeOf(methodName + "CreatedEvent");
		{
			makeEvent(createdCommand, sagaIdField, datasFields);

			makeEvent(createdEvent, sagaIdField, datasFields);

			sagaObjectClassBody.publicMethod("<init>").code(mc -> {
				mc.initObject();
			});
			sagaObjectClassBody.publicMethod("<init>").annotation(CommandHandler.class).parameter("command", createdCommand).code(mc -> {
				mc.initObject();
				mc.newInstace(createdEvent);
				mc.dup();
				for (Field field : datasFieldsWithID) {
					mc.object("command").get(field);
				}
				mc.type(createdEvent).invokeSpecial("<init>", datasFieldsWithID);
				mc.type(AggregateLifecycle.class).invokeStatic("apply", createdEvent);

			});

			sagaObjectClassBody.publicMethod("on").annotation(EventHandler.class).parameter("event", createdEvent).code(mc -> {
				for (Field field : datasFieldsWithID) {
					mc.loadThis();
					mc.object("event").get(field);
					mc.type(mc.thisType()).putTo(field);
				}
			});
		}

		blockStartOfRoot("on" + AsmBuilder.toSimpleName(sagaObjectType.getClassName()) + blockIndex++, datasFields, createdEvent);

		if (Type.getType(boolean.class).getDescriptor().equals(returnType.getDescriptor())) {
			makeEndSaga(domainDefinition, datasFieldsWithID, "Completed", 1);
			makeEndSaga(domainDefinition, datasFieldsWithID, "Fail", 0);
		}
		super.visitCode();
	}

	private void makeFinished(int value) {

		Type commandType;

		if (value == 0) {
			commandType = domainDefinition.typeOf(this.methodName, "CompletedCommand");
		} else {
			commandType = domainDefinition.typeOf(this.methodName, "FailCommand");
		}

		currentBlock().code.block(mc -> {
			mc.def("command", commandType);

			mc.newInstace(commandType);
			mc.dup();
			{
				List<Field> params = new ArrayList<>();
				mc.object("event").getProperty(sagaIdField);
				params.add(sagaIdField);
				mc.type(commandType).invokeSpecial("<init>", params);
			}
			mc.storeTop("command");

			mc.loadThis().get("commandBus");
			mc.load("command");
			mc.type(GenericCommandMessage.class).invokeStatic(CommandMessage.class, "asCommandMessage", commandType);
			mc.type(CommandBus.class).invokeVirtual("dispatch", GenericCommandMessage.class);
		});
	}

	private void makeBlockEnd() {
		ClassMethodCode code = currentBlock().code;
		if (code != null) {
			code.returnVoid();
			code.end();
			currentBlock().code = null;
		}
	}

	void makeCommand(Type type, Field idField, List<Field> fields) {
		if (definedTypes.containsKey(type.getInternalName())) return;
		byte[] createdEventCode = ClassBuilder.make(type).field(idField).fields(fields).publicInitAllFields().defineAllPropetyGet()
		        .publicToStringWithAllFields().toByteArray();
		defineClass(type.getClassName(), createdEventCode);
	}

	public Class<?> defineClass(String name, byte[] binaryRepresentation) {
		try {
			if (definedTypes.containsKey(name)) return classLoader111.loadClass(name);
			Class<?> clz = classLoader111.define(name, binaryRepresentation);
			definedTypes.put(name, clz);
			return clz;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	void makeEvent(Type type, Field idField, List<Field> fields) {
		if (definedTypes.containsKey(type.getInternalName())) return;
		byte[] createdEventCode = ClassBuilder.make(type).field(TargetAggregateIdentifier.class, idField).fields(fields).publicInitAllFields()
		        .defineAllPropetyGet().publicToStringWithAllFields().toByteArray();
		defineClass(type.getClassName(), createdEventCode);
	}

	private void makeInvokeCommand(int opcode, String owner, String name, String desc, boolean itf) {
		Type[] types = Type.getArgumentTypes(desc);
		Type returnType = Type.getReturnType(desc);

		Field[] methodParams = new Field[types.length];

		int offset = stack.size();
		for (int i = types.length - 1; i >= 0; i--) {
			Type type = types[i];
			offset -= type.getSize();
			Field field = stack.elementAt(offset);
			methodParams[i] = field;
		}
		Type ownerType = Type.getObjectType(owner);
		offset -= ownerType.getSize();
		Field ownerField = stack.elementAt(offset);

		Type commandType = domainDefinition.typeOf(this.methodName, ownerField.name, name, "Command");

		{// make command
			byte[] code = ClassBuilder.make(commandType).field(TargetAggregateIdentifier.class, domainDefinition.identifierField).field(sagaIdField)
			        .fields(methodParams).readonlyPojo().toByteArray();
			defineClass(commandType.getClassName(), code);
		}
		if (Type.getType(boolean.class).getDescriptor().equals(returnType.getDescriptor())) {
			List<Field> eventFields = new ArrayList<>();

			eventFields.add(domainDefinition.identifierField);
			eventFields.add(sagaIdField);
			for (Field field : methodParams) {
				eventFields.add(field);
			}
			currentBlock().eventFields = eventFields;
			currentBlock().commandName = ownerField.name + toCamelUpper(name);
		}

		currentBlock().code.block(mc -> {
			mc.def("command", commandType);

			mc.newInstace(commandType);
			mc.dup();
			{
				List<Field> params = new ArrayList<>();
				mc.object("event").getProperty(ownerField.name + toCamelUpper(domainDefinition.identifierField.name), domainDefinition.identifierField.type);
				mc.object("event").getProperty(sagaIdField);
				params.add(domainDefinition.identifierField);
				params.add(sagaIdField);
				for (Field field : methodParams) {
					mc.object("event").getProperty(field);
					params.add(field);
				}
				mc.type(commandType).invokeSpecial("<init>", params);
			}
			mc.storeTop("command");

			mc.loadThis().get("commandBus");
			mc.load("command");
			mc.type(GenericCommandMessage.class).invokeStatic(CommandMessage.class, "asCommandMessage", commandType);
			mc.type(CommandBus.class).invokeVirtual("dispatch", GenericCommandMessage.class);
		});

		// System.out.println(methodParams);

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
		// StringBuffer sb = new StringBuffer();
		// sb.append("\t\t\t\t\t\t\t<<");
		// for (Variable var : stack) {
		// if (var == NA) {
		// sb.append("[],");
		// } else {
		// sb.append(var.name + "|" + var.type + ",");
		// }
		// }
		// sb.append(">>");
		// System.out.println(sb.toString());
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
		// LOGGER.debug("[]{}}", ByteCodeAnaClassVisitor.repeat(" ",
		// blockStack.size()));
		// LOGGER.debug("}");
		blockCloseCurrent();
		super.visitEnd();

		// System.out.println("[[[ " + loop + " ]]]");
		byte[] sageObjectCode = sagaObjectClassBody.end().toByteArray();
		defineClass(sagaObjectType.getClassName(), sageObjectCode);

		byte[] sageCode = sagaClassBody.end().toByteArray();
		defineClass(sagaType.getClassName(), sageCode);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "visitFieldInsn", name);

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
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "visitIincInsn", var);

		super.visitIincInsn(var, increment);
		printStack();
	}

	@Override
	public void visitInsn(int opcode) {
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "visitInsn", opcode);

		if (IRETURN <= opcode && opcode <= RETURN) {
			makeFinished((Integer) stack.peek().value);
			// currentBlock().code.block(mc->{
			// mc.def("i",int.class);
			// mc.load("i");
			// mc.load("i");
			// mc.insn(IADD);
			// mc.storeTop("i");
			// });
			LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "return", opcode);
		}

		int cnt = Types.SIZE[opcode];
		if (cnt > 0) {
			push(cnt);
		} else {
			pop(-cnt);
		}

		if (ICONST_0 <= opcode && opcode <= ICONST_5) {
			stack.peek().value = opcode - ICONST_0;
		}

		super.visitInsn(opcode);
		printStack();
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "visitIntInsn", operand);

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
		LOGGER.debug("[]{}jump", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()));
		if (opcode == GOTO) {
			currentBlock().elseLabel = label;

			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				push(cnt);
			} else {
				pop(-cnt);
			}

		} else {
			blockStartOfResult("inner" + blockIndex++, label);

			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				push(cnt);
			} else {
				pop(-cnt);
			}
			super.visitJumpInsn(opcode, label);
		}
		printStack();
	}

	@Override
	public void visitLabel(Label label) {
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "label", label);
		if (blockStack.size() > 0 && label == currentBlock().labelClose) {
			blockCloseCurrent();
		}
		super.visitLabel(label);
		printStack();
	}

	@Override
	public void visitLdcInsn(Object cst) {
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "visitLdcInsn", cst);

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
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "visitMethodInsn", name);
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
			makeInvokeCommand(opcode, owner, name, desc, itf);

			Type ownerType = Type.getObjectType(owner);

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
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "visitTypeInsn", type);
		if (opcode == NEW) {
			push("", Type.getObjectType(type));
		}
		super.visitTypeInsn(opcode, type);
		printStack();
	}

	@Override
	public void visitVarInsn(int opcode, int varLocal) {
		LOGGER.debug("[]{}{}\t\t\t{}", ByteCodeAnaClassVisitor.repeat("    ", blockStack.size()), "visitVarInsn", varLocal);
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