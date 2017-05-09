package com.nebula.tinyasm;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.asm.ClassUtils;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.api.ClassMethodCode;
import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.Field;

public class ClassBuilder extends ClassVisitor implements Opcodes, ClassBody {
	final static int THIS = 0;
	final static String THIS_NAME = "this";
	private Map<String, Field> fields = new HashMap<>();

	private final Type thisType;

	ClassBuilder(ClassVisitor cv, Type thisType, Type superType) {
		super(Opcodes.ASM5);
		this.cv = cv;
		this.thisType = thisType;

		cv.visit(52, ACC_PUBLIC + ACC_SUPER, thisType.getInternalName(), null, superType.getInternalName(), null);
		String simpleName = ClassUtils.toSimpleName(this.thisType.getClassName());
		cv.visitSource(simpleName + ".java", null);
	}

	@Override
	public ClassBody annotation(Type annotationType, Object value) {
		AsmBuilder.visitAnnotation(cv, annotationType, value);
		return this;
	}

	@Override
	public void end() {
		cv.visitEnd();
	}

	@Override
	public ClassBody field(String fieldName, Type fieldType) {
		fields.put(fieldName, new Field(fieldName, fieldType));
		AsmBuilder.visitDefineField(cv, fieldName, fieldType);
		return this;
	}

	@Override
	public ClassBody field(String fieldName, Type fieldType, String signature) {
		fields.put(fieldName, new ClassField(fieldName, fieldType, signature));
		AsmBuilder.visitDefineField(cv, fieldName, fieldType, signature);
		return this;
	}

	@Override
	public ClassBody field(Type annotationType, Object value, String fieldName, Type fieldType) {
		fields.put(fieldName, new ClassField(fieldName, fieldType));
		AsmBuilder.visitDefineField(cv, fieldName, fieldType, annotationType, value);
		return this;
	}

	@Override
	public ClassBody field(Type annotationType, Object value, String fieldName, Type fieldType, String signature) {
		fields.put(fieldName, new ClassField(fieldName, fieldType, signature));
		AsmBuilder.visitDefineField(cv, fieldName, fieldType, signature, annotationType, value);
		return this;
	}

	public Field fieldOf(String fieldName) {
		return fields.get(fieldName);
	}

	@Override
	public Type referInnerClass(String name) {
		String internalName = thisType.getInternalName() + "$" + name;

		cv.visitInnerClass(internalName, thisType.getInternalName(), name, 0);

		return Type.getType("L" + internalName + ";");
	}

	@Override
	public MethodHeader<ClassMethodCode> method(int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		return new ClassMethodVisitor(this, thisType, access, returnType, methodName, exceptionClasses);
	}

	@Override
	public ClassVisitor visitor() {
		return cv;
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Type superType) {
		return new ClassBuilder(cv, objectType, superType);
	}

	static public ClassBody make(ClassVisitor cv, Type objectType) {
		return make(cv, objectType, Type.getType(Object.class));
	}

	static public ClassBody make(Type objectType, Type superType) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		return new ClassBuilder(classWriter, objectType, superType);
	}

	static public ClassBody make(Type objectType) {
		return make(objectType, Type.getType(Object.class));
	}

	@Override
	public byte[] toByteArray() {
		if(cv instanceof ClassWriter){
			return ((ClassWriter) cv).toByteArray();
		}
		return null;
	}
}
