package com.nebula.cqrs.axon;

public interface CQRSContext {
	public Class<?> loadClass(String name) throws ClassNotFoundException;
	public <T> Class<T> defineClass(String name, byte[] b);
	public void doResolveClass(Class<?> clz) ;
}