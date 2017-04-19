package com.nebula.cqrs.axon.asm;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.Field;
import com.nebula.cqrs.core.asm.AsmBuilder;

public class AxonAsmBuilder extends AsmBuilder {

	public static void visitDefine_init_withAllFields(ClassWriter cw, Type objectType, List<Field> fields) {
		MethodVisitor mv;
		{
			Type[] params = new Type[fields.size()];

			for (int i = 0; i < fields.size(); i++) {
				params[i] = fields.get(i).type;
			}

			String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, params);

			mv = cw.visitMethod(ACC_PUBLIC, "<init>", methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);

			visitInitObject(mv, 0);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(field.type.getOpcode(ILOAD), i + 1);
				mv.visitFieldInsn(PUTFIELD, objectType.getInternalName(), field.name, field.type.getDescriptor());
			}

			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l1, 0);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l1, i + 1);
			}
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}
		cw.visitEnd();
	}

	public static void visitDefine_init_withAllFieldsToSuper(ClassWriter cw, Type objectType, Type superType, List<Field> fields) {
		MethodVisitor mv;
		{
			Type[] params = new Type[fields.size()];

			for (int i = 0; i < fields.size(); i++) {
				params[i] = fields.get(i).type;
			}

			String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, params);
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			{
				mv.visitVarInsn(ALOAD, 0);

				for (int i = 0; i < fields.size(); i++) {
					Field field = fields.get(i);
					mv.visitVarInsn(field.type.getOpcode(ILOAD), i + 1);
				}
				mv.visitMethodInsn(INVOKESPECIAL, superType.getInternalName(), "<init>", methodDescriptor, false);

				mv.visitInsn(RETURN);
			}
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l2, 0);
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l2, i + 1);
			}
			mv.visitMaxs(4, 4);
			mv.visitEnd();

		}
	}

	public static void visitDefine_toString_withAllFields(ClassWriter cw, Type objectType, List<Field> fields) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
			mv.visitInsn(DUP);
			mv.visitLdcInsn(CQRSDomainBuilder.toSimpleName(objectType.getClassName()) + "(");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
			{
				for (int i = 0; i < fields.size(); i++) {
					Field field = fields.get(i);
					if (i != 0) {
						mv.visitLdcInsn(",");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
					}

					mv.visitLdcInsn(field.name + "=");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

					mv.visitVarInsn(ALOAD, 0);
					mv.visitMethodInsn(INVOKEVIRTUAL, objectType.getInternalName(), toPropertyGetName(field.name), Type.getMethodDescriptor(field.type), false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
							Type.getMethodDescriptor(Type.getObjectType("java/lang/StringBuilder"), field.type), false);

				}
			}

			mv.visitLdcInsn(")");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(3, 1);
			mv.visitEnd();
		}
	}

	public static void visitDefineField(ClassWriter cw, Field field, Class<?>... annotations) {
		visitDefineField(cw, field.name, field.type, annotations);
	}

	public static void visitDefinePropetyGet(ClassWriter cw, Type objectType, Field field) {
		visitDefinePropetyGet(cw, objectType, field.name, field.type);
	}

	public static void visitGetProperty(MethodVisitor mv, int objectIndex, Type objectType, Field field) {
		visitGetProperty(mv, objectIndex, objectType, field.name, field.type);
	}

	public static void visitGetProperty(MethodVisitor mv, Type objectType, Field field) {
		visitGetProperty(mv, objectType, field.name, field.type);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Type objectType, Field... fields) {
		Type[] params = new Type[fields.length];
		for (int i = 0; i < fields.length; i++) {
			params[i] = fields[i].type;
		}
		visitInitTypeWithAllFields(mv, objectType, params);
	}

	public static void visitInitTypeWithAllFields(MethodVisitor mv, Type objectType, List<Field> fields) {
		Type[] params = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			params[i] = fields.get(i).type;
		}
		visitInitTypeWithAllFields(mv, objectType, params);
	}

	public static void visitPutField(MethodVisitor mv, int objectIndex, Type objectType, int dataIndex, Field field) {
		visitPutField(mv, objectIndex, objectType, dataIndex, field.name, field.type);
	}

}
