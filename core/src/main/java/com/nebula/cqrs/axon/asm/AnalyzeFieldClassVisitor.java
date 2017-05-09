package com.nebula.cqrs.axon.asm;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public class AnalyzeFieldClassVisitor extends ClassVisitor {
	private final List<Field> fields = new ArrayList<>();
	private Field lastIdentifierField;

	public void setKeyField(Field field) {
		if (lastIdentifierField != null && lastIdentifierField != field) {
			lastIdentifierField.identifier = false;
		}

		field.identifier = true;
		this.lastIdentifierField = field;
	}

	public AnalyzeFieldClassVisitor(ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		Field field = new Field(name, Type.getType(desc));
		if (fields.size() == 0) {
			this.setKeyField(field);
		}
		fields.add(field);
		FieldVisitor fieldVisitor = super.visitField(access, name, desc, signature, value);
		return new CustomFieldVisitor(api, fieldVisitor, field, access, name, desc, signature, value);
	}

	class CustomFieldVisitor extends FieldVisitor {
		Field field;

		public CustomFieldVisitor(int api, FieldVisitor fv, Field field, int access, String name, String desc, String signature, Object value) {
			super(api, fv);
			this.field = field;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			Type type = Type.getType(desc);
			if (type.getInternalName() == Type.getType(AggregateIdentifier.class).getInternalName()) {
				AnalyzeFieldClassVisitor.this.setKeyField(field);
			}
			return super.visitAnnotation(desc, visible);
		}
	}

	public List<Field> finished() {
		return fields;
	}
}
