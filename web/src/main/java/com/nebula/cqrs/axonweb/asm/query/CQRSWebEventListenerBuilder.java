package com.nebula.cqrs.axonweb.asm.query;

import java.io.Serializable;

import org.axonframework.eventhandling.EventHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.core.asm.AsmBuilderHelper;
import com.nebula.cqrs.core.asm.Field;

public class CQRSWebEventListenerBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type objectType, Type repoType, Type bizLogicType, Type entryType, DomainDefinition domainDefinition) throws Exception {

		ClassWriter cw = new ClassWriter(0);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, objectType.getInternalName(), null, bizLogicType.getInternalName(), null);

		Type messageType = Type.getType(SimpMessageSendingOperations.class);

		visitAnnotation(cw, Component.class);

		visitDefineField(cw, "repository", repoType);
		visitDefineField(cw, "messagingTemplate", messageType);

		visitDefine_init(cw, objectType, repoType, bizLogicType, messageType);
		visitDefine_broadcastUpdates(cw, objectType, repoType, entryType, messageType);

		for (Event event : domainDefinition.realEvents.values()) {
			if (event.ctorMethod) {
				visitDefine_onCreate(cw, objectType, repoType, event.type, entryType);
			} else {
				visitDefine_onEvent(cw, objectType, repoType, event.type, entryType, domainDefinition.identifierField);
			}
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void visitDefine_broadcastUpdates(ClassWriter cw, Type objectType, Type repoType, Type entryType, Type messageType) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PRIVATE, "broadcastUpdates", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(40, l0);
			{
				visitGetField(mv, 0, objectType, "repository", repoType);

				visitInvokeInterface(mv, repoType, Iterable.class, "findAll");

				mv.visitVarInsn(ASTORE, 1);

				visitGetField(mv, 0, objectType, "messagingTemplate", messageType);

				mv.visitLdcInsn("/topic/bank-accounts.updates");

				mv.visitVarInsn(ALOAD, 1);

				visitInvokeInterface(mv, SimpMessageSendingOperations.class, "convertAndSend", Object.class, Object.class);

				visitReturn(mv);
			}

			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l3, 0);
			mv.visitLocalVariable("bankAccountEntries", "Ljava/lang/Iterable;", "Ljava/lang/Iterable<" + entryType.getDescriptor() + ">;", l0, l3, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
	}

	private static void visitDefine_init(ClassWriter cw, Type objectType, Type repoType, Type bizLogicType, Type messageType) {
		MethodVisitor mv;
		{

			mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, repoType, messageType), null, null);
			visitAnnotation(mv, Autowired.class);

			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(34, l0);
			{
				mv.visitVarInsn(ALOAD, 0);
				visitInvokeSpecial(mv, bizLogicType, "<init>");

				visitPutField(mv, 0, objectType, 1, "repository", repoType);
				visitPutField(mv, 0, objectType, 2, "messagingTemplate", messageType);
				visitReturn(mv);
			}
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l4, 0);
			mv.visitLocalVariable("repository", repoType.getDescriptor(), null, l0, l4, 1);
			mv.visitLocalVariable("messagingTemplate", messageType.getDescriptor(), null, l0, l4, 2);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
	}

	private static void visitDefine_onCreate(ClassWriter cw, Type objectType, Type repoType, Type eventType, Type entryType) {
		MethodVisitor mv;

		mv = cw.visitMethod(ACC_PUBLIC, "on", Type.getMethodDescriptor(Type.VOID_TYPE, eventType), null, null);
		visitAnnotation(mv, EventHandler.class);

		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(46, l0);
		{
			visitNewObject(mv, entryType);
			mv.visitInsn(DUP);
			visitInvokeSpecial(mv, entryType, "<init>");
			mv.visitVarInsn(ASTORE, 2);

			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			visitInvokeVirtual(mv, objectType, "on", eventType, entryType);

			visitGetField(mv, 0, objectType, "repository", repoType);

			mv.visitVarInsn(ALOAD, 2);
			visitInvokeInterface(mv, repoType, Object.class, "save", Object.class);
			mv.visitInsn(POP);

			mv.visitVarInsn(ALOAD, 0);
			visitInvokeSpecial(mv, objectType, "broadcastUpdates");

			visitReturn(mv);
		}
		Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l5, 0);
		mv.visitLocalVariable("event", eventType.getDescriptor(), null, l0, l5, 1);
		mv.visitLocalVariable("bankAccountEntry", entryType.getDescriptor(), null, l0, l5, 2);
		mv.visitMaxs(3, 3);
		mv.visitEnd();

	}

	private static void visitDefine_onEvent(ClassWriter cw, Type objectType, Type repoType, Type eventType, Type entryType, Field identifierField) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "on", Type.getMethodDescriptor(Type.VOID_TYPE, eventType), null, null);
			visitAnnotation(mv, EventHandler.class);

			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(55, l0);
			{
				visitGetField(mv, 0, objectType, "repository", repoType);
				visitGetProperty(mv, 1, eventType, identifierField);
				String name = "findOneBy" + AsmBuilderHelper.toCamelUpper(identifierField.name);				
				visitInvokeInterface(mv, repoType, Object.class, name, Serializable.class);
				mv.visitTypeInsn(CHECKCAST, entryType.getInternalName());
				mv.visitVarInsn(ASTORE, 2);

				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 2);

				visitInvokeVirtual(mv, objectType, "on", eventType, entryType);

				visitGetField(mv, 0, objectType, "repository", repoType);
				mv.visitVarInsn(ALOAD, 2);

				visitInvokeInterface(mv, repoType, Object.class, "save", Object.class);
				mv.visitInsn(POP);

				visitInvokeSpecial(mv, 0, objectType, "broadcastUpdates");
				visitReturn(mv);

			}
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLocalVariable("this", objectType.getDescriptor(), null, l0, l5, 0);
			mv.visitLocalVariable("event", eventType.getDescriptor(), null, l0, l5, 1);
			mv.visitLocalVariable("bankAccountEntry", entryType.getDescriptor(), null, l0, l5, 2);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
	}
}
