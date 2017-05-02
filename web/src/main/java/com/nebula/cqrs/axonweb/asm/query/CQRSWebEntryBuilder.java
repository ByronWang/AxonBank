package com.nebula.cqrs.axonweb.asm.query;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Field;

public class CQRSWebEntryBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type objectType, Field[] objectFields) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, objectType.getInternalName(), null, "java/lang/Object", null);

		visitAnnotation(cw, Entity.class);

		visitDefineField(cw, "id", Type.getType(long.class),Id.class, GeneratedValue.class);
		visitDefinePropertyGet(cw, objectType, "id", Type.getType(long.class));
		visitDefinePropertySet(cw, objectType, "id", Type.getType(long.class));

		for (Field field : objectFields) {
			visitDefineField(cw,field);
			visitDefinePropertyGet(cw, objectType, field);
			visitDefinePropertySet(cw, objectType, field);
		}

		visitDefine_init_withNothing(cw, objectType);
		
		visitDefine_init_withAllFields(cw, objectType, objectFields);

		visitDefine_toString_withAllFields(cw, objectType, objectFields);

		cw.visitEnd();

		return cw.toByteArray();
	}
}
