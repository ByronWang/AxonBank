package com.nebula.cqrs.axon.asm;

import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.axon.pojo.PojoBuilder;
import com.nebula.tinyasm.ClassBuilder;

public class CQRSEventAliasBuilder extends PojoBuilder {

	public static byte[] dump(Event event) {

		return ClassBuilder.make(event.type, event.getRealEvent().type).publicInitWithSuper(event.fields).toByteArray();
	}
}
