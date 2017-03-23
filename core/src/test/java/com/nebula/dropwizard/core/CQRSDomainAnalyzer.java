package com.nebula.dropwizard.core;

import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.dropwizard.core.CQRSDomainBuilder.Field;

public class CQRSDomainAnalyzer extends ClassVisitor {

	public CQRSDomainAnalyzer(int api, ClassVisitor cv) {
		super(api, cv);
	}

	public CQRSDomainAnalyzer(int api) {
		super(api);
	}

	Map<String, Method> methods = new HashMap<>();

	static class Method {
		String name;
		Field[] params;

		public Method(String name) {
			super();
			this.name = name;
		}

		@Override
		public String toString() {
			return "Method [name=" + name + ", params=" + Arrays.toString(params) + "]\n";
		}
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
	}

	class FillParamsMethodVisitor extends MethodVisitor {
		Method method;

		public FillParamsMethodVisitor(int api, MethodVisitor mv, String name, String desc) {
			super(api, mv);
			this.method = new Method(name);
			CQRSDomainAnalyzer.this.methods.put(name, method);

			Type[] types = Type.getArgumentTypes(desc);
			this.method.params = new Field[types.length];
			for (int i = 0; i < types.length; i++) {
				this.method.params[i] = new Field(name, types[i]);
			}
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			if (0 < index && index <= this.method.params.length) {
				this.method.params[index - 1].name = name;
			}
			super.visitLocalVariable(name, desc, signature, start, end, index);
		}
	}

	static boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (!is(access, ACC_STATIC)) {
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return new FillParamsMethodVisitor(api, methodVisitor, name, desc);
		} else {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}

	}

	static String toBeanProperties(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

}
