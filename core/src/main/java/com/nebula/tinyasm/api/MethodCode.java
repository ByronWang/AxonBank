package com.nebula.tinyasm.api;

import java.util.function.Consumer;
import java.util.function.Function;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface MethodCode<M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>> extends Types {

	C accessLabel(Label label);

	C accessLabel(Label label, int line);

	C block(Consumer<C> invocation);

	C checkCast(Type type);

	C code();

	default C def(Field field) {
		return def(field.name, field.type);
	}

	default C def(String fieldName, Class<?> clz) {
		return def(fieldName, typeOf(clz));
	}

	default C def(String fieldName, Class<?> clz, Class<?>... signatureClasses) {
		return def(fieldName, typeOf(clz), typesOf(signatureClasses));
	}

	default C def(String fieldName, Class<?> clz, String signature) {
		return def(fieldName, typeOf(clz), signature);
	}

	default C def(String fieldName, Class<?> clz, Type... signatureTypes) {
		return def(fieldName, typeOf(clz), signatureTypes);
	}

	C def(String fieldName, Type fieldType, String signature);

	default C def(String fieldName, Type fieldType, Type... signatureTypes) {
		String signature = null;
		if (signatureTypes != null && signatureTypes.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("L");
			sb.append(fieldType.getInternalName());
			sb.append("<");
			for (Type signatureType : signatureTypes) {
				sb.append(signatureType.getDescriptor());
			}
			sb.append(">;");
			signature = sb.toString();
		}
		return def(fieldName, fieldType, signature);
	}

	Label defineLabel();

	void dup();

	void end();

	C insn(int d);

	C jumpInsn(int ifgt, Label label);

	void ldcInsn(Object cst);

	C line(int line);

	void load(int... index);

	default void load(String... varNames) {
		load(vars(varNames));
	}

	default Instance<M, C> newInstace(Class<?> clz) {
		return newInstace(Type.getType(clz));
	}

	Instance<M, C> newInstace(Type type);

	Instance<M, C> object(int index);

	default Instance<M, C> object(String variableName) {
		return object(varIndex(variableName));
	}

	void pop();

	C putTopTo(Field field);

	void returnObject();

	default void returnTop(Class<?> returnClass) {
		returnTop(typeOf(returnClass));
	}

	void returnTop(Type type);

	void returnVoid();

	C storeTop(int index);

	default C storeTop(String varName) {
		return storeTop(varIndex(varName));
	}

	default Instance<M, C> type(Class<?> objectClass) {
		return type(typeOf(objectClass));
	}

	Instance<M, C> type(Type objectType);

	default M use(Function<C, ToType> func) {
		ToType toType = func.apply(code());
		return useTop(toType.getType());
	};

	M use(int... varIndex);;

	default M use(String... varNames) {
		return use(vars(varNames));
	}

	M useTop(Type type);

	int varIndex(String variableName);

	default int[] vars(String... varNames) {
		int[] vars = new int[varNames.length];
		for (int i = 0; i < varNames.length; i++) {
			vars[i] = varIndex(varNames[i]);
		}
		return vars;
	}

}