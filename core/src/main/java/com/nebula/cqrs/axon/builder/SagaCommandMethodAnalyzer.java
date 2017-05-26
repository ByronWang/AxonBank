package com.nebula.cqrs.axon.builder;

import static org.objectweb.asm.Opcodes.ASM5;

import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.commandhandling.model.ApplyMore;
import org.axonframework.commandhandling.model.Repository;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.builder.DomainBuilder.MethodProvider;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AsmBuilderHelper;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.Lamba;
import com.nebula.tinyasm.util.MethodInfo;

public class SagaCommandMethodAnalyzer implements MethodProvider, Types {

	public SagaCommandMethodAnalyzer(String sagaProfix, Field sagaIdField, DomainContext context) {
		this.context = context;
		this.sagaProfix = sagaProfix;
		this.domainDefinition = context.getDomainDefinition();
		this.implClassBody = this.context.get("impl");
		this.commandHandlerClassBody = this.context.get("CommandHandler");
		this.sagaIdField = sagaIdField;
	}

	String sagaProfix;
	DomainContext context;
	DomainDefinition domainDefinition;
	ClassBody commandHandlerClassBody;
	ClassBody implClassBody;
	Field sagaIdField;

	@Override
	public MethodVisitor visitMethod(int access, String originName, String desc, String signature, String[] exceptions) {

		String methodName = concat(sagaProfix, originName);

		Type handlerType = commandHandlerClassBody.getType();
		Type domainType = domainDefinition.implDomainType;
		Type returnType = Type.getReturnType(desc);
		Field identifierField = domainDefinition.identifierField;
		String commandName;

		commandName = AsmBuilderHelper.toCamelUpper(methodName);
		MethodInfo method = domainDefinition.methods.get(originName);

		Type callerType = commandHandlerClassBody.referInnerClass("Invoke" + commandName);

		Type commandType = domainDefinition.typeOf(commandName, "Command");

		context.add(ClassBuilder.make(commandType).field(TargetAggregateIdentifier.class, domainDefinition.identifierField).field(this.sagaIdField)
		        .fields(method.params).readonlyPojo());
		context.add(Lamba.invokeConsumer(callerType, handlerType, domainType, "command", commandType, mb -> {
			mb.load("domain");

			for (Field param : method.params) {
				mb.loadThis().get("command").getProperty(param);
			}
			mb.type(domainType).invokeVirtual(returnType, method.name, method.params);

			if (returnType != Type.VOID_TYPE) mb.pop();
			mb.returnVoid();
		}));

		commandHandlerClassBody.publicMethod("handle", Exception.class).annotation(CommandHandler.class).parameter("command", commandType).code(mc -> {
			mc.def("aggregate", Aggregate.class, domainType);
			mc.line(27).loadThis().get("repository");
			mc.object("command").getProperty(identifierField);
			mc.type(Repository.class).invokeInterface(Aggregate.class, "load", identifierField.type).store("aggregate");
			mc.use("aggregate").with(m -> {
			    mc.newInstace(callerType);
			    mc.dup();
			    mc.load("this");
			    mc.load("command");
			    mc.type(callerType).invokeSpecial("<init>", handlerType, commandType);
		    }).invokeInterface("execute", Consumer.class);

			mc.returnVoid();
		});

		MethodVisitor methodVisitor = ((ClassBuilder) implClassBody).visitor().visitMethod(access, methodName, desc, signature, exceptions);
		CommandMethodVisitor commandMethodVisitor = new CommandMethodVisitor(methodVisitor, commandName, access, methodName, desc, signature, exceptions);

		return commandMethodVisitor;
	}

	class CommandMethodVisitor extends MethodVisitor {
		String commandName;

		public CommandMethodVisitor(MethodVisitor mv, String commandName, int access, String name, String desc, String signature, String[] exceptions) {
			super(ASM5, mv);
			this.commandName = commandName;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

			if (owner.equals(domainDefinition.implDomainType.getInternalName()) && name.startsWith("on")) {

				Type eventType = domainDefinition.typeOf(commandName, "Finished", "Event");
				Type realType = domainDefinition.typeOf(name.substring(2), "Event");
				MethodInfo method = domainDefinition.methods.get(name);
				final Field identifierField = domainDefinition.identifierField;

				boolean checkHasNotIdentifier = true;
				for (Field param : method.params) {
					if (identifierField.name.equals(param.name)) {
						checkHasNotIdentifier = false;
						break;
					}
				}

				final boolean hasNotIdentifier = checkHasNotIdentifier;

				if (hasNotIdentifier) {
					context.add(ClassBuilder.make(eventType, realType).field(sagaIdField)
					        .publicInitWithSuper(fieldsOf(domainDefinition.identifierField, method.params)));
				} else {
					context.add(ClassBuilder.make(eventType, realType).field(sagaIdField).publicInitWithSuper(method.params));
				}

				String applyMethodName = "apply" + commandName + "Finished";
				//
				implClassBody.protectdMethod(applyMethodName).parameter(method.params).code(mc -> {
					mc.newInstace(eventType);
					mc.dup();

					Field[] params = method.params;
					if (hasNotIdentifier) {
						mc.loadThis().get(identifierField);

						params = fieldsOf(identifierField, params);
					}

					for (Field param : method.params) {
						mc.load(param.name);
					}
					mc.type(eventType).invokeSpecial("<init>", params);
					mc.type(AggregateLifecycle.class).invokeStatic(ApplyMore.class, "apply", Object.class);
					mc.pop();
					mc.returnVoid();
				});

				super.visitMethodInsn(opcode, owner, applyMethodName, desc, itf);
			} else {

				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}
}
