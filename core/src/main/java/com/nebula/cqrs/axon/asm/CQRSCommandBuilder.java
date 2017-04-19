package com.nebula.cqrs.axon.asm;

import java.util.List;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.Field;

public class CQRSCommandBuilder extends PojoBuilder {

	public static byte[] dump(Command command) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		Type objectType = command.type;
		List<Field> objectFields = command.fields;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, objectType.getInternalName(), null, "java/lang/Object", null);

		visitDefineField(cw, objectFields.get(0), TargetAggregateIdentifier.class);
		visitDefinePropetyGet(cw, objectType, objectFields.get(0));

		for (int i = 1; i < objectFields.size(); i++) {
			visitDefineField(cw, objectFields.get(i));
			visitDefinePropetyGet(cw, objectType, objectFields.get(i));
		}

		visitDefine_init_withAllFields(cw, objectType, objectFields);
		
		visitDefine_toString_WithAllFields(cw, objectType, objectFields);

		return cw.toByteArray();
	}
}
