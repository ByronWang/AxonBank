package com.nebula.cqrs.axonweb.asm.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import com.nebula.cqrs.axonweb.asm.query.test.MyBankAccountCreatedEvent;
import com.nebula.cqrs.axonweb.asm.query.test.MyBankAccountImpl;
import com.nebula.cqrs.axonweb.asm.query.test.MyBankAccountImplEntry;
import com.nebula.cqrs.core.asm.RenameClassVisitor;

public class CQRSWebEventListnerBizLogicClassVisitorTest {
	MyClassLoader classLoader;

	@Before
	public void setUp() throws Exception {
		classLoader = new MyClassLoader();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException,
			InvocationTargetException {

		ClassReader cr = new ClassReader(MyBankAccountImpl.class.getName());

		Type logicType = Type.getObjectType(MyBankAccountImpl.class.getName().replace('.', '/') + "Logic");
		Type entryType = Type.getObjectType(MyBankAccountImpl.class.getName().replace('.', '/') + "Entry");

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

		ClassVisitor visitor = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		CQRSWebEventListnerBizLogicClassVisitor entityEventListener = new CQRSWebEventListnerBizLogicClassVisitor(Opcodes.ASM5, visitor, logicType, entryType);
		RenameClassVisitor renameClassVisitor = new RenameClassVisitor(entityEventListener, MyBankAccountImpl.class.getName().replace('.', '/'),
				logicType.getInternalName());
		cr.accept(renameClassVisitor, ClassReader.EXPAND_FRAMES);

		byte[] code = cw.toByteArray();

		Class<?> c = this.classLoader.defineClass(logicType.getClassName(), code);
		Object logic = c.newInstance();

		MyBankAccountImplEntry entry = new MyBankAccountImplEntry();
		MyBankAccountCreatedEvent event = new MyBankAccountCreatedEvent("33", 100) {
		};

		Method method = c.getMethod("on", MyBankAccountCreatedEvent.class, MyBankAccountImplEntry.class);
		System.out.println(entry);
		method.invoke(logic, event, entry);
		System.out.println(entry);
	}

	static class MyClassLoader extends ClassLoader {
		public Class<?> defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}

		public void doResolveClass(Class<?> clz) {
			super.resolveClass(clz);
		}
	}
}
