package com.nebula.cqrs.axon.asm;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.Field;

public class CQRSCommandBuilder extends PojoBuilder {

	public static byte[] dump(Command target) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		Type type = target.type;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, type.getInternalName(), null, "java/lang/Object", null);

		AsmBuilder.define_field(cw, target.fields.get(0), TargetAggregateIdentifier.class);

		for (int i = 1; i < target.fields.size(); i++) {
			AsmBuilder.define_field(cw, target.fields.get(i));
		}

		for (Field field : target.fields) {
			AsmBuilder.define_getField(cw, type, field);
		}

		define_init_allfield(cw, type, target.fields);
		define_toString_allfield(cw, type, target.fields);

		return cw.toByteArray();
	}
}
