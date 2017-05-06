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

	@Override
	public C newInstace() {
		ASMBuilder.visitNewObject(mv, targetType);
		return _code();
	}

	Type targetType;

	@Override
	public Instance<C> object(Type targetType) {
		this.targetType = targetType;
		return (Instance<C>) this;
	}

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
	private List<Field> definedParams = new ArrayList<>();
	private final Type returnType;
	List<Annotation> thisMethodAnnotations = new ArrayList<>();
	List<Annotation> thisParameteAnnotations = new ArrayList<>(10);
	final Type thisType;

	public AbstractMethodVistor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		super(ASM5);
		this.cv = cv;
		this.thisType = thisType;
		this.methodName = methodName;
		this.access = access;
		this.returnType = returnType;
		this.exceptionClasses = exceptionClasses;
	}

	Class<?>[] exceptionClasses;

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
		this.thisMethodAnnotations.add(new Annotation(value, type));
		return _header();
	}

	private C begin() {
		String signature = null;
		boolean definedSignature = false;
		localFields.push(new Field(THIS_NAME, thisType));
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (Field field : definedParams) {
			localFields.push(field);
			if (field.signature != null) {
				sb.append(field.signature);
				definedSignature = true;
			} else {
				sb.append(field.type.getDescriptor());
			}
		}
		sb.append(")");
		sb.append(returnType.getDescriptor());
		String signatureFromParameter = sb.toString();

		if (definedSignature) {
			signature = signatureFromParameter;
		}

		locals = computerLocals(localFields);

		List<Field> params = new ArrayList<>();
		params.addAll(this.definedParams);

		String[] excptions;
		if (exceptionClasses != null && exceptionClasses.length > 0) {
			excptions = new String[exceptionClasses.length];
			for (int i = 0; i < exceptionClasses.length; i++) {
				excptions[i] = Type.getInternalName(exceptionClasses[i]);
			}
		} else {
			excptions = new String[0];
		}

		this.mv = ASMBuilder.visitDefineMethod(cv, access, returnType, methodName, AsmBuilderHelper.typesOf(params), signature, excptions);
		for (Annotation annotation : thisMethodAnnotations) {
			ASMBuilder.visitAnnotation(mv, annotation.type, annotation.value);
		}
		for (Annotation annotation : thisParameteAnnotations) {
			if (annotation != null) {
				ASMBuilder.visitParameterAnnotation(mv, annotation.parameter, annotation.type, annotation.value);
			}
		}
		mv.visitCode();
		beginLabel = labelWithoutLineNumber();
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
	public Label defineLabel() {
		Label label = new Label();
		return label;
	}

	public void end() {
		Label endLabel = this.labelWithoutLineNumber();
		int i = 0;
		for (Field field : localFields) {
			mv.visitLocalVariable(field.name, field.type.getDescriptor(), field.signature, beginLabel, endLabel, locals[i++]);
		}
		mv.visitMaxs(0, 0);
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
	public C insn(int opcode) {
		mv.visitInsn(opcode);
		return _code();
	}

	@Override
	public void invoke(int invoketype, Type returnType, String methodName, Type... params) {
		ASMBuilder.visitInvoke(invoketype, mv, targetType, returnType, methodName, params);
	}

	@Override
	public C jumpInsn(int ifgt, Label label) {
		mv.visitJumpInsn(IFGT, label);
		return _code();
	}

	private Label labelWithoutLineNumber() {
		Label label = new Label();
		mv.visitLabel(label);
		return label;
	}

	boolean beFirstlabel = false;

	public C line(int line) {
		Label label;
		if (!beFirstlabel) {
			label = new Label();
		} else {
			label = beginLabel;
		}
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
		targetType = thisType;
		return (Instance<C>) this;
	}

	@Override
	public H parameter(String fieldName, Type fieldType, String signature) {
		definedParams.add(new Field(fieldName, fieldType, signature));
		thisParameteAnnotations.add(null);
		return _header();
	}

	@Override
	public H parameterAnnotation(Type type, Object value) {
		thisParameteAnnotations.set(definedParams.size() - 1, new Annotation(value, type));
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