package com.nebula.cqrs.core.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class WeMethodVisitor extends MethodVisitor implements MethodCode, MethodHeader, Opcodes {
	final static int THIS = 0;
	final static String THIS_NAME = "this";
	private final Type thisType;
	private final Type returnType;
	private Label beginLabel;
	private Stack<Field> localFields = new Stack<Field>();
	private List<Field> params = new ArrayList<>();
	private final ClassVisitor cv;
	private String methodName;
	int access;
	int[] locals;

	class Annotation {
		public Annotation(Object value, Type type) {
			super();
			this.value = value;
			this.type = type;
		}

		public Annotation(Object value, Type type, int parameter) {
			super();
			this.value = value;
			this.type = type;
			this.parameter = parameter;
		}

		public Object value;
		public Type type;
		int parameter;
	}

	public WeMethodVisitor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName) {
		super(Opcodes.ASM5);
		this.cv = cv;
		this.thisType = thisType;
		this.methodName = methodName;
		this.access = access;
		this.returnType = returnType;
	}

	public WeMethodVisitor(ClassVisitor cv, Type thisType, int access, String methodName) {
		this(cv, thisType, access, Type.VOID_TYPE, methodName);
	}

	public WeMethodVisitor parameter(Field field) {
		params.add(field);
		return this;
	}

	public MethodCode parameter(Field... fields) {
		for (Field field : fields) {
			params.add(field);
		}
		return this;
	}

	public MethodCode begin(int line) {

		localFields.push(new Field(THIS_NAME, thisType));
		for (Field field : params) {
			localFields.push(field);
		}

		locals = computerLocals(localFields);

		this.mv = ASMBuilder.visitDefineMethod(access, cv, returnType, methodName, AsmBuilderHelper.typesOf(params));
		for (Annotation annotation : thisMethodAnnotations) {
			ASMBuilder.visitAnnotation(mv, annotation.type, annotation.value);
		}
		for (Annotation annotation : thisParameteAnnotations) {
			ASMBuilder.visitParameterAnnotation(mv, annotation.parameter, annotation.type, annotation.value);
		}
		mv.visitCode();
		beginLabel = this.label(line);
		return this;
	}

	static int[] computerLocals(List<Field> fields) {
		int[] locals = new int[fields.size()];
		int cntLocal = 0;
		for (int i = 0; i < fields.size(); i++) {
			locals[i] = cntLocal;
			cntLocal += fields.get(i).type.getSize();
		}
		return locals;
	}

	List<Annotation> thisMethodAnnotations = new ArrayList<>();
	List<Annotation> thisParameteAnnotations = new ArrayList<>(10);

	public MethodCode annotation(Class<?> annotation, String value) {
		thisMethodAnnotations.add(new Annotation(value, AsmBuilderHelper.typeOf(annotation)));
		return this;
	}

	public MethodCode parameterAnnotation(int parameter, Class<?> annotation, String value) {
		thisParameteAnnotations.set(parameter, new Annotation(value, AsmBuilderHelper.typeOf(annotation)));
		return this;
	}

	public Label label(int line) {
		Label label = new Label();
		mv.visitLabel(label);
		mv.visitLineNumber(line, label);
		return label;
	}

	public MethodCode line(int line) {
		Label label = new Label();
		mv.visitLabel(label);
		mv.visitLineNumber(line, label);
		return this;
	}

	@Override
	public MethodCode load(int... indexes) {
		for (int i : indexes) {
			mv.visitVarInsn(localFields.get(i).type.getOpcode(ILOAD), i);
		}
		return this;
	}

	public Label label() {
		Label label = new Label();
		mv.visitLabel(label);
		return label;
	}

	@Override
	public MethodCode thisGetField(Field field) {
		ASMBuilder.visitGetField(mv, THIS, thisType, field.name, field.type);
		return this;
	}

	@Override
	public MethodCode thisGetProperty(Field field) {
		ASMBuilder.visitGetProperty(mv, THIS, thisType, field.name, field.type);
		return this;
	}

	@Override
	public MethodCode thisPutField(int dataIndex, String fieldName, Type fieldType) {
		ASMBuilder.visitPutField(mv, THIS, thisType, dataIndex, fieldName, fieldType);
		return this;
	}

	@Override
	public MethodCode thisPutField(int dataIndex, Field field) {
		ASMBuilder.visitPutField(mv, THIS, thisType, dataIndex, field.name, field.type);
		return this;
	}

	@Override
	public MethodCode thisPutField(Field field) {
		ASMBuilder.visitPutField(mv, thisType, field.name, field.type);
		return this;
	}

	@Override
	public MethodCode thisInitObject() {
		ASMBuilder.visitInitObject(mv, THIS);
		return this;
	}

	@Override
	public MethodCode returnVoid() {
		ASMBuilder.visitReturn(mv);
		return this;
	}

	@Override
	public MethodCode returnObject() {
		ASMBuilder.visitReturnObject(mv);
		return this;
	}

	@Override
	public MethodCode returnType(Type type) {
		ASMBuilder.visitReturnType(mv, type);
		return this;
	}

	public MethodCode localVariable(String name, Type type) {
		localFields.push(new Field(name, type));
		return this;
	}

	// TODO
	public MethodCode begin() {
		return this.begin(10);
	}

	public void end() {
		Label endLabel = this.label();
		int i = 0;
		for (Field field : localFields) {
			mv.visitLocalVariable(field.name, field.type.getDescriptor(), null, beginLabel, endLabel, locals[i++]);
		}
		mv.visitMaxs(10, 10);
		mv.visitEnd();
	}

	@Override
	public MethodHeader annotation(Type type, String value) {
		ASMBuilder.visitAnnotation(mv, type, value);
		return this;
	}

	@Override
	public MethodHeader parameterAnnotation(int parameter, Type type, Object value) {
		ASMBuilder.visitParameterAnnotation(mv, parameter, type, value);
		return this;
	}

	@Override
	public void thisInvokeVirtual(Type returnType, String methodName, Type... params) {
		ASMBuilder.visitInvokeVirtual(mv, thisType, returnType, methodName, params);

	}

	@Override
	public void thisInvokeSpecial(Type returnType, String methodName, Type... params) {
		ASMBuilder.visitInvokeSpecial(mv, thisType, returnType, methodName, params);

	}

	@Override
	public void thisInvokeInterface(Type returnType, String methodName, Type... params) {
		ASMBuilder.visitInvokeInterface(mv, thisType, returnType, methodName, params);

	}

	@Override
	public MethodCode insn(int opcode) {
		mv.visitInsn(opcode);
		return this;
	}

	@Override
	public MethodCode jumpInsn(int ifgt, Label label) {
		mv.visitJumpInsn(IFGT, label);
		return this;
	}

	@Override
	public MethodCode visit(Label label, int line) {
		mv.visitLabel(label);
		mv.visitLineNumber(line, label);
		return this;
	}

	@Override
	public MethodCode visit(Label label) {
		mv.visitLabel(label);
		return this;
	}

	@Override
	public MethodCode visit(Consumer<MethodCode> invocation) {
		invocation.accept(this);
		return this;
	}

	@Override
	public MethodCode code(Consumer<MethodCode> invocation) {
		begin();
		invocation.accept(this);
		end();
		return this;
	}

	@Override
	public MethodCode code(int line, Consumer<MethodCode> invocation) {
		begin(line);
		invocation.accept(this);
		end();
		return this;
	}
}