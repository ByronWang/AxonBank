package com.nebula.tinyasm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.api.ClassMethodCode;
import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.ClassUtils;
import com.nebula.tinyasm.util.Field;

public class ClassBuilder extends ClassVisitor implements Opcodes, Types, ClassBody {
	final static int THIS = 0;
	final static String THIS_NAME = "this";

	static public ClassBody make(ClassVisitor cv, Type objectType) {
		return make(ACC_PUBLIC + ACC_SUPER, cv, objectType, Type.getType(Object.class));
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Type superType) {
		return new ClassBuilder(ACC_PUBLIC + ACC_SUPER, cv, objectType, superType);
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Type interfaceType, Type interfaceSignature) {
		return new ClassBuilder(ACC_PUBLIC + ACC_SUPER, cv, objectType, Type.getType(Object.class), interfaceType, interfaceSignature);
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Class<?> interfaceClass, Type interfaceSignature) {
		return new ClassBuilder(ACC_PUBLIC + ACC_SUPER, cv, objectType, Type.getType(Object.class), Type.getType(interfaceClass), interfaceSignature);
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Type superType, Type interfaceType, Type interfaceSignature) {
		return new ClassBuilder(ACC_PUBLIC + ACC_SUPER, cv, objectType, superType, interfaceType, interfaceSignature);
	}

	static public ClassBody make(final int access, ClassVisitor cv, Type objectType) {
		return make(access, cv, objectType, Type.getType(Object.class));
	}

	static public ClassBody make(final int access, ClassVisitor cv, Type objectType, Type superType) {
		return new ClassBuilder(access, cv, objectType, superType);
	}

	static public ClassBody make(final int access, ClassVisitor cv, Type objectType, Type superType, Type interfaceType, Type interfaceSignature) {
		return new ClassBuilder(access, cv, objectType, superType, interfaceType, interfaceSignature);
	}

	static public ClassBody make(final int access, Type objectType, Class<?> superClass, Type interfaceType, Type interfaceSignature) {
		return make(access, objectType, Type.getType(superClass), interfaceType, interfaceSignature);
	}

	static public ClassBody make(final int access, Type objectType, Class<?> superClass, Class<?> interfaceClass, Type interfaceSignature) {
		return make(access, objectType, Type.getType(superClass), Type.getType(interfaceClass), interfaceSignature);
	}

	static public ClassBody make(final int access, Type objectType, Type superType, Type interfaceType, Type interfaceSignature) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		return new ClassBuilder(access, classWriter, objectType, superType, interfaceType, interfaceSignature);
	}

	static public ClassBody make(final int access, Type objectType) {
		return make(access, objectType, Type.getType(Object.class));
	}

	static public ClassBody make(final int access, Type objectType, Type superType) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		return new ClassBuilder(access, classWriter, objectType, superType);
	}

	static public ClassBody make(Type objectType) {
		return make(ACC_PUBLIC + ACC_SUPER, objectType, Type.getType(Object.class));
	}

	static public ClassBody make(Type objectType, Type superType) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		return new ClassBuilder(ACC_PUBLIC + ACC_SUPER, classWriter, objectType, superType);
	}

	private List<Field> fieldsList = new ArrayList<>();

	private Map<String, Field> fieldsMap = new HashMap<>();

	boolean hadEnd = false;

	private final Type superType;

	private final Type thisType;

	ClassBuilder(final int access, ClassVisitor cv, Type thisType, Type superType) {
		super(Opcodes.ASM5);
		this.cv = cv;
		this.thisType = thisType;
		this.superType = superType;

		cv.visit(52, access, thisType.getInternalName(), null, superType.getInternalName(), null);
		String simpleName = ClassUtils.toSimpleName(this.thisType.getClassName());
		cv.visitSource(simpleName + ".java", null);
	}

	ClassBuilder(final int access, ClassVisitor cv, Type thisType, Type superType, Type interfaceType, Type interfaceSignatures) {
		super(Opcodes.ASM5);
		this.cv = cv;
		this.thisType = thisType;
		this.superType = superType;

		cv.visit(52, access, thisType.getInternalName(), superType.getDescriptor() + signatureOf(interfaceType, interfaceSignatures),
		        superType.getInternalName(), new String[] { interfaceType.getInternalName() });
		String simpleName = ClassUtils.toSimpleName(this.thisType.getClassName());
		cv.visitSource(simpleName + ".java", null);
	}

	protected void addField(Field field) {
		fieldsMap.put(field.name, field);
		fieldsList.add(field);
	}

	@Override
	public ClassBody annotation(Type annotationType, Object value) {
		AsmBuilder.visitAnnotation(cv, annotationType, value);
		return this;
	}

	@Override
	public ClassBody end() {
		cv.visitEnd();
		hadEnd = true;
		return this;
	}

	@Override
	public ClassBody field(int access, String fieldName, Type fieldType) {
		addField(new Field(fieldName, fieldType));
		AsmBuilder.visitDefineField(cv, access, fieldName, fieldType);
		return this;
	}

	@Override
	public ClassBody field(int access, String fieldName, Type fieldType, String signature) {
		addField(new ClassField(access, fieldName, fieldType, signature));
		AsmBuilder.visitDefineField(cv, access, fieldName, fieldType, signature);
		return this;
	}

	@Override
	public ClassBody field(int access, Type annotationType, Object value, String fieldName, Type fieldType) {
		addField(new ClassField(access, fieldName, fieldType, null));
		AsmBuilder.visitDefineField(cv, access, fieldName, fieldType, annotationType, value);
		return this;
	}

	@Override
	public ClassBody field(int access, Type annotationType, Object value, String fieldName, Type fieldType, String signature) {
		addField(new ClassField(access, fieldName, fieldType, signature));
		AsmBuilder.visitDefineField(cv, access, fieldName, fieldType, signature, annotationType, value);
		return this;
	}

	public Field fieldOf(String fieldName) {
		return fieldsMap.get(fieldName);
	}

	@Override
	public List<Field> getFields() {
		return this.fieldsList;
	}

	@Override
	public Type getSuperType() {
		return superType;
	}

	@Override
	public Type getType() {
		return thisType;
	}

	@Override
	public MethodHeader<ClassMethodCode> method(int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		return new ClassMethodVisitor(this, thisType, access, returnType, methodName, exceptionClasses);
	}

	@Override
	public Type referInnerClass(String name) {
		String internalName = thisType.getInternalName() + "$" + name;

		cv.visitInnerClass(internalName, thisType.getInternalName(), name, 0);

		return Type.getType("L" + internalName + ";");
	}

	@Override
	public byte[] toByteArray() {
		if (!hadEnd) {
			end();
		}
		if (cv instanceof ClassWriter) {
			return ((ClassWriter) cv).toByteArray();
		}
		return null;
	}

	@Override
	public ClassVisitor visitor() {
		return cv;
	}
}