package com.nebula.cqrs.axon.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.axon.pojo.Field;
import com.nebula.cqrs.axon.pojo.PojoBuilder;

public class CQRSEventRealBuilder extends PojoBuilder {

	public static byte[] dump(Event target) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		Type type = target.type;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, type.getInternalName(), null, "java/lang/Object", null);

		for (Field field : target.fields) {
			visitDefineField(cw, field);
			visitDefinePropertyGet(cw, type, field);
		}

		visitDefine_init_withAllFields(cw, type, target.fields);
		visitDefine_toString_withAllFields(cw, type, target.fields);
		return cw.toByteArray();
	}
}
