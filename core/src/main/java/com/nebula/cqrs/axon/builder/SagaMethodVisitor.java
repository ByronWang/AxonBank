package com.nebula.cqrs.axon.builder;

import static com.nebula.tinyasm.util.AsmBuilder.toCamelUpper;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_TRANSIENT;

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
	final List<Field> sagaFields;
	final List<Field> sagaFieldsWithID;

	ClassBody sagaManagementClassBody;
	String sagaName = null;

	ClassBody sagaObjectBody;

	Type sagaObjectType;

	Type sagaType;
	protected Type statusType;

	public SagaMethodVisitor(DomainContext context, MethodVisitor mv, MethodInfo methodInfo, int access, String name, String desc, String signature) {
		super(mv, methodInfo, access, name, desc, signature);
		this.context = context;

		this.domainDefinition = context.getDomainDefinition();
		sagaObjectType = domainDefinition.typeOf(name);
		sagaType = domainDefinition.typeOf(name, "ManagementSaga");
		commandHandlerType = domainDefinition.topLeveltypeOf("CommandHandler");
		sagaFields = new ArrayList<>();

		for (Field field : methodInfo.params) {
			if (field.type.getClassName().startsWith("java/") || field.type.getDescriptor().length() == 1) {
				sagaFields.add(field);
			} else {
				sagaFields.add(new Field(field.name + AsmBuilder.toCamelUpper(domainDefinition.identifierField.name), domainDefinition.identifierField.type));
				// TODO to deal other type
			}
		}

		idField = new Field(name + "Id", Type.getType(String.class));
		sagaFieldsWithID = new ArrayList<>();
		sagaFieldsWithID.add(idField);
		sagaFieldsWithID.addAll(sagaFields);
		this.sagaName = name;

		prepareSagaClassBody();
	}

	public void prepareSagaClassBody() {

		statusType = domainDefinition.apitypeOf(this.sagaName, "Status");
		{
			context.add(StatusBuilder.build(statusType, Status.Started.name(), Status.Failed.name(), Status.Completed.name()));
		}

		sagaObjectBody = ClassBuilder.make(sagaObjectType).field(AggregateIdentifier.class, idField).fields(sagaFields).field("status", statusType)
		        .publicInitNone();
		context.add("saga", sagaObjectBody);

		sagaManagementClassBody = ClassBuilder.make(sagaType).field(ACC_PRIVATE + ACC_TRANSIENT, "commandBus", CommandBus.class)
		        .definePropertySet(Inject.class, "commandBus", CommandBus.class).fields(sagaFields);
		context.add("managementSaga", sagaManagementClassBody);

		commandHandlerBody = context.get("commandHandler");
	}

	@Override
	protected void onStartNewBlock(SagaBlock parentBlock, SagaBlock block) {
		String result = null;

		if (block.blockType == BlockType.IFBLOCK) {
			result = Status.Completed.name();
		} else {
			result = Status.Failed.name();
		}

		final Type eventType = domainDefinition.apitypeOf(parentBlock.commandName, result, "Event");
		context.add(ClassBuilder.make(eventType).fields(parentBlock.eventFields).readonlyPojo());
		onClosePreviousBlock();
		block.code = sagaManagementClassBody.publicMethod("on").annotation(SagaEventHandler.class, "associationProperty", idField.name)
		        .parameter("event", eventType).begin();
	}

	@Override
	protected void onStartSaga(SagaBlock block, Type methodReturnType) {

		if (Type.getType(boolean.class).getDescriptor().equals(methodReturnType.getDescriptor())) {
			onHandleSagaResult(domainDefinition, sagaFieldsWithID, Status.Completed.name(), 1);
			onHandleSagaResult(domainDefinition, sagaFieldsWithID, Status.Failed.name(), 0);
		}
		Type createCommandType = domainDefinition.apitypeOf(sagaName + "CreateCommand");
		Type createdEventType = domainDefinition.apitypeOf(sagaName + "CreatedEvent");

		context.add(createCommandType.getClassName(), ClassBuilder.make(createCommandType).field(TargetAggregateIdentifier.class, idField).fields(sagaFields)
		        .publicInitAllFields().defineAllPropetyGet().publicToStringWithAllFields());

		context.add(createdEventType.getClassName(), ClassBuilder.make(createdEventType).field(TargetAggregateIdentifier.class, idField).fields(sagaFields)
		        .publicInitAllFields().defineAllPropetyGet().publicToStringWithAllFields());

		sagaObjectBody.publicMethod("<init>").annotation(CommandHandler.class).parameter("command", createCommandType).code(mc -> {
			mc.initObject();
			mc.newInstace(createdEventType);
			mc.dup();
			for (Field field1 : sagaFieldsWithID) {
				mc.object("command").getProperty(field1);
			}
			mc.type(createdEventType).invokeSpecial("<init>", sagaFieldsWithID);
			mc.type(AggregateLifecycle.class).invokeStatic("apply", createdEventType);
		});

		sagaObjectBody.publicMethod("on").annotation(EventHandler.class).parameter("event", createdEventType).code(mc -> {
			for (Field field2 : sagaFieldsWithID) {
				mc.loadThis();
				mc.object("event").getProperty(field2);
				mc.type(mc.thisType()).putTo(field2);
			}
			mc.loadThis();
			mc.type(statusType).getStatic(Status.Started.name(), statusType);
			mc.type(mc.thisType()).putTo("status", statusType);
		});

		block.code = sagaManagementClassBody.publicMethod("on").annotation(StartSaga.class)
		        .annotation(SagaEventHandler.class, "associationProperty", idField.name).parameter("event", createdEventType).begin().block(mc -> {
			        for (Field field : sagaFields) {
				        mc.loadThis();
				        mc.object("event").getProperty(field);
				        mc.type(mc.thisType()).putTo(field);
			        }
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

	@Override
	protected void onClosePreviousBlock() {
		ClassMethodCode code = blockCurrent().code;
		if (code != null) {
			code.returnVoid();
			code.end();
			blockCurrent().code = null;
		}
	}

	void onHandleSagaResult(DomainDefinition domainDefinition, List<Field> datasFieldsWithID, String resultString, int resultValue) {
		Type commandType = domainDefinition.apitypeOf(this.sagaName, "Mark", resultString, "Command");
		Type eventType = domainDefinition.apitypeOf(this.sagaName, resultString, "Event");

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

	@Override
	protected void onMarkSagaFinished(int value) {

		Type commandType;

		if (value == 1) {
			commandType = domainDefinition.apitypeOf(this.sagaName, "Mark", Status.Completed.name(), "Command");
		} else {
			commandType = domainDefinition.apitypeOf(this.sagaName, "Mark", Status.Failed.name(), "Command");
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

	@Override
	protected void onInvokeCommand(Stack<Variable> methodStack, int opcode, String owner, String name, String desc, boolean itf) {
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

		Type commandType = domainDefinition.apitypeOf(this.sagaName, ownerField.name, name, "Command");

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
			blockCurrent().commandName = this.sagaName + toCamelUpper(ownerField.name) + toCamelUpper(name);
		}

		blockCurrent().code.block(mc -> {
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

	}

}