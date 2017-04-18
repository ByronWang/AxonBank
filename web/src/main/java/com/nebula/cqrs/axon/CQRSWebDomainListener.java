package com.nebula.cqrs.axon;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Command;

public class CQRSWebDomainListener implements DomainListener {

	@Override
	public void define(CQRSContext ctx, Type typeDomain, CQRSDomainBuilder cqrs, ClassReader domainClassReader) {
		// String name =
		// "org.axonframework.samples.bank.web.MyBankAccountController";
		Type typeController = Type.getObjectType(typeDomain.getInternalName() + "Controller");
		Type typeRepository = Type.getObjectType(typeDomain.getInternalName() + "Repository");
		Type typeEntry = Type.getObjectType(typeDomain.getInternalName() + "Entry");

		try {
			ctx.defineClass(typeEntry.getClassName(), PojoBuilder.dump(typeEntry, cqrs.domain.fields));

			for (Command command : cqrs.commands) {
				Type typeDto = Type.getObjectType(typeDomain.getInternalName() + CQRSDomainBuilder.toCamelUpper(command.actionName) + "Dto");
				ctx.defineClass(typeDto.getClassName(), PojoBuilder.dump(typeDto, command.fields));
			}

			ctx.defineClass(typeController.getClassName(), CQRSWebControllerBuilder.dump(typeDomain, typeController, typeRepository, typeEntry, cqrs));
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
