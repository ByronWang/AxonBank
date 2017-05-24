package com.nebula.tinyasm.builder;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
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

public class DomainBuilder implements Context {

	final ClassReader cr;
	final DomainDefinition domainDefinition;
	final ClassBody classBody;

	final Map<String, ClassBody> types;

	public DomainBuilder(String srcDomainName, Type srcDomainType, ClassReader cr) {
		this.cr = cr;
		this.domainDefinition = new DomainDefinition(srcDomainName, srcDomainType);
		this.classBody = ClassBuilder.make(domainDefinition.implDomainType);
		this.types = new HashMap<>();
		initDefinition(cr, domainDefinition);
	}

	public static DomainDefinition initDefinition(final ClassReader cr, final DomainDefinition domainDefinition) {
		{
			AnalyzeMethodParamsClassVisitor analyzeMethodParamsClassVisitor = new AnalyzeMethodParamsClassVisitor();
			AnalyzeFieldClassVisitor analyzeFieldClassVisitor = new AnalyzeFieldClassVisitor(analyzeMethodParamsClassVisitor);
			cr.accept(analyzeFieldClassVisitor, 0);
			domainDefinition.menthods = analyzeMethodParamsClassVisitor.getMethods();
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

	public void visit(ClassListener classListener) {
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
		for (ClassBody cb : types.values()) {
			classLoader.define(cb.getType().getClassName(), cb.end().toByteArray());
		}
	}

	@Override
	public ClassBody get(Type type) {
		return this.types.get(type.getClassName());
	}
}
