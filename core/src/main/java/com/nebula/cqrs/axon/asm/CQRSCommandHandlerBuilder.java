package com.nebula.cqrs.axon.asm;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.Field;

public class CQRSCommandHandlerBuilder extends AxonAsmBuilder {

	public byte[] dump(Command[] commands, Type typeDomain, Type typeHandler) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		FieldVisitor fv;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeHandler.getInternalName(), null, "java/lang/Object", null);

		for (Command command : commands) {
			if (!command.ctorMethod) {
				Type inner = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
				cw.visitInnerClass(inner.getInternalName(), null, null, 0);
			}
		}

		{
			fv = cw.visitField(ACC_PRIVATE, "repository", "Lorg/axonframework/commandhandling/model/Repository;",
					"Lorg/axonframework/commandhandling/model/Repository<" + typeDomain.getDescriptor() + ">;", null);
			fv.visitEnd();
		}

		visitDefineField(cw, "eventBus", EventBus.class);

		define_init(cw, typeDomain, typeHandler);

		for (Command command : commands) {
			if (command.ctorMethod) {
				dumpCtorMethod(cw, typeDomain, typeHandler, command);
			} else {
				dumpMethod(cw, typeDomain, typeHandler, command);
			}
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	private void define_init(ClassWriter cw, Type typeDomain, Type typeHandler) {
		MethodVisitor mv;
		{
			final int _this = 0;
			final int _repository = 1;
			final int _eventBus = 2;

			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/axonframework/commandhandling/model/Repository;Lorg/axonframework/eventhandling/EventBus;)V",
					"(Lorg/axonframework/commandhandling/model/Repository<" + typeDomain.getDescriptor() + ">;Lorg/axonframework/eventhandling/EventBus;)V",
					null);
			mv.visitParameter("repository", 0);
			mv.visitParameter("eventBus", 0);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(43, l0);

			visitInitObject(mv, _this);
			visitPutField(mv, _this, typeHandler, _repository, "repository", Repository.class);
			visitPutField(mv, _this, typeHandler, _eventBus, "eventBus", EventBus.class);

			mv.visitInsn(RETURN);

			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", typeHandler.getDescriptor(), null, l0, l4, _this);
			mv.visitLocalVariable("repository", "Lorg/axonframework/commandhandling/model/Repository;",
					"Lorg/axonframework/commandhandling/model/Repository<" + typeDomain.getDescriptor() + ">;", l0, l4, _repository);
			mv.visitLocalVariable("eventBus", "Lorg/axonframework/eventhandling/EventBus;", null, l0, l4, _eventBus);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
	}

	void dumpCtorMethod(ClassWriter cw, Type typeDomain, Type typeHandler, Command command) {
		MethodVisitor mv;

		Type typeInner = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
		{

			mv = cw.visitMethod(ACC_PUBLIC, "handle", Type.getMethodDescriptor(Type.VOID_TYPE, command.type), null, new String[] { "java/lang/Exception" });
			mv.visitParameter("command", 0);
			visitAnnotation(mv, CommandHandler.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(50, l0);

			visitGetField(mv, 0, typeHandler, "repository", Repository.class);
			visitNewObject(mv, typeInner);
			mv.visitInsn(DUP);

			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			visitInitTypeWithAllFields(mv, typeInner, typeHandler, command.type);

			visitInvokeInterface(mv, Repository.class, Aggregate.class, "newInstance", Callable.class);

			mv.visitInsn(POP);
			mv.visitInsn(RETURN);

			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", typeHandler.getDescriptor(), null, l0, l2, 0);
			mv.visitLocalVariable("command", command.type.getDescriptor(), null, l0, l2, 1);
			mv.visitMaxs(5, 2);
			mv.visitEnd();
		}
	}

	void dumpMethod(ClassWriter cw, Type typeDomain, Type typeHandler, Command command) {
		MethodVisitor mv;
		{
			Field idField = command.fields[0];

			Type inner = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);

			mv = cw.visitMethod(ACC_PUBLIC, "handle", Type.getMethodDescriptor(Type.VOID_TYPE, command.type), null, null);
			mv.visitParameter("command", 0);
			visitAnnotation(mv, CommandHandler.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(61, l0);

			visitGetField(mv, 0, typeHandler, "repository", Repository.class);
			visitGetProperty(mv, 1, command.type, idField);
			visitInvokeInterface(mv, Repository.class, Aggregate.class, "load", idField.type);
			mv.visitVarInsn(ASTORE, 2);

			mv.visitVarInsn(ALOAD, 2);

			visitNewObject(mv, inner);
			mv.visitInsn(DUP);

			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			visitInitTypeWithAllFields(mv, inner, typeHandler, command.type);

			visitInvokeInterface(mv, Aggregate.class, "execute", Consumer.class);

			mv.visitInsn(RETURN);

			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", typeHandler.getDescriptor(), null, l0, l3, 0);
			mv.visitLocalVariable("command", command.type.getDescriptor(), null, l0, l3, 1);
			mv.visitLocalVariable("bankAccountAggregate", "Lorg/axonframework/commandhandling/model/Aggregate;",
					"Lorg/axonframework/commandhandling/model/Aggregate<" + typeDomain.getDescriptor() + ">;", l0, l3, 2);
			mv.visitMaxs(5, 3);
			mv.visitEnd();

		}
	}
}
