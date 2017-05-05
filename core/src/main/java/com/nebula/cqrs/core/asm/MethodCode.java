package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

public interface MethodCode extends Types {
    public MethodCode localVariable(String name, Type type);

    default MethodCode localVariable(String name, Class<?> clz) {
        return localVariable(name, typeOf(clz));
    }

    void thisInvokeVirtual(Type returnType, String methodName, Type... params);

    default void thisInvokeVirtual(String methodName, Type... params) {
        thisInvokeVirtual(Type.VOID_TYPE, methodName, params);
    }

    default void thisInvokeVirtual(Type returnType, String methodName, Class<?>... paramClasses) {
        thisInvokeVirtual(returnType, methodName, typesOf(paramClasses));
    }

    default void thisInvokeVirtual(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
        thisInvokeVirtual(typeOf(returnClass), methodName, typesOf(paramClasses));
    }

    void thisInvokeSpecial(Type returnType, String methodName, Type... params);

    default void thisInvokeSpecial(String methodName, Type... params) {
        thisInvokeSpecial(Type.VOID_TYPE, methodName, params);
    }

    default void thisInvokeSpecial(String methodName, Class<?>... paramClasses) {
        thisInvokeSpecial(Type.VOID_TYPE, methodName, typesOf(paramClasses));
    }

    default void thisInvokeSpecial(Type returnType, String methodName, Class<?>... paramClasses) {
        thisInvokeSpecial(returnType, methodName, typesOf(paramClasses));
    }

    default void thisInvokeSpecial(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
        thisInvokeSpecial(typeOf(returnClass), methodName, typesOf(paramClasses));
    }

    void thisInvokeInterface(Type returnType, String methodName, Type... params);

    default void thisInvokeInterface(String methodName, Type... params) {
        thisInvokeInterface(Type.VOID_TYPE, methodName, params);
    }

    default void thisInvokeInterface(Type returnType, String methodName, Class<?>... paramClasses) {
        thisInvokeInterface(returnType, methodName, typesOf(paramClasses));
    }

    default void thisInvokeInterface(Class<?> returnClass, String methodName, Class<?>... paramClasses) {
        thisInvokeInterface(typeOf(returnClass), methodName, typesOf(paramClasses));
    }

    void end();

    MethodCode insn(int d);

    public MethodCode line(int line);
    
    public MethodCode visit(Label label,int line);
    
    public MethodCode visit(Label label);

    MethodCode returnType(Type type);

    default MethodCode returnType(Class<?> returnClass) {
        return returnType(typeOf(returnClass));
    }

    MethodCode returnObject();

    MethodCode returnVoid();

    MethodCode thisInitObject();

    MethodCode thisPutField(int dataIndex, Field field);

    MethodCode thisPutField(Field field);

    default MethodCode thisPutField(String fieldName, Type fieldType) {
        return thisPutField(new Field(fieldName, fieldType));
    }

    default MethodCode thisPutField(String fieldName, Class<?> fieldClass) {
        return thisPutField(new Field(fieldName, typeOf(fieldClass)));
    }

    default MethodCode thisPutField(int dataIndex, String fieldName, Type fieldType) {
        return thisPutField(dataIndex, new Field(fieldName, fieldType));
    }

    default MethodCode thisPutField(int dataIndex, String fieldName, Class<?> fieldClass) {
        return thisPutField(dataIndex, new Field(fieldName, typeOf(fieldClass)));
    }

    MethodCode thisGetProperty(Field field);

    default MethodCode thisGetProperty(String fieldName, Type fieldType) {
        return thisGetProperty(new Field(fieldName, fieldType));
    }

    MethodCode thisGetField(Field field);

    default MethodCode thisGetField(String fieldName, Class<?> fieldClass) {
        return thisGetField(new Field(fieldName, typeOf(fieldClass)));
    }

    default MethodCode thisGetField(String fieldName, Type fieldType) {
        return thisGetField(new Field(fieldName, fieldType));

    }

    MethodCode load(int... index);

    MethodCode jumpInsn(int ifgt, Label label);
}
