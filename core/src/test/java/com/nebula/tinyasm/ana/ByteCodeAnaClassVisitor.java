package com.nebula.tinyasm.ana;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.GOTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import com.nebula.tinyasm.Variable;
import com.nebula.tinyasm.ana.ByteCodeAnaClassVisitor.Block.BlockType;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AnalyzeMethodParamsClassVisitor;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.MethodInfo;

public class ByteCodeAnaClassVisitor extends ClassVisitor {
	Map<String, MethodInfo> methods;

	public ByteCodeAnaClassVisitor(Map<String, MethodInfo> methods) {
		super(ASM5);
		this.methods = methods;
	}

	String className;

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.className = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	public ByteCodeAnaClassVisitor(ClassVisitor cv, Map<String, MethodInfo> methods) {
		super(ASM5, cv);
		this.methods = methods;
	}

	public static void main(String[] args) throws IOException {
		// ClassReader cr = new ClassReader(new
		// FileInputStream("D:\\CQRS\\AxonBank\\core\\target\\classes\\org\\axonframework\\samples\\bankcqrs\\generatedsources\\MyBankAccountCommandHandler$InnerDeposit.class"));
		ClassReader cr = new ClassReader(MyBankAccount.class.getName());
		// ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(),
		// new PrintWriter(System.out));

		AnalyzeMethodParamsClassVisitor analyzeMethodParamsClassVisitor = new AnalyzeMethodParamsClassVisitor();
		cr.accept(analyzeMethodParamsClassVisitor, ClassReader.SKIP_FRAMES);
		Map<String, MethodInfo> methods = analyzeMethodParamsClassVisitor.getMethods();

		ByteCodeAnaClassVisitor anaClassVisitor = new ByteCodeAnaClassVisitor(methods);

		cr.accept(anaClassVisitor, ClassReader.EXPAND_FRAMES);
	}

	boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (is(access, ACC_PUBLIC) && is(access, ACC_STATIC)) {
			MethodInfo method = methods.get(name);
			return new ByteCodeAnaMethodVisitor(mv, method, access, name, desc, signature);
		} else {

			return mv;
		}
	}

	static class Block {
		public Block(String name, Label label, BlockType blockType) {
			super();
			this.name = name;
			this.label = label;
			this.blockType = blockType;
		}

		public Block(String name, Label label) {
			super();
			this.name = name;
			this.label = label;
			this.blockType = BlockType.IFBLOCK;
		}

		public Block(String name) {
			super();
			this.name = name;
			this.blockType = BlockType.METHODBLOCK;
		}

		String name;

		Label label;
		Label elseLabel;
		BlockType blockType = BlockType.METHODBLOCK;

		enum BlockType {
			IFBLOCK, ELSEBLOCK, VITUALBLOCK, METHODBLOCK
		}
	}

	static String repeat(String str, int times) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < times; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	class ByteCodeAnaMethodVisitor extends MethodVisitor {
		Stack<Block> branchStack = new Stack<>();

		String sagaName = null;
		String sagaObjectName = null;
		
		List<Variable> locals ;
		Stack<Integer> stacks = new Stack<>();

		public ByteCodeAnaMethodVisitor(MethodVisitor mv, MethodInfo methodInfo, int access, String name, String desc, String signature) {
			super(ASM5, mv);
			String className = AsmBuilder.toSimpleName(ByteCodeAnaClassVisitor.this.className);
			sagaName = name + "ManagementSaga";
			sagaObjectName = name;
			locals = methodInfo.locals;

			StringBuffer sb = new StringBuffer();
			sb.append(className).append(name).append("[");
			sb.append(name).append("Id");
			for (Field field : methodInfo.params) {
				System.out.println(field.name + " : " + field.type.getDescriptor());

				if (field.type.getClassName().startsWith("java/") || field.type.getDescriptor().length() == 1) {
					sb.append(",");
					sb.append(field.name);
				} else {
					sb.append(",");
					sb.append(field.name);
					sb.append("Id");
				}
			}
			sb.append(']');

			System.out.println("  ");
			System.out.println(" make  " + sb.toString());
			branchStack.push(new Block("super" + i++));

			String createdEvent = sagaObjectName + "CreatedEvent";
			System.out.print("[" + branchStack.peek().name + "] ");
			System.out.println(" make " + createdEvent + " ");

			System.out.print("[" + branchStack.peek().name + "] ");
			System.out.println(sagaName + "on(" + createdEvent + ") {");
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			if (opcode == GOTO) {
				Block block = branchStack.peek();
				block.elseLabel = label;
			} else {
				System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
				System.out.println("if {" + label);
				branchStack.push(new Block("inner" + i++, label));
				super.visitJumpInsn(opcode, label);
			}
		}

		@Override
		public void visitLabel(Label label) {
			// System.out.println("label ");
			if (branchStack.size() > 0 && label == branchStack.peek().label) {
				closeCurrent();
			}
			super.visitLabel(label);
		}

		void closeCurrent() {
			Block block = branchStack.pop();
			System.out.print("[" + block.name + "] " + repeat("\t", branchStack.size()));
			System.out.println("} ");

			switch (block.blockType) {
			case IFBLOCK:
				if (block.elseLabel != null) {
					System.out.print("[" + block.name + "] " + repeat("\t", branchStack.size()));
					System.out.println(" else { ");
					block = branchStack.push(new Block(block.name, block.elseLabel, BlockType.ELSEBLOCK));
				} else {
					if (branchStack.size() > 0) {
						System.out.print("[" + block.name + "] " + repeat("\t", branchStack.size()));
						System.out.println(" else { ");
						block = branchStack.push(new Block(block.name, branchStack.peek().label, BlockType.VITUALBLOCK));
					}
				}
				break;
			case ELSEBLOCK:
				break;
			case VITUALBLOCK:
				if (branchStack.size() > 0) {
					closeCurrent();
				}
				break;
			case METHODBLOCK:
			default:
				break;
			}
		}

		@Override
		public void visitParameter(String name, int access) {
			super.visitParameter(name, access);
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("access var " + var + " ");
			super.visitVarInsn(opcode, var);
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("access field " + name + " ");
			super.visitFieldInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("invoke " + name + " command");
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}

		int i = 0;

		@Override
		public void visitEnd() {
			closeCurrent();
			super.visitEnd();
		}

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
			System.out.print("[" + this.branchStack.peek().name + "] " + repeat("\t", this.branchStack.size()));
			System.out.println("visitFrame");
			// TODO Auto-generated method stub
			super.visitFrame(type, nLocal, local, nStack, stack);
		}

		@Override
		public void visitIntInsn(int opcode, int operand) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitIntInsn");
			// TODO Auto-generated method stub
			super.visitIntInsn(opcode, operand);
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitTypeInsn");
			// TODO Auto-generated method stub
			super.visitTypeInsn(opcode, type);
		}

		@Override
		public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitInvokeDynamicInsn " + name + " ");
			// TODO Auto-generated method stub
			super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		}

		@Override
		public void visitLdcInsn(Object cst) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitLdcInsn " + cst + " ");
			// TODO Auto-generated method stub
			super.visitLdcInsn(cst);
		}

		@Override
		public void visitMultiANewArrayInsn(String desc, int dims) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitMultiANewArrayInsn ");
			// TODO Auto-generated method stub
			super.visitMultiANewArrayInsn(desc, dims);
		}

		@Override
		public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitInsnAnnotation");
			// TODO Auto-generated method stub
			return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
		}

		@Override
		public void visitLineNumber(int line, Label start) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitLineNumber");
			// TODO Auto-generated method stub
			super.visitLineNumber(line, start);
		}

		@Override
		public void visitInsn(int opcode) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitInsn");
			// if (IRETURN <= opcode && opcode <= RETURN) {
			// System.out.print("[" + stack.peek() + "] ");
			// System.out.println("RETURN end ");
			// stack.pop();
			// }
			super.visitInsn(opcode);
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("access var " + var + " ");
			super.visitIincInsn(var, increment);
		}
	}
}
