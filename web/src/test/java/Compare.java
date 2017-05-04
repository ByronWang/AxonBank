
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class Compare {

    public static void main(String[] args) throws IOException {
        nav(new File("D:\\CQRS\\test"));
    }

    static void nav(File folder) throws FileNotFoundException, IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                nav(file);

            } else if (file.getName().endsWith(".class")) {
                par(file);
            }
        }
    }

    static void par(File file) throws FileNotFoundException, IOException {
        ClassReader cr = new ClassReader(new FileInputStream(file));
        File outFile = new File(file.getParentFile(), file.getName().replace(".class", "Dump.java"));
        FileOutputStream out = new FileOutputStream(outFile);
        ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(out));
        cr.accept(visitor, ClassReader.EXPAND_FRAMES);
        out.close();
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
