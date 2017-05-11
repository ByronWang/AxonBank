package com.nebula.tinyasm.ana;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.util.MethodInfo;

public class ByteCodeAnaClassVisitor extends ClassVisitor {

	// public static void main(String[] args) throws IOException {
	// // ClassReader cr = new ClassReader(new
	// //
	// FileInputStream("D:\\CQRS\\AxonBank\\core\\target\\classes\\org\\axonframework\\samples\\bankcqrs\\generatedsources\\MyBankAccountCommandHandler$InnerDeposit.class"));
	// ClassReader cr = new ClassReader(MyBankAccount.class.getName());
	// // ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(),
	// // new PrintWriter(System.out));
	//
	// AnalyzeMethodParamsClassVisitor analyzeMethodParamsClassVisitor = new
	// AnalyzeMethodParamsClassVisitor();
	// cr.accept(analyzeMethodParamsClassVisitor, ClassReader.SKIP_FRAMES);
	// Map<String, MethodInfo> methods =
	// analyzeMethodParamsClassVisitor.getMethods();
	//
	// // TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new
	// // ASMifier(), new PrintWriter(System.out));
	// ByteCodeAnaClassVisitor anaClassVisitor = new
	// ByteCodeAnaClassVisitor(methods);
	//
	// cr.accept(anaClassVisitor, ClassReader.EXPAND_FRAMES);
	// }

	static String repeat(String str, int times) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < times; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	DomainDefinition domainDefinition;

	public ByteCodeAnaClassVisitor(ClassVisitor cv, DomainDefinition domainDefinition) {
		super(ASM5, cv);
		this.domainDefinition = domainDefinition;
	}

	public ByteCodeAnaClassVisitor(DomainDefinition domainDefinition) {
		super(ASM5);
		this.domainDefinition = domainDefinition;
	}

	boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (is(access, ACC_PUBLIC) && is(access, ACC_STATIC)) {
			MethodInfo method = domainDefinition.menthods.get(name);
			return new ByteCodeAnaMethodVisitor(domainDefinition, mv, method, access, name, desc, signature);
		} else {

			return mv;
		}
	}

}
