package com.nebula.cqrs.core.asm.wrap;

import org.objectweb.asm.Type;

public interface Types {
    default Type typeOf(Class<?> clz) {
        return Type.getType(clz);
    }

    default Type[] typesOf(Class<?>... classes) {
        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = Type.getType(classes[i]);
        }
        return types;
    }
}
