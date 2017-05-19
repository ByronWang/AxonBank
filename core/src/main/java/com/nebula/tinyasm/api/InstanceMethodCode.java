package com.nebula.tinyasm.api;

public interface InstanceMethodCode <M, C extends MethodCode<M, C>>  extends MethodCode<M, C> {
	final static int THIS = 0;
	final static String THIS_NAME = "this";
	
	M useThis();
	M useTopThis();
}
