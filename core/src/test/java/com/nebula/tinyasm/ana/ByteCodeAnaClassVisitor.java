package com.nebula.tinyasm.ana;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.SALOAD;
import static org.objectweb.asm.Opcodes.SASTORE;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

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

		// TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new
		// ASMifier(), new PrintWriter(System.out));
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
		public Block(String name, Label label, BlockType blockType, final int startStackIndex) {
			super();
			this.name = name;
			this.label = label;
			this.blockType = blockType;
			this.startStackIndex = startStackIndex;
		}

		public Block(String name, Label label, final int startStackIndex) {
			super();
			this.name = name;
			this.label = label;
			this.blockType = BlockType.IFBLOCK;
			this.startStackIndex = startStackIndex;
		}

		public Block(String name, final int startStackIndex) {
			super();
			this.name = name;
			this.blockType = BlockType.METHODBLOCK;
			this.startStackIndex = startStackIndex;
		}

		String name;

		final int startStackIndex;
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

		int[] localsVar;
		List<Variable> variablesList;

		public ByteCodeAnaMethodVisitor(MethodVisitor mv, MethodInfo methodInfo, int access, String name, String desc, String signature) {
			super(ASM5, mv);
			String className = AsmBuilder.toSimpleName(ByteCodeAnaClassVisitor.this.className);
			this.sagaName = name + "ManagementSaga";
			this.sagaObjectName = name;
			this.variablesList = methodInfo.locals;
			this.localsVar = Types.computerLocalsVariable(this.variablesList);

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
			branchStack.push(new Block("super" + blockIndex++, 0));

			String createdEvent = sagaObjectName + "CreatedEvent";
			System.out.print("[" + branchStack.peek().name + "] ");
			System.out.println(" make " + createdEvent + " ");

			System.out.print("[" + branchStack.peek().name + "] ");
			System.out.println(sagaName + " -> on(" + createdEvent + ") {");
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				push(cnt);
			} else {
				pop(-cnt);
			}
			if (opcode == GOTO) {
				Block block = branchStack.peek();
				block.elseLabel = label;
			} else {
				System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
				System.out.println("if {" + label);
				branchStack.push(new Block("inner" + blockIndex++, label, currentStacks.size()));
				super.visitJumpInsn(opcode, label);
			}
			printStack();
		}

		@Override
		public void visitLabel(Label label) {
			// System.out.println("label ");
			if (branchStack.size() > 0 && label == branchStack.peek().label) {
				closeCurrent();
			}
			super.visitLabel(label);
			printStack();
		}

		void closeCurrent() {
			Block block = branchStack.pop();
			pop(currentStacks.size() - block.startStackIndex);
			System.out.print("[" + block.name + "] " + repeat("\t", branchStack.size()));
			System.out.println("} ");

			switch (block.blockType) {
			case IFBLOCK:
				if (block.elseLabel != null) {
					System.out.print("[" + block.name + "] " + repeat("\t", branchStack.size()));
					System.out.println(" else { ");
					block = branchStack.push(new Block(block.name, block.elseLabel, BlockType.ELSEBLOCK, block.startStackIndex));
				} else {
					if (branchStack.size() > 0) {
						System.out.print("[" + block.name + "] " + repeat("\t", branchStack.size()));
						System.out.println(" else { ");
						block = branchStack.push(new Block(block.name, branchStack.peek().label, BlockType.VITUALBLOCK, block.startStackIndex));
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
			printStack();
		}

		@Override
		public void visitVarInsn(int opcode, int varLocal) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitVarInsn " + varLocal + " ");
			if (ILOAD <= opcode && opcode <= SALOAD) {
				Variable var = variablesList.get(localsVar[varLocal]);
				push(var.name, var.type);
			}
			if (ISTORE <= opcode && opcode <= SASTORE) {
				Variable var = variablesList.get(localsVar[varLocal]);
				pop(var.type);
			}
			super.visitVarInsn(opcode, varLocal);
			printStack();
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitFieldInsn " + name + " ");

			if (opcode == GETSTATIC || opcode == GETFIELD) {
				push("", Type.getType(desc));
			} else if (opcode == PUTSTATIC || opcode == PUTFIELD) {
				pop(Type.getType(desc));
			}
			super.visitFieldInsn(opcode, owner, name, desc);
			printStack();
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitMethodInsn " + name + " command");

			if (opcode == INVOKESTATIC) {
				Type[] params = Type.getArgumentTypes(desc);
				for (int i = params.length - 1; i >= 0; i--) {
					pop(params[i]);
				}
				Type returnType = Type.getReturnType(desc);
				if (returnType != Type.VOID_TYPE) {
					push("", returnType);
				}
			} else {
				Type ownerType = Type.getType("L" + owner + ";");

				Type[] params = Type.getArgumentTypes(desc);
				for (int i = params.length - 1; i >= 0; i--) {
					pop(params[i]);
				}
				pop(ownerType);
				Type returnType = Type.getReturnType(desc);
				if (returnType != Type.VOID_TYPE) {
					push("", returnType);
				}
			}
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			printStack();
		}

		int blockIndex = 0;

		@Override
		public void visitEnd() {
			closeCurrent();
			super.visitEnd();
		}

		@Override
		public void visitIntInsn(int opcode, int operand) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitIntInsn");
			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				push(cnt);
			} else {
				pop(-cnt);
			}
			super.visitIntInsn(opcode, operand);
			printStack();
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitTypeInsn");
			if (opcode == NEW) {
				push("", Type.getType("L" + type + ";"));
			}
			super.visitTypeInsn(opcode, type);
			printStack();
		}

		Variable NA = new Variable("NA", Type.VOID_TYPE);
		Stack<Variable> currentStacks = new Stack<>();

		private void push(int size) {
			for (int i = 0; i < size; i++) {
				currentStacks.push(NA);
			}
		}

		private void push(String name, Type type) {
			currentStacks.push(new Variable(name, type));
			push(type.getSize() - 1);
		}

		private void pop(Type type) {
			pop(type.getSize());
		}

		private void pop(int size) {
			for (int i = 0; i < size; i++) {
				currentStacks.pop();
			}
		}

		@Override
		public void visitLdcInsn(Object cst) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitLdcInsn " + cst + " ");

			push("", Type.getType(cst.getClass()));

			super.visitLdcInsn(cst);
			printStack();
		}

		@Override
		public void visitLineNumber(int line, Label start) {
			super.visitLineNumber(line, start);
		}

		@Override
		public void visitInsn(int opcode) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitInsn");

			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				push(cnt);
			} else {
				pop(-cnt);
			}
			super.visitInsn(opcode);
			printStack();
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			System.out.print("[" + branchStack.peek().name + "] " + repeat("\t", branchStack.size()));
			System.out.println("visitIincInsn " + var + " ");
			super.visitIincInsn(var, increment);
			printStack();
		}

		private void printStack() {
			StringBuffer sb = new StringBuffer();
			sb.append("\t\t\t\t\t\t\t<<");
			for (Variable var : currentStacks) {
				if (var == NA) {
					sb.append("[],");
				} else {
					sb.append(var.name + "|" + var.type + ",");
				}
			}
			sb.append(">>");
			System.out.println(sb.toString());
		}
	}

}
