package com.nebula.cqrs.axon.asm;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Field;

public class PojoBuilder extends AsmBuilder {

	public static byte[] dump(Type pojoType, List<Field> fields) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, pojoType.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(pojoType.getClassName(), null);

		for (Field field : fields) {
			define_field (cw,field);
			define_getField(cw, pojoType, field);
		}
		define_init_nothing(cw, pojoType);
		define_init_allfield(cw, pojoType, fields);
		define_toString_allfield(cw, pojoType, fields);
		return cw.toByteArray();
	}

}