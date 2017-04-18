package com.nebula.cqrs.axon;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Event;
import com.nebula.cqrs.axon.CQRSDomainBuilder.Field;

public class CQRSEventRealBuilder implements Opcodes {

	public static byte[] dump(Event target) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		Type type = target.type;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, type.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(type.getClassName(), null);
		
		for (Field field : target.fields) {
			PojoBuilder.visitDefine_field(cw, type, field);
			PojoBuilder.visitDefiine_getField(cw, type, field);
		}
		
		PojoBuilder.visitDefine_init(cw, type, target.fields);
		PojoBuilder.visitDefine_toString(cw, type, target.fields);
		return cw.toByteArray();
	}
}
