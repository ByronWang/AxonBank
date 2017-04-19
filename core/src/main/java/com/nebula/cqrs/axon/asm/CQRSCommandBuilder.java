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

		visitDefineField(cw, target.fields.get(0), TargetAggregateIdentifier.class);

		for (int i = 1; i < target.fields.size(); i++) {
			visitDefineField(cw, target.fields.get(i));
		}

		for (Field field : target.fields) {
			visitDefinePropetyGet(cw, type, field);
		}

		visitDefineInitWithAllFields(cw, type, target.fields);
		visitDefineToStringWithAllFields(cw, type, target.fields);

		return cw.toByteArray();
	}
}
