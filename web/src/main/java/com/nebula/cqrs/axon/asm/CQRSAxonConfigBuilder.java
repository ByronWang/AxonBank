package com.nebula.cqrs.axon.asm;

import javax.persistence.EntityManager;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.spring.config.AxonConfiguration;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class CQRSAxonConfigBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type typeConfig, Type typeDomain, Type typeRepository, Type typeCommandHandler) throws Exception {

		ClassWriter cw = new ClassWriter(0);

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, typeConfig.getInternalName(), null, "java/lang/Object", null);

		cw.visitSource("AxonConfig.java", null);

		visitAnnotation(cw, Configuration.class);
		visitDefineField(cw, "axonConfiguration", AxonConfiguration.class, Autowired.class);
		visitDefineField(cw, "entityManager", EntityManager.class, Autowired.class);
		visitDefineField(cw, "eventBus", EventBus.class, Autowired.class);
		visitDefineField(cw, "repositoryFactory", RepositoryFactorySupport.class, Autowired.class);

		visitDefine_init_withNothing(cw, typeConfig);

		define_bankAccountCommandHandler(cw, typeDomain, typeConfig, typeCommandHandler);

		define_repositoryFactorySupport(cw, typeDomain, typeConfig, typeRepository);

//		define_bankAccountRepository(cw, typeConfig, typeRepository);

		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void define_repositoryFactorySupport(ClassWriter cw, Type typeDomain, Type typeConfig, Type typeRepository) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "repositoryFactorySupport", Type.getMethodDescriptor(Type.getType(RepositoryFactorySupport.class)), null, null);
			visitAnnotation(mv, Bean.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(37, l0);
			{
				visitNewObject(mv, JpaRepositoryFactory.class);
				mv.visitInsn(DUP);

				visitGetField(mv, 0, typeConfig, "entityManager", EntityManager.class);
				visitInitTypeWithAllFields(mv, JpaRepositoryFactory.class, EntityManager.class);

				visitAReturn(mv);
			}
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeConfig.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(4, 1);
			mv.visitEnd();
		}
	}

	private static void define_bankAccountRepository(ClassWriter cw, Type typeConfig, Type typeRepository) {
		MethodVisitor mv;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "init" + toSimpleName(typeRepository.getClassName()), Type.getMethodDescriptor(typeRepository), null, null);
			visitAnnotation(mv, Bean.class);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(37, l0);
			{

				visitGetField(mv, 0, typeConfig, "repositoryFactory", RepositoryFactorySupport.class);
				mv.visitLdcInsn(typeRepository);
				visitInvokeVirtual(mv, RepositoryFactorySupport.class, Object.class, "getRepository", Class.class);
				mv.visitTypeInsn(CHECKCAST, typeRepository.getInternalName());
				visitAReturn(mv);

			}
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", typeConfig.getDescriptor(), null, l0, l1, 0);
			mv.visitMaxs(4, 1);
			mv.visitEnd();
		}
	}

	private static void define_bankAccountCommandHandler(ClassWriter cw, Type typeDomain, Type typeConfig, Type typeCommandHandler) {
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
				mv.visitLdcInsn(typeDomain);
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
