package com.nebula.cqrs.core.asm.wrap;

import java.util.function.Consumer;
import java.util.function.Function;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import com.nebula.cqrs.core.asm.Field;

interface MethodCode<M extends MethodUseCaller<M, C> , C extends MethodCode<M, C>> extends Types {

	C accessLabel(Label label);

	C accessLabel(Label label, int line);

	C block(Consumer<C> invocation);

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

	C insn(int d);

	C jumpInsn(int ifgt, Label label);

	C line(int line);

	void load(int... index);

	default void load(String... varNames) {
		load(vars(varNames));
	}

	Instance<M,C> newInstace(Type type);

	Instance<M,C> object(int index);

	default Instance<M,C> object(String variableName) {
		return object(varIndex(variableName));
	}

	C putTopTo(Field field);

	void returnObject();

	default void returnTop(Class<?> returnClass) {
		returnTop(typeOf(returnClass));
	}

	@Deprecated
	void returnTop(Type type);

	void returnVoid();

	C storeTop(int index);

	default C storeTop(String varName) {
		return storeTop(varIndex(varName));
	}

	default ClassType<M,C> type(Class<?> returnClass) {
		return type(typeOf(returnClass));
	}

	ClassType<M,C> type(Type objectType);

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