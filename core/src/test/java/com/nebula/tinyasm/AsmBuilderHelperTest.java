package com.nebula.tinyasm;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class AsmBuilderHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMyBankAccountBuilder() throws Exception {
		byte[] code = MyBankAccountBuilder.dump();
		byte[] codeExpected = MyBankAccountDump.dump();

		String strCode = toString(code);
		String strCodeExpected = toString(codeExpected);
		assertEquals("Code", strCodeExpected, strCode);
	}

	@Test
	public void testMyBankAccountCommandHandlerBuilder_2() throws Exception {
		byte[] code = MyBankAccountCommandHandlerBuilder.dump();
		byte[] codeExpected = MyBankAccountCommandHandlerDump.dump();

		String strCode = toString(code);
		String strCodeExpected = toString(codeExpected);
		assertEquals("Code", strCodeExpected, strCode);
	}

	@Test
	public void testStatusBuilder() throws Exception {
		byte[] code = StatusBuilder.dump();
		byte[] codeExpected = StatusDump.dump();

		String strCode = toString(code);
		String strCodeExpected = toString(codeExpected);
		assertEquals("Code", strCodeExpected, strCode);
	}

	public static String toString(byte[] code) throws IOException {
		ClassReader cr = new ClassReader(code);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), pw);
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
		return sw.toString();
	}
}
