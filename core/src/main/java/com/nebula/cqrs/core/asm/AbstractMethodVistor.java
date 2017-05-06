package com.nebula.cqrs.core.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public abstract class AbstractMethodVistor<H, C> extends MethodVisitor implements MethodCode<C, Instance<C>>, Instance<C>, MethodHeader<H, C> {

	class Annotation {
		int parameter;

		public Type type;

		public Object value;

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
	}

	final static int THIS = 0;

	final static String THIS_NAME = "this";

	static int[] computerLocals(List<Field> fields) {
		int[] locals = new int[fields.size()];
		int cntLocal = 0;
		for (int i = 0; i < fields.size(); i++) {
			locals[i] = cntLocal;
			cntLocal += fields.get(i).type.getSize();
		}
		return locals;
	}

	int access;

	private Label beginLabel;

	private final ClassVisitor cv;
	private Stack<Field> localFields = new Stack<Field>();
	int[] locals;
	private String methodName;
	private List<Field> params = new ArrayList<>();
	private final Type returnType;
	List<Annotation> thisMethodAnnotations = new ArrayList<>();

	List<Annotation> thisParameteAnnotations = new ArrayList<>(10);

	final Type thisType;

	public AbstractMethodVistor(ClassVisitor cv, Type thisType, int access, String methodName) {
		this(cv, thisType, access, Type.VOID_TYPE, methodName);
	}

	public AbstractMethodVistor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName) {
		super(ASM5);
		this.cv = cv;
		this.thisType = thisType;
		this.methodName = methodName;
		this.access = access;
		this.returnType = returnType;
	}

	abstract C _code();

	abstract H _header();

	@Override
	public C accessLabel(Label label) {
		mv.visitLabel(label);
		return _code();
	}

	@Override
	public C accessLabel(Label label, int line) {
		mv.visitLabel(label);
		mv.visitLineNumber(line, label);
		return _code();
	}

	public C annotation(Class<?> annotation, String value) {
		thisMethodAnnotations.add(new Annotation(value, AsmBuilderHelper.typeOf(annotation)));
		return _code();
	}

	@Override
	public H annotation(Type type, String value) {
		ASMBuilder.visitAnnotation(mv, type, value);
		return _header();
	}

	// TODO
	public C begin() {
		return this.begin(10);
	}

	public C begin(int line) {

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
		return _code();
	}

	@Override
	public C block(Consumer<C> invocation) {
		invocation.accept(_code());
		return _code();
	}

	@Override
	public C code(Consumer<C> invocation) {
		begin();
		invocation.accept(_code());
		end();
		return _code();
	}

	@Override
	public C code(int line, Consumer<C> invocation) {
		begin(line);
		invocation.accept(_code());
		end();
		return _code();
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
	public C get(Field field) {
		ASMBuilder.visitGetField(mv, THIS, thisType, field.name, field.type);
		return _code();
	}

	@Override
	public C getProperty(Field field) {
		ASMBuilder.visitGetProperty(mv, THIS, thisType, field.name, field.type);
		return _code();
	}

	@Override
	public C initObject() {
		ASMBuilder.visitInitObject(mv, THIS);
		return _code();
	}

	@Override
	public C insn(int opcode) {
		mv.visitInsn(opcode);
		return _code();
	}

	@Override
	public void invoke(int invoketype, Type returnType, String methodName, Type... params) {
		ASMBuilder.visitInvoke(invoketype, mv, thisType, returnType, methodName, params);
	}

	@Override
	public C jumpInsn(int ifgt, Label label) {
		mv.visitJumpInsn(IFGT, label);
		return _code();
	}

	private Label label() {
		Label label = new Label();
		mv.visitLabel(label);
		return label;
	}

	private Label label(int line) {
		Label label = new Label();
		mv.visitLabel(label);
		mv.visitLineNumber(line, label);
		return label;
	}

	public C line(int line) {
		Label label = new Label();
		mv.visitLabel(label);
		mv.visitLineNumber(line, label);
		return _code();
	}

	@Override
	public C load(int... indexes) {
		for (int i : indexes) {
			mv.visitVarInsn(localFields.get(i).type.getOpcode(ILOAD), i);
		}
		return _code();
	}

	@Override
	public C localVariable(String name, Type type) {
		localFields.push(new Field(name, type));
		return _code();
	}

	@Override
	public Instance<C> me() {
		return (Instance<C>) this;
	}

	@Override
	public Label defineLabel() {
		Label label = new Label();
		return label;
	}

	@Override
	public H parameter(Field field) {
		params.add(field);
		return _header();
	}

	public C parameter(Field... fields) {
		for (Field field : fields) {
			params.add(field);
		}
		return _code();
	}

	@Override
	public H parameterAnnotation(int parameter, Type type, Object value) {
		thisParameteAnnotations.set(parameter, new Annotation(value, type));
		return _header();
	}

	@Override
	public C put(Field field) {
		ASMBuilder.visitPutField(mv, thisType, field.name, field.type);
		return _code();
	}

	@Override
	public C put(int dataIndex, Field field) {
		ASMBuilder.visitPutField(mv, THIS, thisType, dataIndex, field.name, field.type);
		return _code();
	}

	@Override
	public C put(int dataIndex, String fieldName, Type fieldType) {
		ASMBuilder.visitPutField(mv, THIS, thisType, dataIndex, fieldName, fieldType);
		return _code();
	}

	@Override
	public C returnObject() {
		ASMBuilder.visitReturnObject(mv);
		return _code();
	}

	@Override
	public C returnType(Type type) {
		ASMBuilder.visitReturnType(mv, type);
		return _code();
	}

	@Override
	public C returnVoid() {
		ASMBuilder.visitReturn(mv);
		return _code();
	}

}