package com.nebula.tinyasm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.api.ClassBody;

public class StatusBuilder implements Opcodes {

	public static byte[] dump(Type objectType, String... names) {
		return build(objectType, names).toByteArray();
	}
	public static ClassBody build(Type objectType, String... names) {
		Type superType = Type.getObjectType("java/lang/Enum");

		ClassBody cb = ClassBuilder.make(ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM, objectType, superType, new Type[] { objectType });

		for (String name : names) {
			cb.field(ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM, name, objectType);
		}
		cb.field(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC, "ENUM$VALUES", objectType, true);

		cb.staticMethod("<clinit>").code(mc -> {
			{
				mc.line(4);

				for (int i = 0; i < names.length; i++) {
					mc.newInstace(objectType);
					mc.dup();
					mc.ldcInsn(names[i]);
					mc.intInsn(BIPUSH, i);
					mc.typeThis().invokeSpecial("<init>", String.class, int.class);
					mc.typeThis().putStaticTo(names[i], objectType);
				}

				mc.line(3).intInsn(BIPUSH, names.length);
				mc.typeInsn(ANEWARRAY, mc.thisType());

				for (int i = 0; i < names.length; i++) {
					mc.dup();
					mc.intInsn(BIPUSH, i);
					mc.typeThis().getStatic(names[i], mc.thisType());
					mc.insn(AASTORE);
				}
				mc.typeThis().putStaticTo("ENUM$VALUES", mc.thisType(), true);
				mc.returnVoid();
			}
		});

		cb.privateMethod("<init>").parameter("name", String.class).parameter("value", int.class).code(mc -> {
			mc.line(3).loadThis();
			mc.load("name");
			mc.load("value");
			mc.type(Enum.class).invokeSpecial("<init>", String.class, int.class);
			mc.returnVoid();
		});

		cb.publicStaticMethod(objectType, true, "values").code(mc -> {
			mc.line(1);
			mc.def("vs", objectType, true);
			mc.typeThis().getStatic("ENUM$VALUES", objectType, true);
			mc.dup();
			mc.storeTop("vs");

			mc.insn(ICONST_0);
			mc.load("vs");
			mc.insn(ARRAYLENGTH);
			mc.dup();
			mc.def("length", int.class);
			mc.storeTop("length");

			mc.typeInsn(ANEWARRAY, objectType);
			mc.dup();
			mc.def("newvs", objectType, true);
			mc.storeTop("newvs");

			mc.insn(ICONST_0);
			mc.load("length");

			mc.type(System.class).invokeStatic("arraycopy", Object.class, int.class, Object.class, int.class, int.class);
			mc.load("newvs");
			mc.returnObject();
		});

		cb.publicStaticMethod(objectType, "valueOf").parameter("name", String.class).code(mc -> {
			mc.line(1);
			mc.ldcInsn(objectType);
			mc.load("name");
			mc.type(Enum.class).invokeStatic(Enum.class, "valueOf", Class.class, String.class);
			mc.checkCast(objectType);
			mc.returnObject();
		});
		return cb;
	}
}
