package com.nebula.cqrs.core.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import org.axonframework.eventhandling.EventHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ConvertFromParamsToClassMethodVisitor extends MethodVisitor {
	private final Type eventType;
	private final Field[] params;

	public ConvertFromParamsToClassMethodVisitor(ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions, Type eventType,
			Field[] params) {
		super(Opcodes.ASM5, cv.visitMethod(access, name, Type.getMethodDescriptor(Type.VOID_TYPE, eventType), signature, exceptions));
		this.eventType = eventType;
		this.params = params;

		AsmBuilder.visitAnnotation(mv, EventHandler.class);
	}

	// Replace visit param to invoke event's property
	@Override
	public void visitVarInsn(int opcode, int var) {
		if (0 < var && var <= params.length) {
			super.visitVarInsn(ALOAD, 1);
			Field field = params[var - 1];
			AsmBuilder.visitGetProperty(mv, eventType, field.name, field.type);
		} else {
			super.visitVarInsn(opcode, var);
		}
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if (index == 0) {
			super.visitLocalVariable(name, desc, signature, start, end, index);
			super.visitLocalVariable("event", eventType.getDescriptor(), signature, start, end, 1);
		} else if (index <= params.length) {
		} else {
			super.visitLocalVariable(name, desc, signature, start, end, index - params.length + 1);
		}
	}

	boolean doneVisitParameter = false;

	@Override
	public void visitParameter(String name, int access) {
		if (access == 0) {
			super.visitParameter("event", 0);
		}
	}
}