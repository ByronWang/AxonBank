package com.nebula.cqrs.core.asm;

import org.objectweb.asm.Type;

public interface MethodHeader extends Types {
    MethodHeader annotation(Type type, String value);

    default MethodCode begin() {
        return this.begin(10);
    }

    MethodCode begin(int line);

    MethodHeader parameter(Field field);

    default MethodHeader parameter(String fieldName, Type fieldType){
        return parameter(new Field(fieldName, fieldType));
    }

    default MethodHeader parameter(String fieldName, Class<?> clz) {
        return parameter(new Field(fieldName, typeOf(clz)));
    }

    MethodHeader parameterAnnotation(int parameter, Type type, Object value);;
}
