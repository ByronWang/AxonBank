package com.nebula.cqrs.axon;

import java.util.*;
import org.objectweb.asm.*;
import org.springframework.data.repository.CrudRepository;

public class CQRSRepositoryBuilder implements Opcodes {

	public static byte[] dump(Type typeRepository, Type typeEntry) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, typeRepository.getInternalName(),
				"Ljava/lang/Object;Lorg/springframework/data/repository/CrudRepository<" + typeEntry.getDescriptor() + "Ljava/lang/String;>;",
				"java/lang/Object", new String[] { Type.getInternalName(CrudRepository.class) });

		cw.visitSource(typeRepository.getClassName() + ".java", null);

		{
			av0 = cw.visitAnnotation(typeRepository.getDescriptor(), true);
			av0.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "findAllByOrderByIdAsc", "()Ljava/lang/Iterable;",
					"()Ljava/lang/Iterable<" + typeEntry.getDescriptor() + ">;", null);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "findOneByAxonId", Type.getMethodDescriptor(typeEntry, Type.getType(String.class)), null, null);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
