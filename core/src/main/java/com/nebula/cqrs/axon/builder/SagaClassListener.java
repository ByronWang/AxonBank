package com.nebula.cqrs.axon.builder;

import static com.nebula.tinyasm.util.AsmBuilder.toCamelUpper;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_TRANSIENT;
import static org.objectweb.asm.Opcodes.ASM5;
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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nebula.cqrs.axon.builder.SagaBlock.BlockType;
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

public class SagaClassListener extends ClassVisitor implements DomainListener {
	class SagaMethodVisitor extends MethodVisitor {

		ClassBody commandHandlerBody;

		Type commandHandlerType;
		
		final DomainContext context;
		// Block currentBlock;
		DomainDefinition domainDefinition;

		int sagaBlockIndex = 0;
		Stack<SagaBlock> sagaBlockStack = new Stack<>();
		
		List<Field> sagaDatasFields;
		List<Field> sagaDatasFieldsWithID;

		Field sagaIdField;
		int[] sagaLocalsOfVar;

		ClassBody sagaManagementClassBody;
		String sagaMethodName = null;

		String sagaName = null;
		ClassBody sagaObjectBody;

		Type sagaObjectType;
		Type sagaReturnType;

		Stack<Variable> sagaStack = new Stack<>();
		
		Type sagaType;

		// Block topBlock;
		List<Variable> sagaVariablesList;

		Type statusType;

		public SagaMethodVisitor(DomainContext context, MethodVisitor mv, MethodInfo methodInfo, int access, String name, String desc, String signature) {
			super(ASM5, mv);
			this.context = context;
			this.sagaMethodName = name;
			this.sagaVariablesList = methodInfo.locals;
			this.sagaLocalsOfVar = Types.computerLocalsVariable(this.sagaVariablesList);

			this.domainDefinition = context.getDomainDefinition();
			sagaObjectType = domainDefinition.typeOf(sagaMethodName);
			sagaType = domainDefinition.typeOf(sagaMethodName, "ManagementSaga");
			commandHandlerType = domainDefinition.topLeveltypeOf("CommandHandler");
			sagaDatasFields = new ArrayList<>();

			for (Field field : methodInfo.params) {
				if (field.type.getClassName().startsWith("java/") || field.type.getDescriptor().length() == 1) {
					sagaDatasFields.add(field);
				} else {
					sagaDatasFields
					        .add(new Field(field.name + AsmBuilder.toCamelUpper(domainDefinition.identifierField.name), domainDefinition.identifierField.type));
					// TODO to deal other type
				}
			}

			sagaIdField = new Field(sagaMethodName + "Id", Type.getType(String.class));
			sagaDatasFieldsWithID = new ArrayList<>();
			sagaDatasFieldsWithID.add(sagaIdField);
			sagaDatasFieldsWithID.addAll(sagaDatasFields);

			// List<Field> realFields = new ArrayList<>();
			// realFields.addAll(datasFields);
			sagaReturnType = Type.getReturnType(desc);

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

			SagaBlock previousBlock = sagaBlockStack.pop();
			pop(sagaStack.size() - previousBlock.startStackIndex);

			LOGGER.debug("[]{}****}", repeat("    ", sagaBlockStack.size()));

			switch (previousBlock.blockType) {
			case IFBLOCK:
				if (previousBlock.elseLabel != null) {

					blockStartOfElse(previousBlock.name + "Else", previousBlock.elseLabel, BlockType.ELSEBLOCK, previousBlock);
				} else {
					if (sagaBlockStack.size() > 0) {
						blockStartOfElse(previousBlock.name + "ElseVirtual", previousBlock.elseLabel, BlockType.VITUALBLOCK, previousBlock);
					}
				}
				break;
			case ELSEBLOCK:
				break;
			case VITUALBLOCK:
				if (sagaBlockStack.size() > 0) {
					blockCloseCurrent();
				}
				break;
			case METHODBLOCK:
			default:
				break;
			}
			printStack();
		}

		private void blockStartOfElse(String name, Label labelClose, SagaBlock.BlockType blockType, SagaBlock ifBlock) {
			LOGGER.debug("[]{}else{****", repeat("    ", sagaBlockStack.size()));
			SagaBlock parentBlock = sagaBlockStack.peek();

			Label thisLabelclose = labelClose;
			if (labelClose == null) {
				thisLabelclose = parentBlock.labelClose;
			}

			SagaBlock nextBlock = sagaBlockStack.push(new SagaBlock(name, thisLabelclose, blockType, ifBlock.startStackIndex));
			makeBlockBeginOfResult(parentBlock, nextBlock);
		}

		private void blockStartOfResult(String name, Label labelClose) {
			LOGGER.debug("[]{}if{****", repeat("    ", sagaBlockStack.size()));
			SagaBlock parentBlock = sagaBlockStack.peek();
			Label thisLabelclose = labelClose;
			if (labelClose == null) {
				thisLabelclose = parentBlock.labelClose;
			}

			SagaBlock nextBlock = sagaBlockStack.push(new SagaBlock(name, thisLabelclose, sagaStack.size()));
			makeBlockBeginOfResult(parentBlock, nextBlock);
		}

		private void blockStartOfRoot(String name, List<Field> datasFields, Type createdEvent) {
			LOGGER.debug("[]{}root{****", repeat("    ", sagaBlockStack.size()));
			SagaBlock nextBlock = sagaBlockStack.push(new SagaBlock(name, null, sagaStack.size()));
			makeBlockBeginOfRoot(nextBlock, datasFields, createdEvent);
		}

		private SagaBlock currentBlock() {
			return sagaBlockStack.peek();
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
			block.code = sagaManagementClassBody.publicMethod("on").annotation(SagaEventHandler.class, "associationProperty", sagaIdField.name)
			        .parameter("event", eventType).begin();
		}

		private void makeBlockBeginOfRoot(SagaBlock block, List<Field> datasFields, Type createdEvent) {
			block.code = sagaManagementClassBody.publicMethod("on").annotation(StartSaga.class)
			        .annotation(SagaEventHandler.class, "associationProperty", sagaIdField.name).parameter("event", createdEvent).begin().block(mc -> {
				        for (Field field : datasFields) {
					        mc.loadThis();
					        mc.object("event").getProperty(field);
					        mc.type(mc.thisType()).putTo(field);
				        }
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
				commandType = domainDefinition.apitypeOf(this.sagaMethodName, "Mark", Status.Completed.name(), "Command");
			} else {
				commandType = domainDefinition.apitypeOf(this.sagaMethodName, "Mark", Status.Failed.name(), "Command");
			}

			currentBlock().code.block(mc -> {
				mc.loadThis().get("commandBus");

				mc.newInstace(commandType);
				mc.dup();
				{
					List<Field> paramTypes = new ArrayList<>();
					mc.object("event").getProperty(sagaIdField);
					paramTypes.add(sagaIdField);
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
			Type createdCommand = domainDefinition.apitypeOf(sagaMethodName + "CreateCommand");
			Type createdEvent = domainDefinition.apitypeOf(sagaMethodName + "CreatedEvent");
			{
				context.add(createdCommand.getClassName(), ClassBuilder.make(createdCommand).field(TargetAggregateIdentifier.class, sagaIdField)
				        .fields(sagaDatasFields).publicInitAllFields().defineAllPropetyGet().publicToStringWithAllFields());

				context.add(createdEvent.getClassName(), ClassBuilder.make(createdEvent).field(TargetAggregateIdentifier.class, sagaIdField).fields(sagaDatasFields)
				        .publicInitAllFields().defineAllPropetyGet().publicToStringWithAllFields());

				sagaObjectBody.publicMethod("<init>").annotation(CommandHandler.class).parameter("command", createdCommand).code(mc -> {
					mc.initObject();
					mc.newInstace(createdEvent);
					mc.dup();
					for (Field field : sagaDatasFieldsWithID) {
						mc.object("command").getProperty(field);
					}
					mc.type(createdEvent).invokeSpecial("<init>", sagaDatasFieldsWithID);
					mc.type(AggregateLifecycle.class).invokeStatic("apply", createdEvent);

				});

				sagaObjectBody.publicMethod("on").annotation(EventHandler.class).parameter("event", createdEvent).code(mc -> {
					for (Field field : sagaDatasFieldsWithID) {
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
			Type commandType = domainDefinition.apitypeOf(this.sagaMethodName, "Mark", resultString, "Command");
			Type eventType = domainDefinition.apitypeOf(this.sagaMethodName, resultString, "Event");

			context.add(ClassBuilder.make(commandType).field(sagaIdField).readonlyPojo());

			context.add(ClassBuilder.make(eventType).field(sagaIdField).readonlyPojo());

			sagaObjectBody.publicMethod("handle").annotation(CommandHandler.class).parameter("command", commandType).code(mc -> {
				mc.newInstace(eventType);
				mc.dup();
				mc.object("command").getProperty(sagaIdField);
				mc.type(eventType).invokeSpecial("<init>", sagaIdField);
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

			int offset = sagaStack.size();
			for (int i = types.length - 1; i >= 0; i--) {
				Type type = types[i];
				offset -= type.getSize();
				Field field = sagaStack.elementAt(offset);
				methodParams[i] = field;
			}
			Type ownerType = Type.getObjectType(owner);
			offset -= ownerType.getSize();
			Field ownerField = sagaStack.elementAt(offset);

			Type commandType = domainDefinition.apitypeOf(this.sagaMethodName, ownerField.name, name, "Command");

			{// make command
				context.add(ClassBuilder.make(commandType).field(TargetAggregateIdentifier.class, domainDefinition.identifierField).field(sagaIdField)
				        .fields(methodParams).readonlyPojo());
			}
			if (Type.getType(boolean.class).getDescriptor().equals(returnType.getDescriptor())) {
				List<Field> eventFields = new ArrayList<>();

				eventFields.add(domainDefinition.identifierField);
				eventFields.add(sagaIdField);
				for (Field field : methodParams) {
					eventFields.add(field);
				}
				currentBlock().eventFields = eventFields;
				currentBlock().commandName = this.sagaMethodName + toCamelUpper(ownerField.name) + toCamelUpper(name);
			}

			currentBlock().code.block(mc -> {
				// mc.def("command", commandType);

				mc.loadThis().get("commandBus");

				mc.newInstace(commandType);
				mc.dup();
				{
					List<Field> params = new ArrayList<>();
					mc.object("this").get(ownerField.name + toCamelUpper(domainDefinition.identifierField.name), domainDefinition.identifierField.type);
					mc.object("event").getProperty(sagaIdField);
					params.add(domainDefinition.identifierField);
					params.add(sagaIdField);
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
			sagaObjectBody = ClassBuilder.make(sagaObjectType).field(AggregateIdentifier.class, sagaIdField).fields(sagaDatasFields).field("status", statusType);
			context.add("saga", sagaObjectBody);
			sagaObjectBody.publicMethod("<init>").code(mc -> {
				mc.initObject();
			});
		}

		private void makeSagaManagement() {
			sagaManagementClassBody = ClassBuilder.make(sagaType).field(ACC_PRIVATE + ACC_TRANSIENT, "commandBus", CommandBus.class)
			        .definePropertySet(Inject.class, "commandBus", CommandBus.class).fields(sagaDatasFields);
			context.add("managementSaga", sagaManagementClassBody);
		}

		private Variable pop(int size) {
			Variable var = null;
			for (int i = 0; i < size; i++) {
				var = sagaStack.pop();
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
				sagaStack.push(NA);
			}
		}

		private void push(String name, Type type) {
			sagaStack.push(new Variable(name, type));
			push(type.getSize() - 1);
		}

		@Override
		public void visitCode() {

			statusType = domainDefinition.apitypeOf(this.sagaMethodName, "Status");
			{
				context.add(StatusBuilder.build(statusType, Status.Started.name(), Status.Failed.name(), Status.Completed.name()));
			}

			makeSaga();

			makeSagaManagement();

			makeCommandHandler();

			Type createdEvent = makeHandleCreate();

			blockStartOfRoot("on" + AsmBuilder.toSimpleName(sagaObjectType.getClassName()) + sagaBlockIndex++, sagaDatasFields, createdEvent);

			if (Type.getType(boolean.class).getDescriptor().equals(sagaReturnType.getDescriptor())) {
				makeHandleEndOfSaga(domainDefinition, sagaDatasFieldsWithID, Status.Completed.name(), 1);
				makeHandleEndOfSaga(domainDefinition, sagaDatasFieldsWithID, Status.Failed.name(), 0);
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
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "visitFieldInsn", name);

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
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "visitIincInsn", var);

			super.visitIincInsn(var, increment);
			printStack();
		}

		@Override
		public void visitInsn(int opcode) {
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "visitInsn", opcode);

			if (IRETURN <= opcode && opcode <= RETURN) {
				makeFinished((Integer) sagaStack.peek().value);
				// currentBlock().code.block(mc->{
				// mc.def("i",int.class);
				// mc.load("i");
				// mc.load("i");
				// mc.insn(IADD);
				// mc.storeTop("i");
				// });
				LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "return", (Integer) sagaStack.peek().value);
			}

			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				push(cnt);
			} else {
				pop(-cnt);
			}

			if (ICONST_0 <= opcode && opcode <= ICONST_5) {
				sagaStack.peek().value = opcode - ICONST_0;
			}

			super.visitInsn(opcode);
			printStack();
		}

		@Override
		public void visitIntInsn(int opcode, int operand) {
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "visitIntInsn", operand);

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
			LOGGER.debug("[]{}jump", repeat("    ", sagaBlockStack.size()));
			if (opcode == GOTO) {
				currentBlock().elseLabel = label;

				int cnt = Types.SIZE[opcode];
				if (cnt > 0) {
					push(cnt);
				} else {
					pop(-cnt);
				}

			} else {
				blockStartOfResult("inner" + sagaBlockIndex++, label);

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
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "label", label);
			if (sagaBlockStack.size() > 0 && label == currentBlock().labelClose) {
				blockCloseCurrent();
			}
			super.visitLabel(label);
			printStack();
		}

		@Override
		public void visitLdcInsn(Object cst) {
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "visitLdcInsn", cst);

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
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "visitMethodInsn", name);
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
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "visitTypeInsn", type);
			if (opcode == NEW) {
				push("", Type.getObjectType(type));
			}
			super.visitTypeInsn(opcode, type);
			printStack();
		}

		@Override
		public void visitVarInsn(int opcode, int varLocal) {
			LOGGER.debug("[]{}{}\t\t\t{}", repeat("    ", sagaBlockStack.size()), "visitVarInsn", varLocal);
			if (ILOAD <= opcode && opcode <= SALOAD) {
				Variable var = sagaVariablesList.get(sagaLocalsOfVar[varLocal]);
				push(var.name, var.type);
			}
			if (ISTORE <= opcode && opcode <= SASTORE) {
				Variable var = sagaVariablesList.get(sagaLocalsOfVar[varLocal]);
				pop(var.type);
			}
			super.visitVarInsn(opcode, varLocal);
			printStack();
		}
	}

	enum Status {
		Completed, Failed, Started
	}

	private final static Logger LOGGER = LoggerFactory.getLogger(SagaClassListener.class);

	static Variable NA = new Variable("NA", Type.VOID_TYPE);

	static String repeat(String str, int times) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < times; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	DomainContext context;

	public SagaClassListener() {
		super(ASM5);
	}

	boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	@Override
	public ClassVisitor listen(DomainContext context) {
		this.context = context;
		return this;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (is(access, ACC_PUBLIC) && is(access, ACC_STATIC)) {
			MethodInfo method = context.getDomainDefinition().methods.get(name);
			return new SagaMethodVisitor(context, mv, method, access, name, desc, signature);
		} else {

			return mv;
		}
	}
}
