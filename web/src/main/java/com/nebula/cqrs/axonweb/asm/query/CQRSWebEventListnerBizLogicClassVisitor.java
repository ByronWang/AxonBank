package com.nebula.cqrs.axonweb.asm.query;

import static com.nebula.cqrs.core.asm.AsmBuilder.visitGetProperty;
import static com.nebula.cqrs.core.asm.AsmBuilder.visitSetProperty;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.PUTFIELD;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.cqrs.core.asm.AsmBuilder;

public class CQRSWebEventListnerBizLogicClassVisitor extends ClassVisitor {
	class EventMethodVisitor extends MethodVisitor {
		static final int varThis = 0;
		static final int varEntry = 2;
		public EventMethodVisitor(int api, MethodVisitor mv, int access, Event event, String desc, String signature, String[] exceptions) {
			super(api, mv);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			return null;
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			return null;
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			if (owner.equals(bizLogicType.getInternalName())) {
				if (opcode == GETFIELD) {
					visitGetProperty(mv, entryType, name, Type.getType(desc));
				} else 
					if (opcode == PUTFIELD) {
					visitSetProperty(mv, entryType, name, Type.getType(desc));
				}
			} else {
				super.visitFieldInsn(opcode, owner, name, desc);
			}
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			if (var >= 2) {
				super.visitIincInsn(var + 1,increment);
			} else {
				super.visitVarInsn(var,increment);
			}
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			if (index == 0) {
				super.visitLocalVariable(name, desc, signature, start, end, index);
			} else if (index == 1) {
				super.visitLocalVariable(name, desc, signature, start, end, index);
				super.visitLocalVariable("entry", entryType.getDescriptor(), signature, start, end, varEntry);
			} else {
				super.visitLocalVariable(name, desc, signature, start, end, index + 1);
			}
		}

		@Override
		public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc,
				boolean visible) {
			return null;
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			if (opcode == ALOAD && var == varThis) {
				super.visitIntInsn(opcode, varEntry);
			} else if (var >= 2) {
				super.visitVarInsn(opcode, var + 1);
			} else {
				super.visitVarInsn(opcode, var);
			}
		}
	}

	static boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	Type bizLogicType;
	Type entryType;

	public CQRSWebEventListnerBizLogicClassVisitor(int api, ClassVisitor cv, Type logicType, Type entryType) {
		super(api, cv);
		this.bizLogicType = logicType;
		this.entryType = entryType;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		cv.visit(52, ACC_PUBLIC + ACC_SUPER, bizLogicType.getInternalName(), null, "java/lang/Object", null);
		AsmBuilder.visitDefine_init_withNothing(cv, bizLogicType);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (name.startsWith("on")) {
			Type[] argumentTypes = Type.getArgumentTypes(desc);
			Type[] newArgumentTypes = new Type[argumentTypes.length + 1];
			System.arraycopy(argumentTypes, 0, newArgumentTypes, 0, argumentTypes.length);
			newArgumentTypes[argumentTypes.length] = entryType;

			String newDesc = Type.getMethodDescriptor(Type.VOID_TYPE, newArgumentTypes);

			MethodVisitor mv = super.visitMethod(ACC_PUBLIC, name, newDesc, signature, exceptions);
			EventMethodVisitor methodVisitor = new EventMethodVisitor(Opcodes.ASM5, mv, access, null, newDesc, signature, exceptions);
			return methodVisitor;
		} else {
			return null;
		}
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr) {
		super.visitAttribute(attr);
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return null;
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
	}


	@Override
	public void visitOuterClass(String owner, String name, String desc) {
	}

	@Override
	public void visitSource(String source, String debug) {
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		return null;
	}
}
