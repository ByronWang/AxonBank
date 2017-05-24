package com.nebula.tinyasm.ana;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.ILOAD;

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
import com.nebula.tinyasm.builder.ClassListener;
import com.nebula.tinyasm.builder.Context;
import com.nebula.tinyasm.util.AsmBuilderHelper;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.MethodInfo;

public class DomainCommandClassListener extends ClassVisitor implements ClassListener, Types {

	public DomainCommandClassListener() {
		super(ASM5);
	}

	Context context;

	DomainDefinition domainDefinition;
	ClassBody commandHandlerClassBody;
	ClassBody implClassBody;

	@Override
	public ClassVisitor listen(Context context) {
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

			MethodVisitor methodVisitor = ((ClassVisitor) implClassBody).visitMethod(access, name, desc, signature, exceptions);
			CommandMethodVisitor commandMethodVisitor = new CommandMethodVisitor(methodVisitor, access, name, desc, signature, exceptions);

			return commandMethodVisitor;
		}
		return null;
	}

	class CommandMethodVisitor extends MethodVisitor {
		String commandName;

		public CommandMethodVisitor(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
			super(ASM5, mv);

			String methodName = name;
			String actionName;
			boolean ctorMethod;

			if ("<init>".equals(name)) {
				ctorMethod = true;
				actionName = "create";
			} else {
				ctorMethod = false;
				actionName = methodName;
			}

			commandName = AsmBuilderHelper.toCamelUpper(actionName);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

			if (owner.equals(domainDefinition.implDomainType.getInternalName()) && name.startsWith("on")) {

				Type eventType = domainDefinition.typeOf(commandName, "Finished", "Event");
				Type realType = domainDefinition.typeOf(name.substring(2), "Event");
				MethodInfo method = domainDefinition.methods.get(name);

				context.add(ClassBuilder.make(eventType, realType).publicMethodInitWithAllFieldsToSuper(method.params));

				String applyMethodName = "apply" + commandName;

				implClassBody.protectdMethod(applyMethodName).parameter(method.params).code(mc -> {
					mc.newInstace(eventType);
					mc.dup();
					for (Field param : method.params) {
						mc.load(param.name);
					}
					mc.type(eventType).invokeSpecial("<init>", method.params);
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
