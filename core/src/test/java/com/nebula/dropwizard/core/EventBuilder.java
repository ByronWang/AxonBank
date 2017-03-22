package com.nebula.dropwizard.core;

import java.util.*;
import org.objectweb.asm.*;

import com.nebula.dropwizard.core.CQRSBuilder.Event;
import com.nebula.dropwizard.core.CQRSBuilder.Field;

public class EventBuilder implements Opcodes {

	public static byte[] dump(String packageName, Event event) {
		ClassWriter cw = new ClassWriter(0);
		String eventName = packageName + "." + event.name;
		Type type = Type.getObjectType(eventName.replace('.', '/'));

		cw.visit(52, ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, type.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource(type.getClassName(), null);

		visitFields(cw, type, event.data);
		visitGetField(cw, type, event.data);
		visitinit(cw, type, event.data);
		return cw.toByteArray();
	}

	public static void visitFields(ClassWriter cw, Type type, List<Field> fields) {
		for (Field field : fields) {
			FieldVisitor fv;
			{
				fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, field.name, field.type.getDescriptor(), null, null);
				fv.visitEnd();
			}
		}
	}

	public static String toGetName(String fieldName) {
		return "get" + toBeanProperties(fieldName);
	}

	static String toBeanProperties(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public static void visitGetField(ClassWriter cw, Type type, List<Field> fields) {
		MethodVisitor mv;

		for (Field field : fields) {
			String methodDescriptor = Type.getMethodDescriptor(field.type, new Type[] {});
			mv = cw.visitMethod(ACC_PUBLIC, toGetName(field.name), methodDescriptor, null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, type.getInternalName(), field.name, field.type.getDescriptor());
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
	}

	public static void visitinit(ClassWriter cw, Type type, List<Field> fields) {
		MethodVisitor mv;
		AnnotationVisitor av0;
		{
			Type[] params = new Type[fields.size()];

			for (int i = 0; i < fields.size(); i++) {
				params[i] = fields.get(i).type;
			}

			String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, params);

			mv = cw.visitMethod(ACC_PUBLIC, "<init>", methodDescriptor, null, null);
			{
				av0 = mv.visitAnnotation("Ljava/beans/ConstructorProperties;", true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					for (Field field : fields) {
						av1.visit(null, field.name);
					}
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, i + 1);
				mv.visitFieldInsn(PUTFIELD, type.getInternalName(), field.name, field.type.getDescriptor());
			}

			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", type.getDescriptor(), null, l0, l1, 0);

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, l0, l1, i + 1);
			}
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}
		cw.visitEnd();
	}

	// public void visitEquals(ClassWriter cw) {
	// MethodVisitor mv;
	// {
	// mv = cw.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null,
	// null);
	// mv.visitCode();
	// Label l0 = new Label();
	// mv.visitLabel(l0);
	// mv.visitLineNumber(22, l0);
	// mv.visitVarInsn(ALOAD, 1);
	// mv.visitVarInsn(ALOAD, 0);
	// Label l1 = new Label();
	// mv.visitJumpInsn(IF_ACMPNE, l1);
	// mv.visitInsn(ICONST_1);
	// mv.visitInsn(IRETURN);
	// mv.visitLabel(l1);
	// mv.visitFrame(Opcodes.F_NEW, 2,
	// new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object" }, 0, new Object[] {});
	// mv.visitVarInsn(ALOAD, 1);
	// mv.visitTypeInsn(INSTANCEOF,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent");
	// Label l2 = new Label();
	// mv.visitJumpInsn(IFNE, l2);
	// mv.visitInsn(ICONST_0);
	// mv.visitInsn(IRETURN);
	// mv.visitLabel(l2);
	// mv.visitFrame(Opcodes.F_NEW, 2,
	// new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object" }, 0, new Object[] {});
	// mv.visitVarInsn(ALOAD, 1);
	// mv.visitTypeInsn(CHECKCAST,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent");
	// mv.visitVarInsn(ASTORE, 2);
	// Label l3 = new Label();
	// mv.visitLabel(l3);
	// mv.visitVarInsn(ALOAD, 2);
	// mv.visitVarInsn(ALOAD, 0);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "canEqual",
	// "(Ljava/lang/Object;)Z", false);
	// Label l4 = new Label();
	// mv.visitJumpInsn(IFNE, l4);
	// mv.visitInsn(ICONST_0);
	// mv.visitInsn(IRETURN);
	// mv.visitLabel(l4);
	// mv.visitFrame(Opcodes.F_NEW, 3, new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object",
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent"
	// }, 0, new Object[] {});
	// mv.visitVarInsn(ALOAD, 0);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// toGetName(fieldName),
	// "()Ljava/lang/String;", false);
	// mv.visitVarInsn(ASTORE, 3);
	// Label l5 = new Label();
	// mv.visitLabel(l5);
	// mv.visitVarInsn(ALOAD, 2);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// toGetName(fieldName),
	// "()Ljava/lang/String;", false);
	// mv.visitVarInsn(ASTORE, 4);
	// Label l6 = new Label();
	// mv.visitLabel(l6);
	// mv.visitVarInsn(ALOAD, 3);
	// Label l7 = new Label();
	// mv.visitJumpInsn(IFNONNULL, l7);
	// mv.visitVarInsn(ALOAD, 4);
	// Label l8 = new Label();
	// mv.visitJumpInsn(IFNULL, l8);
	// Label l9 = new Label();
	// mv.visitJumpInsn(GOTO, l9);
	// mv.visitLabel(l7);
	// mv.visitFrame(Opcodes.F_NEW, 5,
	// new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object",
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object", "java/lang/Object" },
	// 0, new Object[] {});
	// mv.visitVarInsn(ALOAD, 3);
	// mv.visitVarInsn(ALOAD, 4);
	// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals",
	// "(Ljava/lang/Object;)Z", false);
	// mv.visitJumpInsn(IFNE, l8);
	// mv.visitLabel(l9);
	// mv.visitFrame(Opcodes.F_NEW, 5,
	// new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object",
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object", "java/lang/Object" },
	// 0, new Object[] {});
	// mv.visitInsn(ICONST_0);
	// mv.visitInsn(IRETURN);
	// mv.visitLabel(l8);
	// mv.visitFrame(Opcodes.F_NEW, 5,
	// new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object",
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object", "java/lang/Object" },
	// 0, new Object[] {});
	// mv.visitVarInsn(ALOAD, 0);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// toGetName(fieldName), "()J",
	// false);
	// mv.visitVarInsn(ALOAD, 2);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// toGetName(fieldName), "()J",
	// false);
	// mv.visitInsn(LCMP);
	// Label l10 = new Label();
	// mv.visitJumpInsn(IFEQ, l10);
	// mv.visitInsn(ICONST_0);
	// mv.visitInsn(IRETURN);
	// mv.visitLabel(l10);
	// mv.visitFrame(Opcodes.F_NEW, 5,
	// new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object",
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// "java/lang/Object", "java/lang/Object" },
	// 0, new Object[] {});
	// mv.visitInsn(ICONST_1);
	// mv.visitInsn(IRETURN);
	// Label l11 = new Label();
	// mv.visitLabel(l11);
	// mv.visitLocalVariable("this",
	// "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent;",
	// null, l0, l11, 0);
	// mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l0, l11, 1);
	// mv.visitLocalVariable("other",
	// "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent;",
	// null, l3, l11, 2);
	// mv.visitLocalVariable("this$bankAccountId", "Ljava/lang/Object;", null,
	// l5, l11, 3);
	// mv.visitLocalVariable("other$bankAccountId", "Ljava/lang/Object;", null,
	// l6, l11, 4);
	// mv.visitMaxs(4, 5);
	// mv.visitEnd();
	// }
	// }
	//
	// public void visitCanEqual(ClassWriter cw) {
	// MethodVisitor mv;
	// {
	// mv = cw.visitMethod(ACC_PROTECTED, "canEqual", "(Ljava/lang/Object;)Z",
	// null, null);
	// mv.visitCode();
	// Label l0 = new Label();
	// mv.visitLabel(l0);
	// mv.visitLineNumber(22, l0);
	// mv.visitVarInsn(ALOAD, 1);
	// mv.visitTypeInsn(INSTANCEOF,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent");
	// mv.visitInsn(IRETURN);
	// Label l1 = new Label();
	// mv.visitLabel(l1);
	// mv.visitLocalVariable("this",
	// "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent;",
	// null, l0, l1, 0);
	// mv.visitLocalVariable("other", "Ljava/lang/Object;", null, l0, l1, 1);
	// mv.visitMaxs(1, 2);
	// mv.visitEnd();
	// }
	// }
	//
	// public void visitHashCode(ClassWriter cw) {
	// MethodVisitor mv;
	// {
	// mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
	// mv.visitCode();
	// Label l0 = new Label();
	// mv.visitLabel(l0);
	// mv.visitLineNumber(22, l0);
	// mv.visitIntInsn(BIPUSH, 59);
	// mv.visitVarInsn(ISTORE, 1);
	// Label l1 = new Label();
	// mv.visitLabel(l1);
	// mv.visitInsn(ICONST_1);
	// mv.visitVarInsn(ISTORE, 2);
	// Label l2 = new Label();
	// mv.visitLabel(l2);
	// mv.visitVarInsn(ALOAD, 0);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// toGetName(fieldName),
	// "()Ljava/lang/String;", false);
	// mv.visitVarInsn(ASTORE, 3);
	// Label l3 = new Label();
	// mv.visitLabel(l3);
	// mv.visitVarInsn(ILOAD, 2);
	// mv.visitIntInsn(BIPUSH, 59);
	// mv.visitInsn(IMUL);
	// mv.visitVarInsn(ALOAD, 3);
	// Label l4 = new Label();
	// mv.visitJumpInsn(IFNONNULL, l4);
	// mv.visitIntInsn(BIPUSH, 43);
	// Label l5 = new Label();
	// mv.visitJumpInsn(GOTO, l5);
	// mv.visitLabel(l4);
	// mv.visitFrame(Opcodes.F_NEW, 4, new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// Opcodes.INTEGER,
	// Opcodes.INTEGER, "java/lang/Object" }, 1, new Object[] { Opcodes.INTEGER
	// });
	// mv.visitVarInsn(ALOAD, 3);
	// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I",
	// false);
	// mv.visitLabel(l5);
	// mv.visitFrame(Opcodes.F_NEW, 4, new Object[] {
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// Opcodes.INTEGER,
	// Opcodes.INTEGER, "java/lang/Object" }, 2, new Object[] { Opcodes.INTEGER,
	// Opcodes.INTEGER });
	// mv.visitInsn(IADD);
	// mv.visitVarInsn(ISTORE, 2);
	// mv.visitVarInsn(ALOAD, 0);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// toGetName(fieldName), "()J",
	// false);
	// mv.visitVarInsn(LSTORE, 4);
	// Label l6 = new Label();
	// mv.visitLabel(l6);
	// mv.visitVarInsn(ILOAD, 2);
	// mv.visitIntInsn(BIPUSH, 59);
	// mv.visitInsn(IMUL);
	// mv.visitVarInsn(LLOAD, 4);
	// mv.visitVarInsn(LLOAD, 4);
	// mv.visitIntInsn(BIPUSH, 32);
	// mv.visitInsn(LUSHR);
	// mv.visitInsn(LXOR);
	// mv.visitInsn(L2I);
	// mv.visitInsn(IADD);
	// mv.visitVarInsn(ISTORE, 2);
	// mv.visitVarInsn(ILOAD, 2);
	// mv.visitInsn(IRETURN);
	// Label l7 = new Label();
	// mv.visitLabel(l7);
	// mv.visitLocalVariable("this",
	// "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent;",
	// null, l0, l7, 0);
	// mv.visitLocalVariable("PRIME", "I", null, l1, l7, 1);
	// mv.visitLocalVariable("result", "I", null, l2, l7, 2);
	// mv.visitLocalVariable("$bankAccountId", "Ljava/lang/Object;", null, l3,
	// l7, 3);
	// mv.visitLocalVariable("$amount", "J", null, l6, l7, 4);
	// mv.visitMaxs(6, 6);
	// mv.visitEnd();
	// }
	// }
	//
	// public void visitToString(ClassWriter cw) {
	// MethodVisitor mv;
	// {
	// mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null,
	// null);
	// mv.visitCode();
	// Label l0 = new Label();
	// mv.visitLabel(l0);
	// mv.visitLineNumber(22, l0);
	// mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
	// mv.visitInsn(DUP);
	// mv.visitLdcInsn("BankAccountMoneySubtractedEvent(bankAccountId=");
	// mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>",
	// "(Ljava/lang/String;)V", false);
	//
	// mv.visitVarInsn(ALOAD, 0);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// toGetName(fieldName),
	// "()Ljava/lang/String;", false);
	// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
	// "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
	// mv.visitLdcInsn(", amount=");
	// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
	// "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
	// mv.visitVarInsn(ALOAD, 0);
	// mv.visitMethodInsn(INVOKEVIRTUAL,
	// "org/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent",
	// toGetName(fieldName), "()J",
	// false);
	// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
	// "(J)Ljava/lang/StringBuilder;", false);
	// mv.visitLdcInsn(")");
	// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
	// "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
	// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
	// "()Ljava/lang/String;", false);
	// mv.visitInsn(ARETURN);
	// Label l1 = new Label();
	// mv.visitLabel(l1);
	// mv.visitLocalVariable("this",
	// "Lorg/axonframework/samples/bank/api/bankaccount/BankAccountMoneySubtractedEvent;",
	// null, l0, l1, 0);
	// mv.visitMaxs(3, 1);
	// mv.visitEnd();
	// }
	// }
}
