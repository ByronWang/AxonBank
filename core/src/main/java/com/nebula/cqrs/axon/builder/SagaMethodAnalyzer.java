package com.nebula.cqrs.axon.builder;

import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SALOAD;
import static org.objectweb.asm.Opcodes.SASTORE;

import java.util.List;
import java.util.Stack;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.builder.SagaBlock.BlockType;
import com.nebula.tinyasm.Variable;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.MethodInfo;

public abstract class SagaMethodAnalyzer extends MethodVisitor {

	private int methodBlockIndex = 0;
	private Stack<SagaBlock> methodBlockStack = new Stack<>();
	private int[] methodLocalsOfVar;
	private Type methodReturnType;
	private Stack<Variable> methodStack = new Stack<>();
	private List<Variable> methodVariablesList;

	public SagaMethodAnalyzer(MethodVisitor mv, MethodInfo methodInfo, int access, String name, String desc, String signature) {
		super(Opcodes.ASM5, mv);
		this.methodVariablesList = methodInfo.locals;
		this.methodLocalsOfVar = Types.computerLocalsVariable(this.methodVariablesList);
		methodReturnType = Type.getReturnType(desc);
	}

	private void blockCloseCurrent() {
		onEndOfSaga();

		SagaBlock previousBlock = methodBlockStack.pop();
		stackPop(methodStack.size() - previousBlock.startStackIndex);

		SagaClassListener.LOGGER.debug("[]{}****}", SagaClassListener.repeat("    ", methodBlockStack.size()));

		switch (previousBlock.blockType) {
		case IFBLOCK:
			if (previousBlock.elseLabel != null) {

				blockStartOfElse(previousBlock.name + "Else", previousBlock.elseLabel, BlockType.ELSEBLOCK, previousBlock);
			} else {
				if (methodBlockStack.size() > 0) {
					blockStartOfElse(previousBlock.name + "ElseVirtual", previousBlock.elseLabel, BlockType.VITUALBLOCK, previousBlock);
				}
			}
			break;
		case ELSEBLOCK:
			break;
		case VITUALBLOCK:
			if (methodBlockStack.size() > 0) {
				blockCloseCurrent();
			}
			break;
		case METHODBLOCK:
		default:
			break;
		}
		printStack();
	}

	protected SagaBlock blockCurrent() {
		return methodBlockStack.peek();
	}

	private void blockStartOfElse(String name, Label labelClose, SagaBlock.BlockType blockType, SagaBlock ifBlock) {
		SagaClassListener.LOGGER.debug("[]{}else{****", SagaClassListener.repeat("    ", methodBlockStack.size()));
		SagaBlock parentBlock = methodBlockStack.peek();

		Label thisLabelclose = labelClose;
		if (labelClose == null) {
			thisLabelclose = parentBlock.labelClose;
		}

		SagaBlock nextBlock = methodBlockStack.push(new SagaBlock(name, thisLabelclose, blockType, ifBlock.startStackIndex));
		onBeginOfResult(parentBlock, nextBlock);
	}

	private void blockStartOfResult(String name, Label labelClose) {
		SagaClassListener.LOGGER.debug("[]{}if{****", SagaClassListener.repeat("    ", methodBlockStack.size()));
		SagaBlock parentBlock = methodBlockStack.peek();
		Label thisLabelclose = labelClose;
		if (labelClose == null) {
			thisLabelclose = parentBlock.labelClose;
		}

		SagaBlock nextBlock = methodBlockStack.push(new SagaBlock(name, thisLabelclose, methodStack.size()));
		onBeginOfResult(parentBlock, nextBlock);
	}

	private void blockStartOfRoot(String name) {
		SagaClassListener.LOGGER.debug("[]{}root{****", SagaClassListener.repeat("    ", methodBlockStack.size()));
		SagaBlock nextBlock = methodBlockStack.push(new SagaBlock(name + methodBlockIndex++, null, methodStack.size()));

		onBeginSaga(nextBlock, this.methodReturnType);
	}

	protected abstract void onBeginOfResult(SagaBlock parentBlock, SagaBlock nextBlock);

	protected abstract void onBeginSaga(SagaBlock nextBlock, Type methodReturnType);

	protected abstract void onEndOfSaga();

	protected abstract void onMarkSagaFinished(int value);

	protected abstract void onInvokeCommand(Stack<Variable> methodStack, int opcode, String owner, String name, String desc, boolean itf);

	protected void printStack() {
		// StringBuffer sb = new StringBuffer();
		// sb.append("\t\t\t\t\t\t\t<<");
		// for (Variable var : stack) {
		// if (var == NA) {
		// sb.append("[],");
		// } else {
		// sb.append(var.name + "|" + var.type + ",");
		// }
		// }
		// sb.append(">>");
		// System.out.println(sb.toString());
	}

	protected Variable stackPop(int size) {
		Variable var = null;
		for (int i = 0; i < size; i++) {
			var = methodStack.pop();
		}
		return var;
	}

	private Variable stackPop(Type type) {
		return stackPop(type.getSize());
	}

	private void stackPush(int size) {
		for (int i = 0; i < size; i++) {
			methodStack.push(SagaClassListener.NA);
		}
	}

	private void stackPush(String name, Type type) {
		methodStack.push(new Variable(name, type));
		stackPush(type.getSize() - 1);
	}

	@Override
	public void visitCode() {
		blockStartOfRoot("on");
		super.visitCode();
	}

	@Override
	public void visitEnd() {
		blockCloseCurrent();
		super.visitEnd();
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitFieldInsn", name);

		if (opcode == GETSTATIC || opcode == GETFIELD) {
			stackPush("", Type.getType(desc));
		} else if (opcode == PUTSTATIC || opcode == PUTFIELD) {
			stackPop(Type.getType(desc));
		}
		super.visitFieldInsn(opcode, owner, name, desc);
		printStack();
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitIincInsn", var);

		super.visitIincInsn(var, increment);
		printStack();
	}

	@Override
	public void visitInsn(int opcode) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitInsn", opcode);

		if (IRETURN <= opcode && opcode <= RETURN) {
			onMarkSagaFinished((Integer) methodStack.peek().value);
			// currentBlock().code.block(mc->{
			// mc.def("i",int.class);
			// mc.load("i");
			// mc.load("i");
			// mc.insn(IADD);
			// mc.storeTop("i");
			// });
			SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "return",
			        (Integer) methodStack.peek().value);
		}

		int cnt = Types.SIZE[opcode];
		if (cnt > 0) {
			stackPush(cnt);
		} else {
			stackPop(-cnt);
		}

		if (ICONST_0 <= opcode && opcode <= ICONST_5) {
			methodStack.peek().value = opcode - ICONST_0;
		}

		super.visitInsn(opcode);
		printStack();
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitIntInsn", operand);

		int cnt = Types.SIZE[opcode];
		if (cnt > 0) {
			stackPush(cnt);
		} else {
			stackPop(-cnt);
		}
		super.visitIntInsn(opcode, operand);
		printStack();
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		SagaClassListener.LOGGER.debug("[]{}jump", SagaClassListener.repeat("    ", methodBlockStack.size()));
		if (opcode == GOTO) {
			blockCurrent().elseLabel = label;

			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				stackPush(cnt);
			} else {
				stackPop(-cnt);
			}

		} else {
			blockStartOfResult("inner" + methodBlockIndex++, label);

			int cnt = Types.SIZE[opcode];
			if (cnt > 0) {
				stackPush(cnt);
			} else {
				stackPop(-cnt);
			}
			super.visitJumpInsn(opcode, label);
		}
		printStack();
	}

	@Override
	public void visitLabel(Label label) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "label", label);
		if (methodBlockStack.size() > 0 && label == blockCurrent().labelClose) {
			blockCloseCurrent();
		}
		super.visitLabel(label);
		printStack();
	}

	@Override
	public void visitLdcInsn(Object cst) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitLdcInsn", cst);

		stackPush("", Type.getType(cst.getClass()));

		super.visitLdcInsn(cst);
		printStack();
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitMethodInsn", name);
		if (opcode == INVOKESTATIC) {
			Type[] params = Type.getArgumentTypes(desc);
			for (int i = params.length - 1; i >= 0; i--) {
				stackPop(params[i]);
			}
			Type returnType = Type.getReturnType(desc);
			if (returnType != Type.VOID_TYPE) {
				stackPush("", returnType);
			}
		} else {
			onInvokeCommand(methodStack, opcode, owner, name, desc, itf);

			Type ownerType = Type.getObjectType(owner);

			Type[] params = Type.getArgumentTypes(desc);
			for (int i = params.length - 1; i >= 0; i--) {
				stackPop(params[i]);
			}
			Variable varOwner = stackPop(ownerType);
			Type returnType = Type.getReturnType(desc);
			if (returnType != Type.VOID_TYPE) {
				stackPush(varOwner.name + "_" + name, returnType);
			}
		}
		super.visitMethodInsn(opcode, owner, name, desc, itf);
		printStack();

	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitTypeInsn", type);
		if (opcode == NEW) {
			stackPush("", Type.getObjectType(type));
		}
		super.visitTypeInsn(opcode, type);
		printStack();
	}

	@Override
	public void visitVarInsn(int opcode, int varLocal) {
		SagaClassListener.LOGGER.debug("[]{}{}\t\t\t{}", SagaClassListener.repeat("    ", methodBlockStack.size()), "visitVarInsn", varLocal);
		if (ILOAD <= opcode && opcode <= SALOAD) {
			Variable var = methodVariablesList.get(methodLocalsOfVar[varLocal]);
			stackPush(var.name, var.type);
		}
		if (ISTORE <= opcode && opcode <= SASTORE) {
			Variable var = methodVariablesList.get(methodLocalsOfVar[varLocal]);
			stackPop(var.type);
		}
		super.visitVarInsn(opcode, varLocal);
		printStack();
	}

}
