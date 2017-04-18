package com.nebula.cqrs.axon.asm;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.spring.config.AxonConfiguration;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class CQRSAxonConfigBuilder extends AsmBuilder {

	public static byte[] dump(Type typeDomain, Type typeConfig, Type typeCommandHandler) throws Exception {

		ClassWriter cw = new ClassWriter(0);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeConfig.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource("AxonConfig.java", null);

		annotation(cw, Configuration.class);
		define_field(cw, AxonConfiguration.class, "axonConfiguration", Autowired.class);
		define_field(cw, EventBus.class, "eventBus", Autowired.class);

		define_init_nothing(cw, typeConfig);

		define_bankAccountCommandHandler(cw, typeDomain, typeConfig, typeCommandHandler);
		
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void define_bankAccountCommandHandler(ClassWriter cw, Type typeDomain, Type typeConfig, Type typeCommandHandler) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "bankAccountCommandHandler", Type.getMethodDescriptor(typeCommandHandler), null, null);
			annotation(mv, Bean.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(37, l0);

			NEW_type(mv, typeCommandHandler);

			mv.visitInsn(DUP);

			GET_FIELD(mv, typeConfig, 0, AxonConfiguration.class, "axonConfiguration");

			mv.visitLdcInsn(typeDomain);

			INVOKE_VIRTUAL(mv, AxonConfiguration.class, Repository.class, "repository", Class.class);

			GET_FIELD(mv, typeConfig, 0, EventBus.class, "eventBus");

			INVOKE_init_typeWithAllfield(mv, typeCommandHandler, Repository.class, EventBus.class);

			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeConfig.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(4, 1);
			mv.visitEnd();
		}
	}
}
