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

public abstract class AbstractMethodVistor<H, M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>> extends MethodVisitor
        implements MethodCode<M, C>, MethodHeader<H, C>, Opcodes {

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
		public void invoke(int invoketype, String methodName, Type... params) {
			ASMBuilder.visitInvoke(invoketype, mv, currentClassType, Type.VOID_TYPE, methodName, params);
		}

		@Override
		public Instance<C> invoke(int invoketype, Type returnType, String methodName, Type... params) {
			ASMBuilder.visitInvoke(invoketype, mv, currentClassType, returnType, methodName, params);
			return top(returnType);
		}

		@Override
		public C newInstace() {
			ASMBuilder.visitNewObject(mv, currentClassType);
			return _code();
		}

		@Override
		public C putTopTo(Field field) {
			ASMBuilder.visitPutField(mv, currentClassType, field.name, field.type);
			return _code();
		}
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
			Field var = variablesStack.get(dataIndex);
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

	abstract class RealUseCaller implements MethodUseCaller<M, C> {

		Type objectType;

		public RealUseCaller(Type objectType) {
			this.objectType = objectType;
		}

		@Override
		public M with(Consumer<C> invocation) {
			invocation.accept(_code());
			return caller();
		}

		@Override
		public M add(int varIndex) {
			load(varIndex);
			return caller();
		}

		abstract M caller();
		// @Override
		// public UseCaller<M,C> add(int varIndex) {
		// load(varIndex);
		// return this;
		// }

		@Override
		public C code() {
			return _code();
		}

		@Override
		public void invoke(int invoketype, String methodName, Type... params) {
			type(objectType).invoke(invoketype, Type.VOID_TYPE, methodName, params);
		}

		@Override
		public Instance<C> invoke(int invoketype, Type returnType, String methodName, Type... params) {
			return type(objectType).invoke(invoketype, returnType, methodName, params);
		}

		@Override
		public C putTopTo(Field field) {
			return type(objectType).putTopTo(field);
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

	boolean beFirstlabel = false;

	private Label beginLabel;

	MyClassType currentClassType = new MyClassType();

	MyInstance currentInstance = new MyInstance();

	private final ClassVisitor cv;
	int thisMethodAccess;

	List<Annotation> thisMethodAnnotations = new ArrayList<>();

	// final Type thisType;
	Class<?>[] thisMethodExceptionClasses;

	private String thisMethodName;

	List<Annotation> thisMethodParameterAnnotations = new ArrayList<>(10);

	private List<Field> thisMethodParams = new ArrayList<>();

	private final Type thisMethodReturnType;

	Type thisObjectType;

	int[] variablesInt;

	Map<String, Integer> variablesMap = new HashMap<>();

	// Type targetType;

	protected Stack<Field> variablesStack = new Stack<Field>();

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

	abstract M makeCaller(Type type);

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
		variablesStack.push(new Field(THIS_NAME, thisObjectType));
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (Field field : thisMethodParams) {
			variablesStack.push(field);
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

		String[] excptions;
		if (thisMethodExceptionClasses != null && thisMethodExceptionClasses.length > 0) {
			excptions = new String[thisMethodExceptionClasses.length];
			for (int i = 0; i < thisMethodExceptionClasses.length; i++) {
				excptions[i] = Type.getInternalName(thisMethodExceptionClasses[i]);
			}
		} else {
			excptions = new String[0];
		}

		this.mv = ASMBuilder.visitDefineMethod(cv, thisMethodAccess, thisMethodReturnType, thisMethodName, AsmBuilderHelper.typesOf(thisMethodParams),
		        signature, excptions);
		for (Annotation annotation : thisMethodAnnotations) {
			ASMBuilder.visitAnnotation(mv, annotation.type, annotation.value);
		}
		for (Annotation annotation : thisMethodParameterAnnotations) {
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
		for (Field field : variablesStack) {
			mv.visitLocalVariable(field.name, field.type.getDescriptor(), field.signature, beginLabel, endLabel, variablesInt[i++]);
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
			mv.visitVarInsn(variablesStack.get(i).type.getOpcode(ILOAD), i);
		}
	}

	@Override
	public C localVariable(String name, Type type, String signature) {
		variablesStack.push(new Field(name, type, signature));
		recomputerLocals();
		return _code();
	}

	@Override
	public Instance<C> object(int index) {
		currentInstance.index = index;
		Field var = variablesStack.get(index);
		mv.visitVarInsn(var.type.getOpcode(ILOAD), index);
		return top(var.type);
	}

	@Override
	public H parameter(String fieldName, Type fieldType, String signature) {
		thisMethodParams.add(new Field(fieldName, fieldType, signature));
		thisMethodParameterAnnotations.add(null);
		return _header();
	}

	@Override
	public H parameterAnnotation(Type type, Object value) {
		thisMethodParameterAnnotations.set(thisMethodParams.size() - 1, new Annotation(value, type));
		return _header();
	}

	void recomputerLocals() {
		this.variablesInt = computerLocalss(variablesStack);
		for (int i = 0; i < variablesStack.size(); i++) {
			variablesMap.put(variablesStack.get(i).name, i);
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

	protected Instance<C> top(Type type) {
		currentClassType.currentClassType = type;
		return currentInstance;
	}

	@Override
	public ClassType<C> type(Type objectType) {
		currentClassType.currentClassType = objectType;
		return currentClassType;
	}

	@Override
	public M use(int... varIndexes) {
		Field object = variablesStack.get(varIndexes[0]);
		M caller = makeCaller(object.type);
		load(varIndexes);
		return caller;
	}

	@Override
	public int var(String variableName) {
		return variablesMap.get(variableName);
	}

}