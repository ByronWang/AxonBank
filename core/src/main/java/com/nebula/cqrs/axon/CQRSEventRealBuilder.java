package com.nebula.cqrs.axon;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
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

		PojoBuilder.visit_fields(cw, type, target.fields);
		PojoBuilder.visit_getField(cw, type, target.fields);
		PojoBuilder.visit_init(cw, type, target.fields);
		PojoBuilder.visit_toString(cw, type, target.fields);
		return cw.toByteArray();
	}
}
