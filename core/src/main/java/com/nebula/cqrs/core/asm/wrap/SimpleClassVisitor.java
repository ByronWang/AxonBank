package com.nebula.cqrs.core.asm.wrap;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.asm.ClassUtils;
import com.nebula.cqrs.core.asm.ASMBuilder;
import com.nebula.cqrs.core.asm.Field;

public class SimpleClassVisitor extends ClassVisitor implements Opcodes, ClassBody {
	final static int THIS = 0;
	final static String THIS_NAME = "this";
	private final Type thisType;

	private Map<String, Field> fields = new HashMap<>();

	public SimpleClassVisitor(ClassVisitor cv, Type thisType) {
		this(cv, thisType, Type.getType(Object.class));
	}

	public Field fieldOf(String fieldName) {
		return fields.get(fieldName);
	}

	public SimpleClassVisitor(ClassVisitor cv, Type thisType, Type superType) {
		super(Opcodes.ASM5);
		this.cv = cv;
		this.thisType = thisType;

		cv.visit(52, ACC_PUBLIC + ACC_SUPER, thisType.getInternalName(), null, superType.getInternalName(), null);
		String simpleName = ClassUtils.toSimpleName(this.thisType.getClassName());
		cv.visitSource(simpleName + ".java", null);
	}

	@Override
	public ClassBody annotation(Type annotationType, Object value) {
		ASMBuilder.visitAnnotation(cv, annotationType, value);
		return this;
	}

	@Override
	public void end() {
		cv.visitEnd();
	}

	@Override
	public MethodHeader<ClassMethodCode> method(int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		return new ClassMethodVisitor(this, thisType, access, returnType, methodName, exceptionClasses);
	}

	@Override
	public ClassVisitor visitor() {
		return cv;
	}

	@Override
	public ClassBody field(String fieldName, Type fieldType) {
		fields.put(fieldName, new Field(fieldName, fieldType));
		ASMBuilder.visitDefineField(cv, fieldName, fieldType);
		return this;
	}

	@Override
	public ClassBody field(String fieldName, Type fieldType, String signature) {
		fields.put(fieldName, new ClassField(fieldName, fieldType, signature));
		ASMBuilder.visitDefineField(cv, fieldName, fieldType, signature);
		return this;
	}

	@Override
	public ClassBody field(Type annotationType, Object value, String fieldName, Type fieldType) {
		fields.put(fieldName, new ClassField(fieldName, fieldType));
		ASMBuilder.visitDefineField(cv, fieldName, fieldType, annotationType, value);
		return this;
	}

	@Override
	public ClassBody field(Type annotationType, Object value, String fieldName, Type fieldType, String signature) {
		fields.put(fieldName, new ClassField(fieldName, fieldType, signature));
		ASMBuilder.visitDefineField(cv, fieldName, fieldType, signature, annotationType, value);
		return this;
	}

}
