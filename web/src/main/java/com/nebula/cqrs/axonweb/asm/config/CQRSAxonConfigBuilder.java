package com.nebula.cqrs.axonweb.asm.config;

import javax.persistence.EntityManager;

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

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;

public class CQRSAxonConfigBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type typeConfig, Type implDomainType, Type typeRepository, Type typeCommandHandler) throws Exception {

		ClassWriter cw = new ClassWriter(0);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeConfig.getInternalName(), null, "java/lang/Object", null);

		visitAnnotation(cw, Configuration.class);
		visitDefineField(cw, "axonConfiguration", AxonConfiguration.class, Autowired.class);
		visitDefineField(cw, "entityManager", EntityManager.class, Autowired.class);
		visitDefineField(cw, "eventBus", EventBus.class, Autowired.class);

		visitDefine_init_withNothing(cw, typeConfig);

		define_bankAccountCommandHandler(cw, implDomainType, typeConfig, typeCommandHandler);

		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void define_bankAccountCommandHandler(ClassWriter cw, Type implDomainType, Type typeConfig, Type typeCommandHandler) {
		MethodVisitor mv;
		{
			
			mv = cw.visitMethod(ACC_PUBLIC,"init" + toSimpleName(typeCommandHandler.getClassName()), Type.getMethodDescriptor(typeCommandHandler), null, null);
			visitAnnotation(mv, Bean.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(37, l0);
			{
				visitNewObject(mv, typeCommandHandler);

				mv.visitInsn(DUP);

				visitGetField(mv, 0, typeConfig, "axonConfiguration", AxonConfiguration.class);
				mv.visitLdcInsn(implDomainType);
				visitInvokeVirtual(mv, AxonConfiguration.class, Repository.class, "repository", Class.class);

				visitGetField(mv, 0, typeConfig, "eventBus", EventBus.class);

				visitInitTypeWithAllFields(mv, typeCommandHandler, Repository.class, EventBus.class);
				visitAReturn(mv);

			}
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeConfig.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(4, 1);
			mv.visitEnd();
		}
	}
}
