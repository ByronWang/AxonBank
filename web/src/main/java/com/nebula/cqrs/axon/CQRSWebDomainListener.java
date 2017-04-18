package com.nebula.cqrs.axon;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Command;
import com.nebula.cqrs.axon.CQRSDomainBuilder.Field;

public class CQRSWebDomainListener implements DomainListener {

	@Override
	public void define(CQRSContext ctx, Type typeDomain, CQRSDomainBuilder cqrs, ClassReader domainClassReader) {
		// String name =
		// "org.axonframework.samples.bank.web.MyBankAccountController";
		Type typeController = Type.getObjectType(typeDomain.getInternalName() + "Controller");
		Type typeRepository = Type.getObjectType(typeDomain.getInternalName() + "Repository");
		Type typeEntry = Type.getObjectType(typeDomain.getInternalName() + "Entry");
		Type typeConfig = Type.getObjectType(typeDomain.getInternalName() + "AxonConfig");
		Type typeCommandHandler = Type.getObjectType(typeDomain.getInternalName() + "CommandHandler");

		try {
			ctx.defineClass(typeEntry.getClassName(), PojoBuilder.dump(typeEntry, cqrs.domain.fields));

			for (Command command : cqrs.commands) {
				Type typeDto = Type.getObjectType(typeDomain.getInternalName() + CQRSDomainBuilder.toCamelUpper(command.actionName) + "Dto");
				List<Field> fields;
				if (command.ctorMethod) {
					fields = new ArrayList<>();
					for (int i = 0; i < command.fields.size(); i++) {
						if (!command.fields.get(i).idField) {
							fields.add(command.fields.get(i));
						}
					}
				} else {
					fields = command.fields;
				}
				ctx.defineClass(typeDto.getClassName(), PojoBuilder.dump(typeDto, fields));
			}

			ctx.defineClass(typeController.getClassName(), CQRSWebControllerBuilder.dump(typeDomain, typeController, typeRepository, typeEntry, cqrs));

			ctx.defineClass(typeRepository.getClassName(), CQRSRepositoryBuilder.dump(typeRepository, typeEntry));

			ctx.defineClass(typeConfig.getClassName(), CQRSAxonConfigBuilder.dump(typeDomain, typeConfig, typeCommandHandler));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
