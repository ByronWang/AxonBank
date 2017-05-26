package com.nebula.cqrs.axon.builder;

import static org.objectweb.asm.Opcodes.ASM5;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.ClassLoaderDebuger;
import com.nebula.cqrs.axon.MyClassLoader;
import com.nebula.cqrs.axon.asm.AnalyzeEventsClassVisitor;
import com.nebula.cqrs.axon.asm.AnalyzeFieldClassVisitor;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.api.ClassBody;
import com.nebula.tinyasm.util.AnalyzeMethodParamsClassVisitor;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.RenameClassVisitor;

public class DomainBuilder implements DomainContext {

	final ClassReader cr;
	final DomainDefinition domainDefinition;
	// final ClassBody classBody;

	final Map<String, ClassBody> types;

	public DomainBuilder(String srcDomainName, Type srcDomainType, ClassReader cr) {
		this.domainDefinition = new DomainDefinition(srcDomainName, srcDomainType);
		initDefinition(cr, domainDefinition);
		this.cr = init(cr, srcDomainType, domainDefinition.implDomainType);
		this.types = new HashMap<>();

		ClassBuilder domainObject = (ClassBuilder) ClassBuilder.make();
		CQRSDomainFilterClassVisitor domainFilterClassVisitor = new CQRSDomainFilterClassVisitor(domainObject);
		this.accept(domainFilterClassVisitor);
		this.add("impl", domainObject);
	}

	private static ClassReader init(ClassReader cr, Type domainType, Type implType) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		RenameClassVisitor renameClassVisitor = new RenameClassVisitor(cw, domainType.getInternalName(), implType.getInternalName());
		cr.accept(renameClassVisitor, 0);
		ClassReader newcr = new ClassReader(cw.toByteArray());
		return newcr;
	}

	public static DomainDefinition initDefinition(final ClassReader cr, final DomainDefinition domainDefinition) {
		{
			AnalyzeMethodParamsClassVisitor analyzeMethodParamsClassVisitor = new AnalyzeMethodParamsClassVisitor();
			AnalyzeFieldClassVisitor analyzeFieldClassVisitor = new AnalyzeFieldClassVisitor(analyzeMethodParamsClassVisitor);
			cr.accept(analyzeFieldClassVisitor, 0);
			domainDefinition.methods = analyzeMethodParamsClassVisitor.getMethods();
			domainDefinition.fields = analyzeFieldClassVisitor.finished().toArray(new Field[0]);
			for (Field field : domainDefinition.fields) {
				if (field.identifier) {
					domainDefinition.identifierField = field;
				}
			}

		}

		{
			AnalyzeEventsClassVisitor analyzeEventsClassVisitor = new AnalyzeEventsClassVisitor(domainDefinition);
			cr.accept(analyzeEventsClassVisitor, 0);
			domainDefinition.realEvents = analyzeEventsClassVisitor.finished();
		}

		return domainDefinition;
	}

	public void accept(ClassVisitor classVisitor) {
		cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
	}

	public void visit(DomainListener classListener) {
		cr.accept(classListener.listen(this), ClassReader.SKIP_FRAMES);
	}

	@Override
	public ClassBody add(String name, ClassBody cb) {
		this.types.put(name, cb);
		return cb;
	}

	@Override
	public ClassBody get(String name) {
		return this.types.get(name);
	}

	MyClassLoader classLoader = new ClassLoaderDebuger();

	@Override
	public DomainDefinition getDomainDefinition() {
		return this.domainDefinition;
	}

	@Override
	public ClassBody add(ClassBody cb) {
		this.types.put(cb.getType().getClassName(), cb);
		return cb;
	}

	public void finished() {
		try {
			for (ClassBody cb : types.values()) {
				classLoader.load(cb.getType().getClassName(), cb.end().toByteArray());
			}
			for (ClassBody cb : types.values()) {
				classLoader.loadClass(cb.getType().getClassName());
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ClassBody get(Type type) {
		return this.types.get(type.getClassName());
	}

	@Override
	public void read(final String provideName, MethodProvider provider) {
		this.cr.accept(new ClassVisitor(ASM5) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				if (provideName.equals(name)) {
					return provider.visitMethod(access, name, desc, signature, exceptions);
				} else {
					return null;
				}
			}
		}, ClassReader.SKIP_FRAMES);
	}

	interface MethodProvider {
		MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions);
	}
}
