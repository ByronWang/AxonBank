

import java.io.IOException;
import java.io.PrintWriter;

import org.axonframework.samples.bank.config.AxonConfig;
import org.axonframework.samples.bank.query.bankaccount.BankAccountRepository;
import org.axonframework.samples.bank.web.BankAccountController;
import org.axonframework.samples.bank.web.dto.BankAccountDto;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class TodoTest {

	@Test
	public void testPrintTodo() throws IOException {
		ClassReader cr = new ClassReader(TestPrint.class.getName());
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}

	static class MyClassLoader extends ClassLoader {
		public Class<?> defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}
		
		public void doResolveClass(Class<?> clz){
			super.resolveClass(clz);
		}
	}
}
