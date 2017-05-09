package com.nebula.tinyasm.api;

import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.util.Field;

public interface InvokeMethod<M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>> extends Types, ToType {

	default void invoke(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}
	
	C code();

	default Instance<M, C> topInstance(){
		return code().type(getType());
	}

	C putTo(Field field);

	Instance<M, C>  get(Field field);
	
	default C putTo(String fieldName, Class<?> fieldClass) {
		return putTo(new Field(fieldName, typeOf(fieldClass)));
	}

	default C putTo(String fieldName, Type fieldType) {
		return putTo(new Field(fieldName, fieldType));
	}

	default void invoke(int invoketype, String methodName, Type... params) {
		invoke(invoketype, Type.VOID_TYPE, methodName, params);
	}

	void invoke(Type objectType, int invoketype, Type returnType, String methodName, Type... params);

	default void invoke(Type objectType, int invoketype, String methodName, Type... params){
		invoke(objectType, invoketype, Type.VOID_TYPE, methodName, params);
	}

	default Instance<M, C> invoke(int invoketype, Type returnType, String methodName, Type... params) {
		invoke(getType(), invoketype, returnType, methodName, params);
		return topInstance();
	}

	default Instance<M, C> invokeInterface(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEINTERFACE, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeInterface(String methodName, Class<?>... paramClasses) {
		invoke(INVOKEINTERFACE, methodName, typesOf(paramClasses));
	}

	default void invokeInterface(String methodName, Type... params) {
		invoke(INVOKEINTERFACE, methodName, params);
	}

	default Instance<M, C> invokeInterface(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEINTERFACE, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeInterface(Type returnType, String methodName, Type... params) {
		return invoke(INVOKEINTERFACE, returnType, methodName, params);
	}

	default Instance<M, C> invokeSpecial(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKESPECIAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(String methodName, Class<?>... paramClasses) {
		invoke(INVOKESPECIAL, methodName, typesOf(paramClasses));
	}

	default void invokeSpecial(String methodName, Type... params) {
		invoke(INVOKESPECIAL, methodName, params);
	}

	default Instance<M, C> invokeSpecial(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKESPECIAL, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeSpecial(Type returnType, String methodName, Type... params) {
		return invoke(INVOKESPECIAL, returnType, methodName, params);
	}

	default Instance<M, C> invokeVirtual(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEVIRTUAL, typeOf(returnClass), methodName, typesOf(paramClasses));
	}

	default void invokeVirtual(String methodName, Class<?>... paramClasses) {
		invoke(INVOKEVIRTUAL, methodName, typesOf(paramClasses));
	}

	default void invokeVirtual(String methodName, Type... params) {
		invoke(INVOKEVIRTUAL, methodName, params);
	}

	default Instance<M, C> invokeVirtual(Type returnType, String methodName, Class<?>... paramClasses) {
		return invoke(INVOKEVIRTUAL, returnType, methodName, typesOf(paramClasses));
	}

	default Instance<M, C> invokeVirtual(Type returnType, String methodName, Type... params) {
		return invoke(INVOKEVIRTUAL, returnType, methodName, params);
	}

	default Instance<M, C> getProperty(String fieldName, Type fieldType) {
		return invoke(INVOKEVIRTUAL, fieldType, toPropertyGetName(fieldName));
	}

	default String toPropertyGetName(String fieldName) {
		return "get" + toPropertyName(fieldName);
	}

	default String toPropertyName(String fieldName) {
		return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}

}
