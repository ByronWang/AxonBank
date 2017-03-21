package com.nebula.dropwizard.core;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class DomainMaker extends ClassVisitor {
	Type type;

	public DomainMaker(int api, ClassVisitor cv) {
		super(api, cv);
	}

	public DomainMaker(int api) {
		super(api);
	}

	String typeDescriptor;

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		typeDescriptor = name.replace('.', '/');
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public FieldVisitor visitField(int access, String fieldName, String desc, String signature, Object value) {
		//
		MethodVisitor mv = null;
		Type fieldType = Type.getType(desc);

		{
			String methodDescriptor = Type.getMethodDescriptor(fieldType, new Type[] {});
			mv = super.visitMethod(ACC_PUBLIC, "get" + toBeanProperties(fieldName), methodDescriptor, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, typeDescriptor, "completed", desc);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 1);
			mv.visitEnd();
		}

		{
			String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { fieldType });
			mv = super.visitMethod(ACC_PUBLIC, "set" + toBeanProperties(fieldName), methodDescriptor, null, null);
			mv.visitParameter(fieldName, 0);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, typeDescriptor, fieldName, desc);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 1);
			mv.visitEnd();
		}

		FieldVisitor fv = super.visitField(access, fieldName, desc, signature, value);
		return fv;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		TypeMakerMethodVisitor methodVisitor = new TypeMakerMethodVisitor(api, mv);
		return methodVisitor;
	}

	class TypeMakerMethodVisitor extends MethodVisitor {
		public TypeMakerMethodVisitor(int api, MethodVisitor mv) {
			super(api, mv);
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			if (opcode == Opcodes.PUTFIELD) {
				String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getMethodType(desc) });
				super.visitMethodInsn(INVOKEVIRTUAL, owner, "set" + toBeanProperties(name), methodDescriptor, false);
			} else if (opcode == Opcodes.GETFIELD) {
				String methodDescriptor = Type.getMethodDescriptor(Type.getMethodType(desc), new Type[] {});
				mv.visitMethodInsn(INVOKEVIRTUAL, owner, "get" + toBeanProperties(name), methodDescriptor, false);
			}
		}

	}

	static String toBeanProperties(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

}
