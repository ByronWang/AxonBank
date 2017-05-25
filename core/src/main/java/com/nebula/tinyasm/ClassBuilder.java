package com.nebula.tinyasm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.api.ClassMethodCode;
import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.api.StaticMethodCode;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.ClassUtils;
import com.nebula.tinyasm.util.Field;

public class ClassBuilder extends ClassVisitor implements Opcodes, Types, ClassBody {
	final static int THIS = 0;

	final static String THIS_NAME = "this";

	static public ClassBody make() {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		return new ClassBuilder(classWriter);
	}

	static public ClassBody make(ClassVisitor cv, Type objectType) {
		return make(ACC_PUBLIC + ACC_SUPER, cv, objectType, Type.getType(Object.class));
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Type superType) {
		return new ClassBuilder(ACC_PUBLIC + ACC_SUPER, cv, objectType, superType);
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Type superType, Class<?> interfaceClass, Type[] interfaceSignature) {
		return make(cv, objectType, superType, Type.getType(interfaceClass), interfaceSignature);
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Type superType, Type interfaceType, Type[] interfaceSignature) {
		return new ClassBuilder(ACC_PUBLIC + ACC_SUPER, cv, objectType, superType, interfaceType, interfaceSignature);
	}

	static public ClassBody make(ClassVisitor cv, Type objectType, Type superType, Type[] superTypeSignature) {
		return new ClassBuilder(ACC_PUBLIC + ACC_SUPER, cv, objectType, superType, superTypeSignature);
	}

	static public ClassBody make(final int access, ClassVisitor cv, Type objectType) {
		return make(access, cv, objectType, Type.getType(Object.class));
	}

	static public ClassBody make(final int access, ClassVisitor cv, Type objectType, Type superType) {
		return new ClassBuilder(access, cv, objectType, superType);
	}

	static public ClassBody make(final int access, ClassVisitor cv, Type objectType, Type superType, Type interfaceType, Type[] interfaceSignature) {
		return new ClassBuilder(access, cv, objectType, superType, interfaceType, interfaceSignature);
	}

	static public ClassBody make(final int access, Type objectType) {
		return make(access, objectType, Type.getType(Object.class));
	}

	static public ClassBody make(final int access, Type objectType, Class<?> superClass, Class<?> interfaceClass, Type[] interfaceSignature) {
		return make(access, objectType, Type.getType(superClass), Type.getType(interfaceClass), interfaceSignature);
	}

	static public ClassBody make(final int access, Type objectType, Class<?> superClass, Type interfaceType, Type[] interfaceSignature) {
		return make(access, objectType, Type.getType(superClass), interfaceType, interfaceSignature);
	}

	static public ClassBody make(final int access, Type objectType, Type superType) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		return new ClassBuilder(access, classWriter, objectType, superType);
	}

	static public ClassBody make(final int access, Type objectType, Type superType, Type interfaceType, Type[] interfaceSignature) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		return new ClassBuilder(access, classWriter, objectType, superType, interfaceType, interfaceSignature);
	}

	static public ClassBody make(final int access, Type objectType, Type superType, Type[] superTypeSignature) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		return new ClassBuilder(access, classWriter, objectType, superType, superTypeSignature);
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

	private Type superType;

	private Type thisType;

	protected ClassBuilder() {
		super(Opcodes.ASM5, new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS));
	}

	protected ClassBuilder(ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
	}

	ClassBuilder(final int access, ClassVisitor cv, Type thisType, Type superType) {
		super(Opcodes.ASM5, cv);
		initType(thisType, superType);
		cv.visit(52, access, thisType.getInternalName(), null, superType.getInternalName(), null);
		cv.visitSource(ClassUtils.toSimpleName(this.thisType.getClassName()) + ".java", null);
	}

	ClassBuilder(final int access, ClassVisitor cv, Type thisType, Type superType, Type interfaceType, Type[] interfaceSignatures) {
		super(Opcodes.ASM5, cv);
		initType(thisType, superType);

		cv.visit(52, access, thisType.getInternalName(), superType.getDescriptor() + signatureOf(interfaceType, interfaceSignatures),
		        superType.getInternalName(), new String[] { interfaceType.getInternalName() });
		cv.visitSource(ClassUtils.toSimpleName(this.thisType.getClassName()) + ".java", null);
	}

	ClassBuilder(final int access, ClassVisitor cv, Type thisType, Type superType, Type[] superTypeSignatures) {
		super(Opcodes.ASM5, cv);
		initType(thisType, superType);

		cv.visit(52, access, thisType.getInternalName(), signatureOf(superType, superTypeSignatures), superType.getInternalName(), null);
		cv.visitSource(ClassUtils.toSimpleName(this.thisType.getClassName()) + ".java", null);
	}

	protected void addField(Field field) {
		if (!fieldsMap.containsKey(field.name)) {
			fieldsMap.put(field.name, field);
			fieldsList.add(field);
		}
	}

	@Override
	public ClassBody annotation(Type annotationType, Object annotationValue) {
		AsmBuilder.visitAnnotation(cv, annotationType, annotationValue);
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
		if (fieldsMap.containsKey(fieldName)) return this;

		addField(new Field(fieldName, fieldType));
		AsmBuilder.visitDefineField(cv, access, fieldName, fieldType);
		return this;
	}

	@Override
	public ClassBody field(int access, String fieldName, Type fieldType, String signature) {
		if (fieldsMap.containsKey(fieldName)) return this;
		addField(new ClassField(access, fieldName, fieldType, signature, null));
		AsmBuilder.visitDefineField(cv, access, fieldName, fieldType, signature);
		return this;
	}

	@Override
	public ClassBody field(int access, Type annotationType, Object annotationValue, String fieldName, Type fieldType) {
		if (fieldsMap.containsKey(fieldName)) return this;
		addField(new ClassField(access, fieldName, fieldType, null, null));
		AsmBuilder.visitDefineField(cv, access, fieldName, fieldType, annotationType, annotationValue);
		return this;
	}

	@Override
	public ClassBody field(int access, Type annotationType, Object annotationValue, String fieldName, Type fieldType, String signature) {
		if (fieldsMap.containsKey(fieldName)) return this;
		addField(new ClassField(access, fieldName, fieldType, signature, null));
		AsmBuilder.visitDefineField(cv, access, fieldName, fieldType, signature, annotationType, annotationValue);
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

	private void initType(Type thisType, Type superType) {
		this.thisType = thisType;
		this.superType = superType;
	}

	// @Override
	// public MethodHeader<ClassMethodCode> method(int access, String name,
	// String desc, String signature, String[] exceptions) {
	// Type returnType = Type.getReturnType(desc);
	//
	// MethodHeader<ClassMethodCode> mh = new ClassMethodVisitor(this, thisType,
	// access, returnType, name, exceptions);
	//
	//
	// return new ClassMethodVisitor(this, thisType, access, returnType,
	// methodName, exceptions);
	// }

	@Override
	public MethodHeader<ClassMethodCode> method(int access, Type returnType, String methodName, String[] exceptions) {
		return new ClassMethodVisitor(this, thisType, access, returnType, methodName, exceptions);
	}

	@Override
	public Type referInnerClass(String name) {
		String internalName = thisType.getInternalName() + "$" + name;

		cv.visitInnerClass(internalName, thisType.getInternalName(), name, 0);

		return Type.getType("L" + internalName + ";");
	}

	@Override
	public MethodHeader<StaticMethodCode> staticMethod(int access, Type returnType, String methodName, String[] exceptionClasses) {
		return new StaticMethodVisitor(this, thisType, access, returnType, methodName, exceptionClasses);
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
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		initType(Type.getObjectType(name), Type.getObjectType(superName));
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
		hadEnd = true;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		this.addField(new ClassField(access, name, Type.getType(desc), signature, value));
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}

	@Override
	public ClassVisitor visitor() {
		return cv;
	}
}
