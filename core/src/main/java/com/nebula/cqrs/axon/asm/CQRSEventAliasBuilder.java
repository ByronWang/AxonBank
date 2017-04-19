package com.nebula.cqrs.axon.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Event;

public class CQRSEventAliasBuilder extends PojoBuilder {

	public static byte[] dump(Event event) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		Type type = event.type;
		Type typeSuper = event.realEvent;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, type.getInternalName(), null, typeSuper.getInternalName(), null);

		visitDefineInitWithAllFieldsToSuper(cw, type, typeSuper, event.fields);
		
		return cw.toByteArray();
	}
}
