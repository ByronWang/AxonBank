package com.nebula.cqrs.core.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class AbstractMethodVistor<H, C> extends MethodVisitor implements MethodCode<C>, MethodHeader<H, C>, Opcodes {

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

	class MyClassType implements ClassType<C> {
		Type currentClassType;

		@Override
		public C newInstace() {
			ASMBuilder.visitNewObject(mv, currentClassType);
			return _code();
		}

		@Override
		public void invoke(int invoketype, String methodName, Type... params) {
			ASMBuilder.visitInvoke(invoketype, mv, currentClassType, Type.VOID_TYPE, methodName, params);
		}

		@Override
		public Instance<C> invoke(int invoketype, Type returnType, String methodName, Type... params) {
			ASMBuilder.visitInvoke(invoketype, mv, currentClassType, returnType, methodName, params);
			return top(returnType);
		}

		@Override
		public C putTopTo(Field field) {
			ASMBuilder.visitPutField(mv, currentClassType, field.name, field.type);
			return _code();
		}
	}

	protected Instance<C> top(Type type) {
		currentClassType.currentClassType = type;
		return currentInstance;
	}

	class MyInstance implements Instance<C> {
		int index;

		@Override
		public Instance<C> get(Field field) {
			ASMBuilder.visitGetField(mv, currentClassType.currentClassType, field.name, field.type);
			return top(field.type);
		}

		@Override
		public Instance<C> getProperty(Field field) {
			ASMBuilder.visitGetProperty(mv, currentClassType.currentClassType, field.name, field.type);
			return top(field.type);
		}

		@Override
		public void invoke(int invoketype, String methodName, Type... params) {
			ASMBuilder.visitInvoke(invoketype, mv, currentClassType.currentClassType, Type.VOID_TYPE, methodName, params);
		}

		@Override
		public Instance<C> invoke(int invoketype, Type returnType, String methodName, Type... params) {
			ASMBuilder.visitInvoke(invoketype, mv, currentClassType.currentClassType, returnType, methodName, params);
			return top(returnType);
		}

		@Override
		public C put(int dataIndex, Field field) {
			Field var = localFields.get(dataIndex);
			mv.visitVarInsn(var.type.getOpcode(ILOAD), dataIndex);
			ASMBuilder.visitPutField(mv, currentClassType.currentClassType, field.name, field.type);
			return _code();
		}

		@Override
		public C putTopTo(Field field) {
			ASMBuilder.visitPutField(mv, currentClassType.currentClassType, field.name, field.type);
			return _code();
		}
	}

	final static int THIS = 0;

	final static String THIS_NAME = "this";

	static int[] computerLocalss(List<Field> fields) {
		int[] locals = new int[fields.size()];
		int cntLocal = 0;
		for (int i = 0; i < fields.size(); i++) {
			locals[i] = cntLocal;
			cntLocal += fields.get(i).type.getSize();
		}
		return locals;
	}

	int thisMethodAccess;

	boolean beFirstlabel = false;

	private Label beginLabel;
	MyClassType currentClassType = new MyClassType();

	MyInstance currentInstance = new MyInstance();

	private final ClassVisitor cv;

	private List<Field> definedParams = new ArrayList<>();

	// final Type thisType;
	Class<?>[] thisMethodExceptionClasses;

	protected Stack<Field> localFields = new Stack<Field>();

	int[] locals;

	Map<String, Integer> localVariables = new HashMap<>();

	private String thisMethodName;

	private final Type thisMethodReturnType;

	// Type targetType;

	List<Annotation> thisMethodAnnotations = new ArrayList<>();

	List<Annotation> thisParameteAnnotations = new ArrayList<>(10);

	Type thisObjectType;

	public AbstractMethodVistor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		super(ASM5);
		this.cv = cv;
		this.thisMethodName = methodName;
		this.thisMethodAccess = access;
		this.thisMethodReturnType = returnType;
		this.thisMethodExceptionClasses = exceptionClasses;
		this.thisObjectType = thisType;
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
		this.thisMethodAnnotations.add(new Annotation(value, type));
		return _header();
	}

	private C begin() {
		String signature = null;
		boolean definedSignature = false;
		localFields.push(new Field(THIS_NAME, thisObjectType));
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
		sb.append(this.thisMethodReturnType.getDescriptor());
		String signatureFromParameter = sb.toString();

		if (definedSignature) {
			signature = signatureFromParameter;
		}
		recomputerLocals();

		List<Field> params = new ArrayList<>();
		params.addAll(this.definedParams);

		String[] excptions;
		if (thisMethodExceptionClasses != null && thisMethodExceptionClasses.length > 0) {
			excptions = new String[thisMethodExceptionClasses.length];
			for (int i = 0; i < thisMethodExceptionClasses.length; i++) {
				excptions[i] = Type.getInternalName(thisMethodExceptionClasses[i]);
			}
		} else {
			excptions = new String[0];
		}

		this.mv = ASMBuilder.visitDefineMethod(cv, thisMethodAccess, thisMethodReturnType, thisMethodName, AsmBuilderHelper.typesOf(params), signature,
		        excptions);
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
	public C insn(int opcode) {
		mv.visitInsn(opcode);
		return _code();
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
	public void load(int... indexes) {
		for (int i : indexes) {
			mv.visitVarInsn(localFields.get(i).type.getOpcode(ILOAD), i);
		}
	}

	@Override
	public C localVariable(String name, Type type, String signature) {
		localFields.push(new Field(name, type, signature));
		recomputerLocals();
		return _code();
	}

	@Override
	public Instance<C> object(int index) {
		currentInstance.index = index;
		Field var = localFields.get(index);
		mv.visitVarInsn(var.type.getOpcode(ILOAD), index);
		return top(var.type);
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

	void recomputerLocals() {
		this.locals = computerLocalss(localFields);
		for (int i = 0; i < localFields.size(); i++) {
			localVariables.put(localFields.get(i).name, i);
		}
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

	@Override
	public C store(int index) {
		mv.visitVarInsn(ASTORE, index);
		return _code();
	}

	@Override
	public ClassType<C> type(Type objectType) {
		currentClassType.currentClassType = objectType;
		return currentClassType;
	}

	@Override
	public int var(String variableName) {
		return localVariables.get(variableName);
	}

}