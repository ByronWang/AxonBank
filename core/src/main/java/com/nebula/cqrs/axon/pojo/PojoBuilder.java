package com.nebula.cqrs.axon.pojo;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.Field;

public class PojoBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type objectType, Field[] fields) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, objectType.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(objectType.getClassName(), null);

		for (Field field : fields) {
			visitDefineField(cw, field);
			visitDefinePropertyGet(cw, objectType, field);
		}
		visitDefine_init_withNothing(cw, objectType);
		visitDefine_init_withAllFields(cw, objectType, fields);
		visitDefine_toString_withAllFields(cw, objectType, fields);
		return cw.toByteArray();
	}

}