package com.nebula.cqrs.axon.builder;

import static com.nebula.tinyasm.util.AsmBuilder.toCamelUpper;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_TRANSIENT;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_5;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.builder.SagaBlock.BlockType;
import com.nebula.cqrs.axon.builder.SagaClassListener.Status;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.StatusBuilder;
import com.nebula.tinyasm.Variable;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.api.ClassMethodCode;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.MethodInfo;

class SagaMethodVisitor extends SagaMethodAnalyzer {

	ClassBody commandHandlerBody;

	Type commandHandlerType;

	final DomainContext context;
	// Block currentBlock;
	DomainDefinition domainDefinition;

	Field idField;
	int methodBlockIndex = 0;

	Stack<SagaBlock> methodBlockStack = new Stack<>();
	int[] methodLocalsOfVar;

	String methodName = null;
	Type methodReturnType;

	Stack<Variable> methodStack = new Stack<>();
	List<Variable> methodVariablesList;

	List<Field> sagaFields;
	List<Field> sagaFieldsWithID;

	ClassBody sagaManagementClassBody;
	String sagaName = null;

	ClassBody sagaObjectBody;

	Type sagaObjectType;

	Type sagaType;

	Type statusType;

	public SagaMethodVisitor(DomainContext context, MethodVisitor mv, MethodInfo methodInfo, int access, String name, String desc, String signature) {
		super(mv);
		this.context = context;
		this.methodName = name;
		this.methodVariablesList = methodInfo.locals;
		this.methodLocalsOfVar = Types.computerLocalsVariable(this.methodVariablesList);

		this.domainDefinition = context.getDomainDefinition();
		sagaObjectType = domainDefinition.typeOf(methodName);
		sagaType = domainDefinition.typeOf(methodName, "ManagementSaga");
		commandHandlerType = domainDefinition.topLeveltypeOf("CommandHandler");
		sagaFields = new ArrayList<>();

		for (Field field : methodInfo.params) {
			if (field.type.getClassName().startsWith("java/") || field.type.getDescriptor().length() == 1) {
				sagaFields.add(field);
			} else {
				sagaFields
				        .add(new Field(field.name + AsmBuilder.toCamelUpper(domainDefinition.identifierField.name), domainDefinition.identifierField.type));
				// TODO to deal other type
			}
		}

		idField = new Field(methodName + "Id", Type.getType(String.class));
		sagaFieldsWithID = new ArrayList<>();
		sagaFieldsWithID.add(idField);
		sagaFieldsWithID.addAll(sagaFields);

		// List<Field> realFields = new ArrayList<>();
		// realFields.addAll(datasFields);
		methodReturnType = Type.getReturnType(desc);

		//
		// @StartSaga
		// @SagaEventHandler(associationProperty = "bankTransferId")
		// public void on(BankTransferCreatedEvent event) {
		// this.sourceBankAccountId = event.getSourceBankAccountId();
		// this.destinationBankAccountId =
		// event.getDestinationBankAccountId();
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

	void blockCloseCurrent() {
		makeBlockEnd();

		SagaBlock previousBlock = methodBlockStack.pop();
		stackPop(methodStack.size() - previousBlock.startStackIndex);

		SagaClassListener.LOGGER.debug("[]{}****}", SagaClassListener.repeat("    ", methodBlockStack.size()));

		switch (previousBlock.blockType) {
		case IFBLOCK:
			if (previousBlock.elseLabel != null) {

				blockStartOfElse(previousBlock.name + "Else", previousBlock.elseLabel, BlockType.ELSEBLOCK, previousBlock);
			} else {
				if (methodBlockStack.size() > 0) {
					blockStartOfElse(previousBlock.name + "ElseVirtual", previousBlock.elseLabel, BlockType.VITUALBLOCK, previousBlock);
				}
			}
			break;
		case ELSEBLOCK:
			break;
		case VITUALBLOCK:
			if (methodBlockStack.size() > 0) {
				blockCloseCurrent();
			}
			break;
		case METHODBLOCK:
		default:
			break;
		}
		printStack();
	}

	private SagaBlock blockCurrent() {
		return methodBlockStack.peek();
	}

	private void blockStartOfElse(String name, Label labelClose, SagaBlock.BlockType blockType, SagaBlock ifBlock) {
		SagaClassListener.LOGGER.debug("[]{}else{****", SagaClassListener.repeat("    ", methodBlockStack.size()));
		SagaBlock parentBlock = methodBlockStack.peek();

		Label thisLabelclose = labelClose;
		if (labelClose == null) {
			thisLabelclose = parentBlock.labelClose;
		}

		SagaBlock nextBlock = methodBlockStack.push(new SagaBlock(name, thisLabelclose, blockType, ifBlock.startStackIndex));
		makeBlockBeginOfResult(parentBlock, nextBlock);
	}

	private void blockStartOfResult(String name, Label labelClose) {
		SagaClassListener.LOGGER.debug("[]{}if{****", SagaClassListener.repeat("    ", methodBlockStack.size()));
		SagaBlock parentBlock = methodBlockStack.peek();
		Label thisLabelclose = labelClose;
		if (labelClose == null) {
			thisLabelclose = parentBlock.labelClose;
		}

		SagaBlock nextBlock = methodBlockStack.push(new SagaBlock(name, thisLabelclose, methodStack.size()));
		makeBlockBeginOfResult(parentBlock, nextBlock);
	}

	private void blockStartOfRoot(String name, List<Field> datasFields, Type createdEvent) {
		SagaClassListener.LOGGER.debug("[]{}root{****", SagaClassListener.repeat("    ", methodBlockStack.size()));
		SagaBlock nextBlock = methodBlockStack.push(new SagaBlock(name, null, methodStack.size()));
		makeBlockBeginOfRoot(nextBlock, datasFields, createdEvent);
	}

	private void makeBlockBeginOfResult(SagaBlock parentBlock, SagaBlock block) {
		String result = null;

		if (block.blockType == BlockType.IFBLOCK) {
			result = Status.Completed.name();
		} else {
			result = Status.Failed.name();
		}

		final Type eventType = domainDefinition.apitypeOf(parentBlock.commandName, result, "Event");
		context.add(ClassBuilder.make(eventType).fields(parentBlock.eventFields).readonlyPojo());
		makeBlockEnd();
		block.code = sagaManagementClassBody.publicMethod("on").annotation(SagaEventHandler.class, "associationProperty", idField.name)
		        .parameter("event", eventType).begin();
	}

	private void makeBlockBeginOfRoot(SagaBlock block, List<Field> datasFields, Type createdEvent) {
		block.code = sagaManagementClassBody.publicMethod("on").annotation(StartSaga.class)
		        .annotation(SagaEventHandler.class, "associationProperty", idField.name).parameter("event", createdEvent).begin().block(mc -> {
			        for (Field field : datasFields) {
				        mc.loadThis();
				        mc.object("event").getProperty(field);
				        mc.type(mc.thisType()).putTo(field);
			        }
		        });
	}

	private void makeBlockEnd() {
		ClassMethodCode code = blockCurrent().code;
		if (code != null) {
			code.returnVoid();
			code.end();
			blockCurrent().code = null;
		}
	}

	private void makeCommandHandler() {
		// commandHandlerBody =
		// ClassBuilder.make(commandHandlerType).field("repository",
		// Repository.class,
		// domainDefinition.implDomainType).field("eventBus",
		// EventBus.class);
		// context.add("commandHandler", commandHandlerBody);
		// commandHandlerBody.publicMethod("<init>").parameter("repository",
		// Repository.class, domainDefinition.implDomainType)
		// .parameter("eventBus", EventBus.class).code(mb -> {
		// mb.line(15).initObject();
		// mb.loadThis().put("repository", "repository");
		// mb.loadThis().put("eventBus", "eventBus");
		// mb.returnVoid();
		// });
		//
		commandHandlerBody = context.get("commandHandler");
	}

	private void makeFinished(int value) {

		Type commandType;

		if (value == 1) {
			commandType = domainDefinition.apitypeOf(this.methodName, "Mark", Status.Completed.name(), "Command");
		} else {
			commandType = domainDefinition.apitypeOf(this.methodName, "Mark", Status.Failed.name(), "Command");
		}

		blockCurrent().code.block(mc -> {
			mc.loadThis().get("commandBus");

			mc.newInstace(commandType);
			mc.dup();
			{
				List<Field> paramTypes = new ArrayList<>();
				mc.object("event").getProperty(idField);
				paramTypes.add(idField);
				mc.type(commandType).invokeSpecial("<init>", paramTypes);
			}

			mc.type(GenericCommandMessage.class).invokeStatic(CommandMessage.class, "asCommandMessage", commandType);
			mc.type(CommandBus.class).invokeVirtual("dispatch", GenericCommandMessage.class);
		});
	}

	// private static void visitDefine_handle_execute(ClassBody cb, Type
	// handleType, Type callerType, Type commandType, Type domainType, Field
	// identifierField) {
	// cb.publicMethod("handle",
	// Exception.class).annotation(CommandHandler.class).parameter("command",
	// commandType).code(mc -> {
	// mc.def("aggregate", Aggregate.class, domainType);
	// mc.line(27).loadThis().get("repository");
	// mc.object("command").getProperty(identifierField);
	// mc.type(Repository.class).invokeInterface(Aggregate.class, "load",
	// identifierField.type).store("aggregate");
	// mc.use("aggregate").with(m -> {
	// mc.newInstace(callerType);
	// mc.dup();
	// mc.load("this");
	// mc.load("command");
	// mc.type(callerType).invokeSpecial("<init>", handleType, commandType);
	// }).invokeInterface("execute", Consumer.class);
	//
	// mc.returnVoid();
	// });
	// }

	private Type makeHandleCreate() {
		Type createdCommand = domainDefinition.apitypeOf(methodName + "CreateCommand");
		Type createdEvent = domainDefinition.apitypeOf(methodName + "CreatedEvent");
		{
			context.add(createdCommand.getClassName(), ClassBuilder.make(createdCommand).field(TargetAggregateIdentifier.class, idField).fields(sagaFields)
			        .publicInitAllFields().defineAllPropetyGet().publicToStringWithAllFields());

			context.add(createdEvent.getClassName(), ClassBuilder.make(createdEvent).field(TargetAggregateIdentifier.class, idField).fields(sagaFields)
			        .publicInitAllFields().defineAllPropetyGet().publicToStringWithAllFields());

			sagaObjectBody.publicMethod("<init>").annotation(CommandHandler.class).parameter("command", createdCommand).code(mc -> {
				mc.initObject();
				mc.newInstace(createdEvent);
				mc.dup();
				for (Field field : sagaFieldsWithID) {
					mc.object("command").getProperty(field);
				}
				mc.type(createdEvent).invokeSpecial("<init>", sagaFieldsWithID);
				mc.type(AggregateLifecycle.class).invokeStatic("apply", createdEvent);

			});

			sagaObjectBody.publicMethod("on").annotation(EventHandler.class).parameter("event", createdEvent).code(mc -> {
				for (Field field : sagaFieldsWithID) {
					mc.loadThis();
					mc.object("event").getProperty(field);
					mc.type(mc.thisType()).putTo(field);
				}
				mc.loadThis();
				mc.type(statusType).getStatic(Status.Started.name(), statusType);
				mc.type(mc.thisType()).putTo("status", statusType);
			});
		}
		return createdEvent;
	}

	private void makeHandleEndOfSaga(DomainDefinition domainDefinition, List<Field> datasFieldsWithID, String resultString, int resultValue) {
		Type commandType = domainDefinition.apitypeOf(this.methodName, "Mark", resultString, "Command");
		Type eventType = domainDefinition.apitypeOf(this.methodName, resultString, "Event");

		context.add(ClassBuilder.make(commandType).field(idField).readonlyPojo());

		context.add(ClassBuilder.make(eventType).field(idField).readonlyPojo());

		sagaObjectBody.publicMethod("handle").annotation(CommandHandler.class).parameter("command", commandType).code(mc -> {
			mc.newInstace(eventType);
			mc.dup();
			mc.object("command").getProperty(idField);
			mc.type(eventType).invokeSpecial("<init>", idField);
			mc.type(AggregateLifecycle.class).invokeStatic("apply", eventType);
		});

		sagaObjectBody.publicMethod("on").annotation(EventHandler.class).parameter("event", eventType).code(mc -> {
			mc.loadThis();
			mc.type(statusType).getStatic(resultString, statusType);
			mc.type(mc.thisType()).putTo("status", statusType);
		});
	}

	private void makeInvokeCommand(int opcode, String owner, String name, String desc, boolean itf) {
		Type[] types = Type.getArgumentTypes(desc);
		Type returnType = Type.getReturnType(desc);

		Field[] methodParams = new Field[types.length];

		int offset = methodStack.size();
		for (int i = types.length - 1; i >= 0; i--) {
			Type type = types[i];
			offset -= type.getSize();
			Field field = methodStack.elementAt(offset);
			methodParams[i] = field;
		}
		Type ownerType = Type.getObjectType(owner);
		offset -= ownerType.getSize();
		Field ownerField = methodStack.elementAt(offset);

		Type commandType = domainDefinition.apitypeOf(this.methodName, ownerField.name, name, "Command");

		{// make command
			context.add(ClassBuilder.make(commandType).field(TargetAggregateIdentifier.class, domainDefinition.identifierField).field(idField)
			        .fields(methodParams).readonlyPojo());
		}
		if (Type.getType(boolean.class).getDescriptor().equals(returnType.getDescriptor())) {
			List<Field> eventFields = new ArrayList<>();

			eventFields.add(domainDefinition.identifierField);
			eventFields.add(idField);
			for (Field field : methodParams) {
				eventFields.add(field);
			}
			blockCurrent().eventFields = eventFields;
			blockCurrent().commandName = this.methodName + toCamelUpper(ownerField.name) + toCamelUpper(name);
		}

		blockCurrent().code.block(mc -> {
			// mc.def("command", commandType);

			mc.loadThis().get("commandBus");

			mc.newInstace(commandType);
			mc.dup();
			{
				List<Field> params = new ArrayList<>();
				mc.object("this").get(ownerField.name + toCamelUpper(domainDefinition.identifierField.name), domainDefinition.identifierField.type);
				mc.object("event").getProperty(idField);
				params.add(domainDefinition.identifierField);
				params.add(idField);
				for (Field field : methodParams) {
					mc.object("this").get(field);
					params.add(field);
				}
				mc.type(commandType).invokeSpecial("<init>", params);
			}
			mc.type(GenericCommandMessage.class).invokeStatic(CommandMessage.class, "asCommandMessage", commandType);
			mc.type(CommandBus.class).invokeVirtual("dispatch", GenericCommandMessage.class);
		});

		// System.out.println(methodParams);

	}

	private void makeSaga() {
		sagaObjectBody = ClassBuilder.make(sagaObjectType).field(AggregateIdentifier.class, idField).fields(sagaFields).field("status", statusType);
		context.add("saga", sagaObjectBody);
		sagaObjectBody.publicMethod("<init>").code(mc -> {
			mc.initObject();
		});
	}

	private void makeSagaManagement() {
		sagaManagementClassBody = ClassBuilder.make(sagaType).field(ACC_PRIVATE + ACC_TRANSIENT, "commandBus", CommandBus.class)
		        .definePropertySet(Inject.class, "commandBus", CommandBus.class).fields(sagaFields);
		context.add("managementSaga", sagaManagementClassBody);
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

	private Variable stackPop(int size) {
		Variable var = null;
		for (int i = 0; i < size; i++) {
			var = methodStack.pop();
		}
		return var;
	}

	private Variable stackPop(Type type) {
		return stackPop(type.getSize());
	}

	private void stackPush(int size) {
		for (int i = 0; i < size; i++) {
			methodStack.push(SagaClassListener.NA);
		}
	}

	private void stackPush(String name, Type type) {
		methodStack.push(new Variable(name, type));
		stackPush(type.getSize() - 1);
	}

	@Override
	public void visitCode() {

		statusType = domainDefinition.apitypeOf(this.methodName, "Status");
		{
			context.add(StatusBuilder.build(statusType, Status.Started.name(), Status.Failed.name(), Status.Completed.name()));
		}

		makeSaga();

		makeSagaManagement();

		makeCommandHandler();

		Type createdEvent = makeHandleCreate();

		blockStartOfRoot("on" + AsmBuilder.toSimpleName(sagaObjectType.getClassName()) + methodBlockIndex++, sagaFields, createdEvent);

		if (Type.getType(boolean.class).getDescriptor().equals(methodReturnType.getDescriptor())) {
			makeHandleEndOfSaga(domainDefinition, sagaFieldsWithID, Status.Completed.name(), 1);
			makeHandleEndOfSaga(domainDefinition, sagaFieldsWithID, Status.Failed.name(), 0);
		}
		super.visitCode();
	}

	@Override
	public void visitEnd() {
		blockCloseCurrent();
		super.visitEnd();

		// byte[] sageObjectCode = sagaObjectClassBody.end().toByteArray();
		// defineClass(sagaObjectType, sageObjectCode);
		//
		// byte[] sageCode = sagaClassBody.end().toByteArray();
		// defineClass(sagaType, sageCode);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitFieldInsn", name);

		if (opcode == GETSTATIC || opcode == GETFIELD) {
			stackPush("", Type.getType(desc));
		} else if (opcode == PUTSTATIC || opcode == PUTFIELD) {
			stackPop(Type.getType(desc));
		}
		super.visitFieldInsn(opcode, owner, name, desc);
		printStack();
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitIincInsn", var);

		super.visitIincInsn(var, increment);
		printStack();
	}

	@Override
	public void visitInsn(int opcode) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitInsn", opcode);

		if (IRETURN <= opcode && opcode <= RETURN) {
			makeFinished((Integer) methodStack.peek().value);
			// currentBlock().code.block(mc->{
			// mc.def("i",int.class);
			// mc.load("i");
			// mc.load("i");
			// mc.insn(IADD);
			// mc.storeTop("i");
			// });
			SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "return", (Integer) methodStack.peek().value);
		}

		int cnt = Types.SIZE[opcode];
		if (cnt > 0) {
			stackPush(cnt);
		} else {
			stackPop(-cnt);
		}

		if (ICONST_0 <= opcode && opcode <= ICONST_5) {
			methodStack.peek().value = opcode - ICONST_0;
		}

		super.visitInsn(opcode);
		printStack();
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitIntInsn", operand);

		int cnt = Types.SIZE[opcode];
		if (cnt > 0) {
			stackPush(cnt);
		} else {
			stackPop(-cnt);
		}
		super.visitIntInsn(opcode, operand);
		printStack();
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		SagaClassListener.LOGGER.debug("[]{}jump", SagaClassListener.repeat("    ", methodBlockStack.size()));
		if (opcode == GOTO) {
			blockCurrent().elseLabel = label;

			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				stackPush(cnt);
			} else {
				stackPop(-cnt);
			}

		} else {
			blockStartOfResult("inner" + methodBlockIndex++, label);

			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				stackPush(cnt);
			} else {
				stackPop(-cnt);
			}
			super.visitJumpInsn(opcode, label);
		}
		printStack();
	}

	@Override
	public void visitLabel(Label label) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "label", label);
		if (methodBlockStack.size() > 0 && label == blockCurrent().labelClose) {
			blockCloseCurrent();
		}
		super.visitLabel(label);
		printStack();
	}

	@Override
	public void visitLdcInsn(Object cst) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitLdcInsn", cst);

		stackPush("", Type.getType(cst.getClass()));

		super.visitLdcInsn(cst);
		printStack();
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitMethodInsn", name);
		if (opcode == INVOKESTATIC) {
			Type[] params = Type.getArgumentTypes(desc);
			for (int i = params.length - 1; i >= 0; i--) {
				stackPop(params[i]);
			}
			Type returnType = Type.getReturnType(desc);
			if (returnType != Type.VOID_TYPE) {
				stackPush("", returnType);
			}
		} else {
			makeInvokeCommand(opcode, owner, name, desc, itf);

			Type ownerType = Type.getObjectType(owner);

			Type[] params = Type.getArgumentTypes(desc);
			for (int i = params.length - 1; i >= 0; i--) {
				stackPop(params[i]);
			}
			Variable varOwner = stackPop(ownerType);
			Type returnType = Type.getReturnType(desc);
			if (returnType != Type.VOID_TYPE) {
				stackPush(varOwner.name + "_" + name, returnType);
			}
		}
		super.visitMethodInsn(opcode, owner, name, desc, itf);
		printStack();

	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitTypeInsn", type);
		if (opcode == NEW) {
			stackPush("", Type.getObjectType(type));
		}
		super.visitTypeInsn(opcode, type);
		printStack();
	}

	@Override
	public void visitVarInsn(int opcode, int varLocal) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitVarInsn", varLocal);
		if (ILOAD <= opcode && opcode <= SALOAD) {
			Variable var = methodVariablesList.get(methodLocalsOfVar[varLocal]);
			stackPush(var.name, var.type);
		}
		if (ISTORE <= opcode && opcode <= SASTORE) {
			Variable var = methodVariablesList.get(methodLocalsOfVar[varLocal]);
			stackPop(var.type);
		}
		super.visitVarInsn(opcode, varLocal);
		printStack();
	}
}