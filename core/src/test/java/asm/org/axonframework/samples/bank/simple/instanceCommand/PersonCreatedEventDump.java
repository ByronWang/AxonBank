package asm.org.axonframework.samples.bank.simple.instanceCommand;

import java.util.*;
import org.objectweb.asm.*;

public class PersonCreatedEventDump implements Opcodes {

	public static byte[] dump() throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, "org/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent", null,
				"java/lang/Object", null);

		cw.visitSource("org.axonframework.samples.bank.simple.instanceCommand.PersonCreatedEvent", null);

		{
			fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "id", "Ljava/lang/String;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "name", "Ljava/lang/String;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "age", "J", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getId", "()Ljava/lang/String;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent", "id", "Ljava/lang/String;");
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent", "name", "Ljava/lang/String;");
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getAge", "()J", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(22, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent", "age", "J");
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;Ljava/lang/String;J)V", null, null);
			{
				av0 = mv.visitAnnotation("Ljava/beans/ConstructorProperties;", true);
				{
					AnnotationVisitor av1 = av0.visitArray("value");
					av1.visit(null, "id");
					av1.visit(null, "name");
					av1.visit(null, "age");
					av1.visitEnd();
				}
				av0.visitEnd();
			}
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent", "id", "Ljava/lang/String;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent", "name", "Ljava/lang/String;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitFieldInsn(PUTFIELD, "org/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent", "age", "J");
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lorg/axonframework/samples/bank/simple/instanceCommand/PersonCreatedEvent;", null, l0, l1, 0);
			mv.visitLocalVariable("id", "Ljava/lang/String;", null, l0, l1, 1);
			mv.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l1, 2);
			mv.visitLocalVariable("age", "J", null, l0, l1, 3);
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}