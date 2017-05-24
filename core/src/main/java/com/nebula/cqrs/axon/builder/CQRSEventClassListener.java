package com.nebula.cqrs.axon.builder;

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
import com.nebula.tinyasm.util.AsmBuilderHelper;
import com.nebula.tinyasm.util.ConvertFromParamsToClassMethodVisitor;
import com.nebula.tinyasm.util.Field;

public class CQRSEventClassListener extends ClassVisitor implements ClassListener, Types {

	public CQRSEventClassListener() {
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

			Field identifierField = domainDefinition.identifierField;
			boolean hasNotIdentifier = true;
			for (Field param : params) {
				if (identifierField.name.equals(param.name)) {
					hasNotIdentifier = false;
					break;
				}
			}
			
			if(hasNotIdentifier){
				context.add(ClassBuilder.make(eventType).field(identifierField).fields(params).publicInitAllFields());
			}else{
				context.add(ClassBuilder.make(eventType).fields(params).publicInitAllFields());				
			}


			ConvertFromParamsToClassMethodVisitor eventMethodVisitor = new ConvertFromParamsToClassMethodVisitor(implClassBody.visitor(), access, newMethodName, desc, signature,
			        exceptions, eventType, params);

			return eventMethodVisitor;
		}
		return null;
	}
}
