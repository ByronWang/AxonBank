package com.nebula.cqrs.axonweb.asm.query;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nebula.cqrs.axon.pojo.AxonAsmBuilder;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.util.AsmBuilderHelper;
import com.nebula.tinyasm.util.Field;

public class CQRSRepositoryBuilder extends AxonAsmBuilder {

	public static byte[] dump(Type typeRepository, Type typeEntry, DomainDefinition domainDefinition) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		cw.visit(52, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, typeRepository.getInternalName(),
				"Ljava/lang/Object;Lorg/springframework/data/repository/CrudRepository<" + typeEntry.getDescriptor() + "Ljava/lang/String;>;",
				"java/lang/Object", new String[] { Type.getInternalName(CrudRepository.class) });

		visitAnnotation(cw, Repository.class);

		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "findAllByOrderByIdAsc", "()Ljava/lang/Iterable;",
					"()Ljava/lang/Iterable<" + typeEntry.getDescriptor() + ">;", null);
			mv.visitEnd();
		}
		{
			Field identifierField = domainDefinition.identifierField;
			String name = "findOneBy" + AsmBuilderHelper.toCamelUpper(identifierField.name);

			mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, name, Type.getMethodDescriptor(typeEntry, identifierField.type), null, null);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
