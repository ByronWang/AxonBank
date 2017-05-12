package com.nebula.tinyasm;

import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.IFGT;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.POP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.Instance;
import com.nebula.tinyasm.api.MethodCode;
import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.api.MethodUseCaller;
import com.nebula.tinyasm.api.Types;
import com.nebula.tinyasm.util.AsmBuilder;
import com.nebula.tinyasm.util.AsmBuilderHelper;
import com.nebula.tinyasm.util.Field;

public abstract class AbstractMethodVistor<H, M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>> extends MethodVisitor
        implements MethodCode<M, C>, MethodHeader<C>, Types {

	class Annotation {
		int parameter;

		final public Type type;

		final public String name;

		final public Object value;

		public Annotation(Type type, String name, Object value) {
			super();
			this.value = value;
			this.type = type;
			this.name = name;
		}

		public Annotation(Type type, String name, Object value, int parameter) {
			this(type, name, value);
			this.parameter = parameter;
		}
	}

	class MyInstance extends AbstractInvokeMethod<M, C> implements Instance<M, C> {
		private int topIndex;

		MyInstance(MethodVisitor mv) {
			super(mv);
		}

		@Override
		public C code() {
			return AbstractMethodVistor.this.code();
		}

		@Override
		public Instance<M, C> get(Field field) {
			AsmBuilder.visitGetField(mv, getType(), field.name, field.type);
			return AbstractMethodVistor.this.type(field.type);
		}

		public int getIndex() {
			return topIndex;
		}

		@Override
		public Type getType() {
			return AbstractMethodVistor.this.getTopType();
		}

		@Override
		public C put(int varIndex, Field field) {
			Field var = variablesStack.get(varIndex);
			accessVar(varIndex);
			mv.visitVarInsn(var.type.getOpcode(ILOAD), variablesLocals[varIndex]);
			AsmBuilder.visitPutField(mv, getType(), field.name, field.type);
			return code();
		}

		public void setIndex(int index) {
			this.topIndex = index;
		}

		@Override
		public M use() {
			return AbstractMethodVistor.this.useTop(thisMethodReturnType);
		}
	}

	abstract class RealUseCaller extends AbstractInvokeMethod<M, C> implements MethodUseCaller<M, C> {

		Type objectType;

		public RealUseCaller(MethodVisitor mv, Type objectType) {
			super(mv);
			this.objectType = objectType;
		}

		@Override
		public M add(int varIndex) {
			load(varIndex);
			return caller();
		}

		abstract M caller();

		@Override
		public C code() {
			return AbstractMethodVistor.this.code();
		}

		@Override
		public Type getType() {
			return objectType;
		}

		@Override
		public void returnMe() {
			returnTop(objectType);
		}

		@Override
		public M with(Consumer<C> invocation) {
			invocation.accept(code());
			return caller();
		}
	}

	final static int THIS = 0;

	final static String THIS_NAME = "this";

	MyInstance currentInstance;

	private final ClassVisitor cv;

	boolean hasEnded = false;

	private Label labelCurrent;

	boolean labelHasDefineBegin = false;

	int thisMethodAccess;

	List<Annotation> thisMethodAnnotations = new ArrayList<>();

	// final Type thisType;
	Class<?>[] thisMethodExceptionClasses;

	private String thisMethodName;

	List<Annotation> thisMethodParameterAnnotations = new ArrayList<>(10);

	private List<ClassField> thisMethodParams = new ArrayList<>();

	private final Type thisMethodReturnType;

	Type thisObjectType;

	Type topType;

	int[] variablesLocals;

	Map<String, Integer> variablesMap = new HashMap<>();

	protected Stack<Variable> variablesStack = new Stack<>();

	public AbstractMethodVistor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		super(ASM5);
		this.cv = cv;
		this.thisMethodName = methodName;
		this.thisMethodAccess = access;
		this.thisMethodReturnType = returnType;
		this.thisMethodExceptionClasses = exceptionClasses;
		this.thisObjectType = thisType;
	}

	@Override
	public C accessLabel(Label label) {
		labelCurrent = label;
		mv.visitLabel(label);
		return code();
	}

	@Override
	public C accessLabel(Label label, int line) {
		labelCurrent = label;
		mv.visitLabel(label);
		mv.visitLineNumber(line, label);
		return code();
	}

	private void accessVar(int... varIndexes) {
		for (int i : varIndexes) {
			Variable var = variablesStack.get(i);
			if (var.startFrom == null) {
				var.startFrom = labelCurrent;
			}
		}
	}

	public C annotation(Class<?> annotation, String value) {
		thisMethodAnnotations.add(new Annotation(AsmBuilderHelper.typeOf(annotation), null, value));
		return code();
	}

	@Override
	public MethodHeader<C> annotation(Type type, Object value) {
		this.thisMethodAnnotations.add(new Annotation(type, null, value));
		return this;
	}

	@Override
	public MethodHeader<C> annotation(Type type, String name, Object value) {
		this.thisMethodAnnotations.add(new Annotation(type, name, value));
		return this;
	}

	@Override
	public C begin() {
		doMethodDefine();
		doMethodBegin();
		return code();
	}

	@Override
	public C block(Consumer<C> invocation) {
		invocation.accept(code());
		return code();
	}

	@Override
	public C checkCast(Type type) {
		mv.visitTypeInsn(CHECKCAST, type.getInternalName());
		return code();
	}

	@Override
	public void code(Consumer<C> invocation) {
		invocation.accept(this.begin());
		this.end();
	}

	@Override
	public C def(String name, Type type, String signature) {
		variablesStack.push(new Variable(name, type, signature));
		recomputerLocals();
		return code();
	}

	@Override
	public Label defineLabel() {
		Label label = new Label();
		return label;
	}

	protected C doMethodBegin() {
		currentInstance = new MyInstance(mv);
		mv.visitCode();

		labelCurrent = labelWithoutLineNumber();
		// TODO add class sign
		variablesStack.push(new Variable(THIS_NAME, thisObjectType, null, labelCurrent));
		for (ClassField field : thisMethodParams) {
			variablesStack.push(new Variable(field, labelCurrent));
		}
		recomputerLocals();

		return code();
	}

	protected void doMethodDefine() {
		String signature = null;
		boolean definedSignature = false;
		{
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (ClassField param : thisMethodParams) {
				if (param.signature != null) {
					sb.append(param.signature);
					definedSignature = true;
				} else {
					sb.append(param.type.getDescriptor());
				}
			}
			sb.append(")");
			sb.append(this.thisMethodReturnType.getDescriptor());
			String signatureFromParameter = sb.toString();

			if (definedSignature) {
				signature = signatureFromParameter;
			}
		}

		String[] excptions;
		if (thisMethodExceptionClasses != null && thisMethodExceptionClasses.length > 0) {
			excptions = new String[thisMethodExceptionClasses.length];
			for (int i = 0; i < thisMethodExceptionClasses.length; i++) {
				excptions[i] = Type.getInternalName(thisMethodExceptionClasses[i]);
			}
		} else {
			excptions = new String[0];
		}

		this.mv = AsmBuilder.visitDefineMethod(cv, thisMethodAccess, thisMethodReturnType, thisMethodName, ClassField.typesOf(thisMethodParams), signature,
		        excptions);
		for (Annotation annotation : thisMethodAnnotations) {
			AsmBuilder.visitAnnotation(mv, annotation.type, annotation.name, annotation.value);
		}
		for (Annotation annotation : thisMethodParameterAnnotations) {
			if (annotation != null) {
				AsmBuilder.visitParameterAnnotation(mv, annotation.parameter, annotation.type, annotation.value);
			}
		}
	}

	public void doMethodEnd() {
		if (hasEnded) return;
		Label endLabel = this.labelWithoutLineNumber();
		int i = 0;
		for (Variable var : variablesStack) {
			if (!is(var.access, ACC_SYNTHETIC)) {
				mv.visitLocalVariable(var.name, var.type.getDescriptor(), var.signature, var.startFrom, endLabel, variablesLocals[i]);
			}
			i++;
		}
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		hasEnded = true;
	}

	@Override
	public void dup() {
		mv.visitInsn(DUP);
	}

	@Override
	public void end() {
		doMethodEnd();
	}

	protected Type getTopType() {
		return topType;
	}

	@Override
	public C insn(int opcode) {
		mv.visitInsn(opcode);
		return code();
	}

	@Override
	public C jumpInsn(int ifgt, Label label) {
		mv.visitJumpInsn(IFGT, label);
		return code();
	}

	private Label labelWithoutLineNumber() {
		Label label = new Label();
		labelCurrent = label;
		mv.visitLabel(label);
		return label;
	}

	@Override
	public void ldcInsn(Object cst) {
		mv.visitLdcInsn(cst);
	}

	public C line(int line) {
		Label label;
		if (!labelHasDefineBegin) {
			label = new Label();
			labelCurrent = label;
			mv.visitLabel(label);
		} else {
			label = labelCurrent;
		}
		mv.visitLineNumber(line, label);
		return code();
	}

	@Override
	public void load(int... indexes) {
		for (int index : indexes) {
			mv.visitVarInsn(variablesStack.get(index).type.getOpcode(ILOAD), variablesLocals[index]);
		}
		accessVar(indexes);
	}

	@Override
	public Instance<M, C> newInstace(Type type) {
		AsmBuilder.visitNewObject(mv, type);
		return type(type);
	}

	@Override
	public Instance<M, C> object(int index) {
		accessVar(index);
		Field var = variablesStack.get(index);
		mv.visitVarInsn(var.type.getOpcode(ILOAD), variablesLocals[index]);
		return type(var.type);
	}

	@Override
	public MethodHeader<C> parameter(ClassField field) {
		thisMethodParams.add(field);
		thisMethodParameterAnnotations.add(null);
		return this;
	}

	@Override
	public MethodHeader<C> parameter(String fieldName, Type fieldType) {
		thisMethodParams.add(new Variable(fieldName, fieldType));
		thisMethodParameterAnnotations.add(null);
		return this;
	}

	@Override
	public MethodHeader<C> parameter(String fieldName, Type fieldType, String signature) {
		thisMethodParams.add(new Variable(fieldName, fieldType, signature));
		thisMethodParameterAnnotations.add(null);
		return this;
	}

	@Override
	public MethodHeader<C> parameter(Type annotationType, Object value, String fieldName, Type fieldType) {
		thisMethodParams.add(new Variable(fieldName, fieldType));
		thisMethodParameterAnnotations.set(thisMethodParams.size() - 1, new Annotation(annotationType, null, value));
		return this;
	}

	@Override
	public MethodHeader<C> parameter(Type annotationType, Object value, String fieldName, Type fieldType, String signature) {
		thisMethodParams.add(new Variable(fieldName, fieldType, signature));
		thisMethodParameterAnnotations.set(thisMethodParams.size() - 1, new Annotation(annotationType, null, value));
		return this;
	}

	@Override
	public MethodHeader<C> parameterAnnotation(Type annotationType, Object value) {
		thisMethodParameterAnnotations.set(thisMethodParams.size() - 1, new Annotation(annotationType, null, value));
		return this;
	}

	@Override
	public void pop() {
		mv.visitInsn(POP);
	}

	@Override
	public C putTopTo(Field field) {
		AsmBuilder.visitPutField(mv, getTopType(), field.name, field.type);
		return code();
	}

	void recomputerLocals() {
		this.variablesLocals = Types.computerVariableLocals(variablesStack);
		for (int i = 0; i < variablesStack.size(); i++) {
			variablesMap.put(variablesStack.get(i).name, i);
		}
	}

	@Override
	public void returnObject() {
		AsmBuilder.visitReturnObject(mv);
	}

	@Override
	public void returnTop(Type type) {
		AsmBuilder.visitReturnType(mv, type);
	}

	@Override
	public void returnVoid() {
		AsmBuilder.visitReturn(mv);
	}

	@Override
	public C storeTop(int index) {
		Field var = variablesStack.get(index);
		mv.visitVarInsn(var.type.getOpcode(ISTORE), variablesLocals[index]);
		return code();
	}

	public Instance<M, C> type(Type type) {
		this.topType = type;
		return currentInstance;
	}

	@Override
	public M use(int... varIndexes) {
		Field object = variablesStack.get(varIndexes[0]);
		load(varIndexes);
		accessVar(varIndexes);
		return useTop(object.type);
	}

	@Override
	public int varIndex(String variableName) {
		return variablesMap.get(variableName);
	}

}