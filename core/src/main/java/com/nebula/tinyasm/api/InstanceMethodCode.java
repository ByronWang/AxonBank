package com.nebula.tinyasm.api;

import org.objectweb.asm.Type;

public interface InstanceMethodCode <M extends MethodUseCaller<M, C>, C extends MethodCode<M, C>>  extends MethodCode<M, C> {
	final static int THIS = 0;
	final static String THIS_NAME = "this";
	
	Type thisType();
	M useThis();
	M useTopThis();
}
