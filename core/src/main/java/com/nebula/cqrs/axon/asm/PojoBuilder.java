package com.nebula.cqrs.axon.asm;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Field;

public class PojoBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type objectType, List<Field> fields) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, objectType.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(objectType.getClassName(), null);

		for (Field field : fields) {
			visitDefineField(cw, field);
			visitDefinePropetyGet(cw, objectType, field);
		}
		visitDefine_init_withNothing(cw, objectType);
		visitDefine_init_withAllFields(cw, objectType, fields);
		visitDefine_toString_withAllFields(cw, objectType, fields);
		return cw.toByteArray();
	}

}