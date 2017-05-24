package com.nebula.cqrs.axon.builder;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.commandhandling.model.ApplyMore;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AsmBuilderHelper;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.Lamba;
import com.nebula.tinyasm.util.MethodInfo;

public class CQRSCommandClassListener extends ClassVisitor implements DomainListener, Types {

	public CQRSCommandClassListener() {
		super(ASM5);
	}

	DomainContext context;

	DomainDefinition domainDefinition;
	ClassBody commandHandlerClassBody;
	ClassBody implClassBody;

	@Override
	public ClassVisitor listen(DomainContext context) {
		this.context = context;
		this.domainDefinition = context.getDomainDefinition();

		Type commandHandler = domainDefinition.topLeveltypeOf("CommandHandler");
		commandHandlerClassBody = ClassBuilder.make(commandHandler);
		commandHandlerClassBody.field("repository", Repository.class, domainDefinition.implDomainType);
		commandHandlerClassBody.field("eventBus", EventBus.class);
		commandHandlerClassBody.publicMethod("<init>").parameter("repository", Repository.class, domainDefinition.implDomainType)
		        .parameter("eventBus", EventBus.class).code(mb -> {
			        mb.line(15).initObject();
			        mb.loadThis().put("repository", "repository");
			        mb.loadThis().put("eventBus", "eventBus");
			        mb.returnVoid();
		        });

		this.context.add("CommandHandler", commandHandlerClassBody);
		this.implClassBody = this.context.get("impl");
		return this;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (is(access, ACC_PUBLIC) && !is(access, ACC_STATIC)) {

			String methodName = name;
			String actionName;

			Type handlerType = commandHandlerClassBody.getType();
			Type domainType = domainDefinition.implDomainType;
			Type returnType = Type.getReturnType(desc);
			String commandName;
			if ("<init>".equals(name)) {
				actionName = "create";

				commandName = AsmBuilderHelper.toCamelUpper(actionName);

				Field identifierField = domainDefinition.identifierField;
				MethodInfo method = domainDefinition.methods.get(name);

				Type commandType = domainDefinition.typeOf(commandName, "Command");
				Type callerType = commandHandlerClassBody.referInnerClass("Invoke" + commandName);

				context.add(ClassBuilder.make(commandType).field(TargetAggregateIdentifier.class, domainDefinition.identifierField).fields(method.params)
				        .readonlyPojo());
				context.add(Lamba.invokeCallable(callerType, handlerType, domainType, "command", commandType, mb -> {
					mb.newInstace(domainType);

					mb.dup();
					for (Field param : method.params) {
						mb.loadThis().get("command").getProperty(param);
					}

					mb.type(domainType).invokeSpecial("<init>", method.params);

					mb.returnObject();
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

			} else {
				actionName = methodName;

				commandName = AsmBuilderHelper.toCamelUpper(actionName);
				MethodInfo method = domainDefinition.methods.get(name);

				Type callerType = commandHandlerClassBody.referInnerClass("Invoke" + commandName);

				Type commandType = domainDefinition.typeOf(commandName, "Command");
				context.add(ClassBuilder.make(commandType).field(TargetAggregateIdentifier.class, domainDefinition.identifierField).fields(method.params)
				        .readonlyPojo());
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
					mc.line(22).loadThis().get("repository");
					mc.newInstace(callerType);
					mc.dup();
					mc.load("this");
					mc.load("command");
					mc.type(callerType).invokeSpecial("<init>", commandHandlerClassBody.getType(), commandType);
					mc.type(Repository.class).invokeInterface(Aggregate.class, "newInstance", Callable.class);
					mc.pop();
					mc.returnVoid();
				});
			}

			MethodVisitor methodVisitor = ((ClassVisitor) implClassBody).visitMethod(access, name, desc, signature, exceptions);
			CommandMethodVisitor commandMethodVisitor = new CommandMethodVisitor(methodVisitor, commandName, access, name, desc, signature, exceptions);

			return commandMethodVisitor;
		}
		return null;
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

				context.add(ClassBuilder.make(eventType, realType).publicMethodInitWithAllFieldsToSuper(method.params));

				String applyMethodName = "apply" + commandName + "Finished";

				implClassBody.protectdMethod(applyMethodName).parameter(method.params).code(mc -> {
					mc.newInstace(eventType);
					mc.dup();

					boolean hasNotIdentifier = true;
					for (Field param : method.params) {
						if (identifierField.name.equals(param.name)) {
							hasNotIdentifier = false;
							break;
						}
					}

					Field[] params = method.params;
					if (hasNotIdentifier) {
						mc.loadThis().get(identifierField);
						Field[] newparams = new Field[params.length + 1];
						newparams[0] = identifierField;
						System.arraycopy(params, 0, newparams, 1, params.length);
						params = newparams;
					}

					for (Field param : method.params) {
						mc.load(param.name);
					}
					mc.type(eventType).invokeSpecial("<init>", params);
					mc.type(AggregateLifecycle.class).invokeStatic(ApplyMore.class, "apply", Object.class);
					mc.pop();
					mc.returnVoid();

					//
				    // if (event.withoutID) {
				    // AxonAsmBuilder.visitGetField(mv, 0, implDomainType,
				    // domainDefinition.identifierField);
				    // }
				});

				super.visitMethodInsn(opcode, owner, applyMethodName, desc, itf);
			} else {

				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}
}
