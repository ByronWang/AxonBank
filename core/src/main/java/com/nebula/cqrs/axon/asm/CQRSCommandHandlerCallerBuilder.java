package com.nebula.cqrs.axon.asm;

import java.util.function.Consumer;

import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.api.ClassBody;

public class CQRSCommandHandlerCallerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type handleType, Type objectType, Type domainType, Type commandType, Command command, DomainDefinition domainDefinition)
	        throws Exception {
		ClassBody cw = ClassBuilder.make(ACC_SUPER, objectType, Object.class, Consumer.class, new Type[] { domainType });
		cw.visitor().visitInnerClass(objectType.getInternalName(), handleType.getInternalName(), "Inner" + command.commandName, 0);
		cw.field("command", command.type);
		cw.field(ACC_FINAL + ACC_SYNTHETIC, "this$0", handleType);

		visitDefine_init(cw, handleType, command);
		visitDefine_invoke(cw, domainType, command);
		visitDefine_invoke_bridge(cw, domainType);

		return cw.toByteArray();
	}

	private static void visitDefine_init(ClassBody cw, Type handleType, Command command) {
		cw.publicMethod("<init>").parameter("handle", handleType).parameter("command", command.type).code(mb -> {
			mb.loadThis().put(1, "this$0");
			mb.line(15).initObject();
			mb.loadThis().put("command", "command");
			mb.returnVoid();
		});
	}

	private static void visitDefine_invoke(ClassBody cw, Type domainType, Command command) {
		cw.publicMethod("accept").parameter("domain", domainType).code(mb -> {
			mb.load("domain");

			for (int i = 0; i < command.methodParams.length; i++) {
				mb.loadThis().get("command").getProperty(command.methodParams[i]);
			}
			mb.type(domainType).invokeVirtual(command.returnType, command.methodName, command.methodParams);

			if (command.returnType != Type.VOID_TYPE) mb.pop();
			mb.returnVoid();
		});
	}

	private static void visitDefine_invoke_bridge(ClassBody cw, Type domainType) {
		cw.method(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, Type.VOID_TYPE, "accept").parameter("domain", Object.class).code(mb -> {
			mb.load("this");
			mb.load("domain");
			mb.checkCast(domainType);
			mb.useTopThis().invokeVirtual("accept", domainType);

			mb.returnVoid();
		});
	}
}
