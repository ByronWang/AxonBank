package com.nebula.cqrs.axon;

import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class ClassLoaderDebuger extends MyClassLoader {

	@Override
	public Class<?> define(String name, byte[] binaryRepresentation, boolean debug) {
		if (debug) {
			ClassReader cr = new ClassReader(binaryRepresentation);
			ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
			cr.accept(visitor, ClassReader.EXPAND_FRAMES);
		}
		return super.define(name, binaryRepresentation);
	}

}
