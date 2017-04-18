package com.nebula.cqrs.axon;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Field;

public class AsmBuilder {

	public static void getField(MethodVisitor mv, int index, Type type, Field field) {
		mv.visitVarInsn(ALOAD, index);
		mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), PojoBuilder.toGetName(field.name), Type.getMethodDescriptor(field.type), false);
	}

	public static void init(MethodVisitor mv, Type type, List<Field> fields) {
		Type[] params = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			params[i] = fields.get(i).type;
		}
		mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, params), false);
	}

	public static void printStaticMessage(MethodVisitor mv, String staticMessage) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(staticMessage);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	public static void printObject(MethodVisitor mv, int objectIndex) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
	}

	public static void printString(MethodVisitor mv, int stringIndex) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(stringIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}
}
