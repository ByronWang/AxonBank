package com.nebula.cqrs.axon.asm;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.util.Field;

public class CQRSCommandHandlerBuilder {

	public static byte[] dump(Type handleType, Type domainType, DomainDefinition domainDefinition) throws Exception {

		Field identifierField = domainDefinition.identifierField;

		ClassBody cb = ClassBuilder.make(handleType);

		for (Command command : domainDefinition.commands.values()) {
			cb.referInnerClass("Inner" + command.commandName);
		}

		cb.field("repository", Repository.class, domainType);

		cb.field("eventBus", EventBus.class);

		visitDefine_init(cb, handleType, domainType);

		for (Command command : domainDefinition.commands.values()) {
			Type callerType = Type.getObjectType(handleType.getInternalName() + "$" + "Inner" + command.commandName);
			Type commandType = command.type;
			if (command.ctorMethod) {
				visitDefine_handle_create(cb, handleType, callerType, commandType);
			} else {
				visitDefine_handle_execute(cb, handleType, callerType, commandType, domainType, identifierField);
			}
		}
		cb.end();

		return cb.toByteArray();
	}

	private static void visitDefine_init(ClassBody cb, Type handleType, Type domainType) {
		{
			cb.publicMethod("<init>").parameter("repository", Repository.class, domainType).parameter("eventBus", EventBus.class).code(mb -> {
				mb.line(15).initObject();
				mb.loadThis().put("repository", "repository");
				mb.loadThis().put("eventBus", "eventBus");
				mb.returnVoid();
			});
		}
	}

	private static void visitDefine_handle_create(ClassBody cb, Type handleType, Type callerType, Type commandType) {
		cb.publicMethod("handle", Exception.class).annotation(CommandHandler.class).parameter("command", commandType).code(mc -> {
			mc.line(22).loadThis().get("repository");
			mc.newInstace(callerType);
			mc.dup();
			mc.load("this");
			mc.load("command");
			mc.type(callerType).invokeSpecial("<init>", handleType, commandType);
			mc.type(Repository.class).invokeInterface(Aggregate.class, "newInstance", Callable.class);
			mc.pop();
			mc.returnVoid();
		});
	}

	private static void visitDefine_handle_execute(ClassBody cb, Type handleType, Type callerType, Type commandType, Type domainType, Field identifierField) {
		cb.publicMethod("handle", Exception.class).annotation(CommandHandler.class).parameter("command", commandType).code(mc -> {
			mc.def("aggregate", Aggregate.class, domainType);
			mc.line(27).loadThis().get("repository");
			mc.object("command").getProperty(identifierField);
			mc.type(Repository.class).invokeInterface(Aggregate.class, "load", identifierField.type).store("aggregate");
			mc.use("aggregate").with(m -> {
			    mc.newInstace(callerType);
			    mc.dup();
			    mc.load("this");
			    mc.load("command");
			    mc.type(callerType).invokeSpecial("<init>", handleType, commandType);
		    }).invokeInterface("execute", Consumer.class);

			mc.returnVoid();
		});
	}
}
