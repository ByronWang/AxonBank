package com.nebula.cqrs.core.asm;

import java.util.function.Consumer;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

interface MethodCode<M, C extends MethodCode<M, C>> extends Types {

	C accessLabel(Label label);

	C accessLabel(Label label, int line);

	C block(Consumer<C> invocation);

	Label defineLabel();

	C insn(int d);

	M use(int... varIndex);

	default M use(String... varNames) {
		return use(vars(varNames));
	}

	Instance<C> object(int index);

	int var(String variableName);

	default int[] vars(String... varNames) {
		int[] vars = new int[varNames.length];
		for (int i = 0; i < varNames.length; i++) {
			vars[i] = var(varNames[i]);
		}
		return vars;
	}

	default Instance<C> object(String variableName) {
		return object(var(variableName));
	}

	ClassType<C> type(Type objectType);

	default ClassType<C> type(Class<?> returnClass) {
		return type(typeOf(returnClass));
	}

	C jumpInsn(int ifgt, Label label);

	C line(int line);

	void load(int... index);

	default void load(String... varNames) {
		load(vars(varNames));
	}

	C store(int index);

	default C store(String varName) {
		return store(var(varName));
	}

	default C localVariable(Field field) {
		return localVariable(field.name, field.type);
	}

	default C localVariable(String fieldName, Class<?> clz) {
		return localVariable(fieldName, typeOf(clz));
	}

	default C localVariable(String fieldName, Class<?> clz, Class<?>... signatureClasses) {
		return localVariable(fieldName, typeOf(clz), typesOf(signatureClasses));
	}

	default C localVariable(String fieldName, Class<?> clz, String signature) {
		return localVariable(fieldName, typeOf(clz), signature);
	}

	default C localVariable(String fieldName, Class<?> clz, Type... signatureTypes) {
		return localVariable(fieldName, typeOf(clz), signatureTypes);
	}

	C localVariable(String fieldName, Type fieldType, String signature);;

	default C localVariable(String fieldName, Type fieldType, Type... signatureTypes) {
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
		return localVariable(fieldName, fieldType, signature);
	};

	C returnObject();

	default C returnType(Class<?> returnClass) {
		return returnType(typeOf(returnClass));
	}

	C returnType(Type type);

	C returnVoid();

}