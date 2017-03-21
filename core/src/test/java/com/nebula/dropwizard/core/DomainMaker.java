package com.nebula.dropwizard.core;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class DomainMaker extends ClassVisitor {
	Type type;

	public DomainMaker(int api, ClassVisitor cv) {
		super(api, cv);
	}

	public DomainMaker(int api, String clzNewName) {
		super(api);
	}

	String typeDescriptor;

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		typeDescriptor = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public FieldVisitor visitField(int access, String fieldName, String desc, String signature, Object value) {

		FieldVisitor fv = super.visitField(access, fieldName, desc, signature, value);

		//
		MethodVisitor mv = null;
		Type fieldType = Type.getType(desc);
		if (fieldName.equals("id")) {
			return fv;
		}
		String methodDescriptor = Type.getMethodDescriptor(fieldType, new Type[] {});
		{
			mv = super.visitMethod(ACC_PUBLIC, "get" + toBeanProperties(fieldName), methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(35, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeDescriptor, fieldName, desc);
			mv.visitInsn(fieldType.getOpcode(IRETURN));
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "L" + typeDescriptor + ";", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}

		// {
		// String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE,
		// new Type[] { fieldType });
		// mv = super.visitMethod(ACC_PUBLIC, "set" +
		// toBeanProperties(fieldName), methodDescriptor, null, null);
		// mv.visitParameter(fieldName, 0);
		// mv.visitCode();
		// Label l0 = new Label();
		// mv.visitLabel(l0);
		// mv.visitLineNumber(31, l0);
		// mv.visitVarInsn(ALOAD, 0);
		// mv.visitVarInsn(fieldType.getOpcode(ILOAD), 1);
		// mv.visitFieldInsn(PUTFIELD, typeDescriptor, fieldName, desc);
		// Label l1 = new Label();
		// mv.visitLabel(l1);
		// mv.visitLineNumber(32, l1);
		// mv.visitInsn(RETURN);
		// Label l2 = new Label();
		// mv.visitLabel(l2);
		// mv.visitLocalVariable("this", "L" + typeDescriptor + ";", null, l0,
		// l2, 0);
		// mv.visitLocalVariable(fieldName, desc, null, l0, l2, 1);
		// mv.visitMaxs(2, 2);
		// mv.visitEnd();
		// }

		return fv;
	}

	// @Override
	// public MethodVisitor visitMethod(int access, String name, String desc,
	// String signature, String[] exceptions) {
	// MethodVisitor mv = super.visitMethod(access, name, desc, signature,
	// exceptions);
	// TypeMakerMethodVisitor methodVisitor = new TypeMakerMethodVisitor(api,
	// mv);
	// return methodVisitor;
	// }
	//
	// class TypeMakerMethodVisitor extends MethodVisitor {
	// public TypeMakerMethodVisitor(int api, MethodVisitor mv) {
	// super(api, mv);
	// }
	//
	// @Override
	// public void visitFieldInsn(int opcode, String owner, String name, String
	// desc) {
	// if (opcode == PUTFIELD) {
	// String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, new
	// Type[] { Type.getMethodType(desc) });
	// super.visitMethodInsn(INVOKEVIRTUAL, owner, "set" +
	// toBeanProperties(name), methodDescriptor, false);
	// } else if (opcode == GETFIELD) {
	// String methodDescriptor =
	// Type.getMethodDescriptor(Type.getMethodType(desc), new Type[] {});
	// mv.visitMethodInsn(INVOKEVIRTUAL, owner, "get" + toBeanProperties(name),
	// methodDescriptor, false);
	// }
	// }
	// }

	static String toBeanProperties(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

}
