package com.nebula.tinyasm.ana;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ASM5;

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
import com.nebula.tinyasm.util.ConvertFromParamsToClassMethodVisitor;
import com.nebula.tinyasm.util.Field;

public class DomainEventClassListener extends ClassVisitor implements ClassListener, Types {

	public DomainEventClassListener() {
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

		this.implClassBody = this.context.get("impl");
		return this;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (is(access, ACC_PRIVATE) && name.startsWith("on")) {
			String newMethodName = "on";
			String eventName = AsmBuilderHelper.toCamelUpper(name.substring(2));

			Type eventType = context.getDomainDefinition().typeOf(eventName,"Event");
			Field[] params = context.getDomainDefinition().methods.get(name).params;

			context.add(ClassBuilder.make(eventType).fields(params).publicInitAllFields());

			ConvertFromParamsToClassMethodVisitor eventMethodVisitor = new ConvertFromParamsToClassMethodVisitor(implClassBody.visitor(), access, newMethodName, desc, signature,
			        exceptions, eventType, params);

			return eventMethodVisitor;
		}
		return null;
	}
}
