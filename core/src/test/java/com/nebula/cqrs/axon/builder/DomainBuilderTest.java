package com.nebula.cqrs.axon.builder;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.tinyasm.ClassBuilder;
import com.nebula.tinyasm.util.AsmBuilder;

public class DomainBuilderTest {

	public static void main(String[] args) throws IOException {
		Type domainType = Type.getType(BankAccount.class);

		ClassReader cr = new ClassReader(BankAccount.class.getName());

		String srcDomainName = AsmBuilder.toSimpleName(domainType.getClassName());

		DomainBuilder domainBuilder = new DomainBuilder(srcDomainName, domainType, cr);

		DomainDefinition dd = domainBuilder.getDomainDefinition();
//		domainBuilder.add("impl", ClassBuilder.make(dd.implDomainType).fields(dd.fields));

		domainBuilder.visit(new CQRSEventClassListener());
		domainBuilder.visit(new CQRSCommandClassListener());

		// SagaClassListener saga = new SagaClassListener();
		// domainBuilder.visit(saga);

		domainBuilder.finished();
	}
}
