package com.nebula.cqrs.axon.asm;

import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.axon.pojo.PojoBuilder;
import com.nebula.tinyasm.ClassBuilder;

public class CQRSEventRealBuilder extends PojoBuilder {

	public static byte[] dump(Event target) {
		return ClassBuilder.make(ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, target.type).fields(target.fields).defineAllPropetyGet().publicInitAllFields()
		        .publicToStringWithAllFields().toByteArray();
	}
}
