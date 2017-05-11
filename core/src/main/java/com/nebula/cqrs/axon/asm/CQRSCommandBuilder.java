package com.nebula.cqrs.axon.asm;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.PojoBuilder;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.util.Field;

public class CQRSCommandBuilder extends PojoBuilder {

	public static byte[] dump(Command command) {
		Field[] objectFields = command.fields;

		ClassBody cb = ClassBuilder.make(command.type);
		cb.field(TargetAggregateIdentifier.class,objectFields[0]);
		for (int i = 1; i < objectFields.length; i++) {
			cb.field(objectFields[i]);
		}
		
		cb.defineAllPropetyGet().publicInitAllFields().publicToStringWithAllFields();

		return cb.toByteArray();
	}
}
