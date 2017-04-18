package com.nebula.cqrs.axon;

import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Field;

public class AsmBuilder {

	public static void visitInvoke_getField(MethodVisitor mv, int index, Type type, Field field) {
		mv.visitVarInsn(ALOAD, index);
		mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), PojoBuilder.toGetName(field.name), Type.getMethodDescriptor(field.type), false);
	}

	public static void visitInvoke_init(MethodVisitor mv, Type type, List<Field> fields) {
		Type[] params = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			params[i] = fields.get(i).type;
		}
		mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, params), false);
	}
}
