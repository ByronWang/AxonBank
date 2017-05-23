package com.nebula.tinyasm.builder;

import org.objectweb.asm.ClassVisitor;

public interface ClassListener {
	ClassVisitor listen(Context context);
}