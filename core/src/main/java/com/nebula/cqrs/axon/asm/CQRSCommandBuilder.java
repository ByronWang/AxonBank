package com.nebula.cqrs.axon.asm;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.Field;
import com.nebula.cqrs.axon.pojo.PojoBuilder;

public class CQRSCommandBuilder extends PojoBuilder {

	public static byte[] dump(Command command) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		Type objectType = command.type;
		Field[] objectFields = command.fields;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, objectType.getInternalName(), null, "java/lang/Object", null);

		visitDefineField(cw, objectFields[0], TargetAggregateIdentifier.class);
		visitDefinePropertyGet(cw, objectType, objectFields[0]);

		for (int i = 1; i < objectFields.length; i++) {
			visitDefineField(cw, objectFields[i]);
			visitDefinePropertyGet(cw, objectType, objectFields[i]);
		}

		visitDefine_init_withAllFields(cw, objectType, objectFields);

		visitDefine_toString_withAllFields(cw, objectType, objectFields);

		return cw.toByteArray();
	}
}
