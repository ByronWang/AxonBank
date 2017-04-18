package com.nebula.cqrs.axon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.CQRSDomainBuilder.Command;
import com.nebula.cqrs.axon.CQRSDomainBuilder.Event;

public class CQRSBuilder implements CQRSContext {
	final MyClassLoader classLoader;

	public CQRSBuilder() {
		super();
		this.classLoader = new MyClassLoader();
		listeners.add(new CommandHandlerListener());
	}

	final List<DomainListener> listeners = new ArrayList<>();

	public void add(DomainListener domainListener) {
		this.listeners.add(domainListener);
	}

	static class CommandHandlerListener implements DomainListener {
		@Override
		public void define(CQRSContext ctx, Type typeDomain, CQRSDomainBuilder cqrs, ClassReader domainClassReader) {
			try {
				Class<?> clzHandle = null;

				Type typeHandler = Type.getObjectType(typeDomain.getInternalName() + "CommandHandler");

				for (Command command : cqrs.commands) {
					if (command.ctorMethod) {
						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCtorCallerBuilder.dump(typeDomain, typeHandler, command);
						Class<?> clzCommandHandlerInvoke = ctx.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
						ctx.doResolveClass(clzCommandHandlerInvoke);
					} else {
						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCallerBuilder.dump(typeDomain, typeHandler, command);
						Class<?> clzCommandHandlerInvoke = ctx.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
						ctx.doResolveClass(clzCommandHandlerInvoke);
					}
				}

				byte[] codeHandler = new CQRSCommandHandlerBuilder().dump(cqrs.commands, typeDomain, typeHandler);
				clzHandle = ctx.defineClass(typeHandler.getClassName(), codeHandler);
				ctx.doResolveClass(clzHandle);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return;
		}
	}

	public void makeDomainCQRSHelper(String domainClassName) {

		try {
			Type typeDomain = Type.getObjectType(domainClassName.replace('.', '/'));

			ClassReader cr = new ClassReader(typeDomain.getClassName());
			CQRSDomainAnalyzer analyzer = new CQRSDomainAnalyzer(Opcodes.ASM5);
			cr.accept(analyzer, 0);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			CQRSDomainBuilder cqrs = new CQRSDomainBuilder(Opcodes.ASM5, cw, analyzer.methods);
			cr.accept(cqrs, 0);

			byte[] code = cw.toByteArray();

			{
				for (Event event : cqrs.events) {
					if (event.realEvent == null) {
						byte[] eventCode = CQRSEventRealBuilder.dump(event);
						classLoader.defineClass(cqrs.fullnameOf(event.simpleClassName), eventCode);
					}
				}
				for (Event event : cqrs.events) {
					if (event.realEvent != null) {
						byte[] eventCode = CQRSEventAliasBuilder.dump(event);
						classLoader.defineClass(cqrs.fullnameOf(event.simpleClassName), eventCode);
					}
				}
				for (Command command : cqrs.commands) {
					if (command.ctorMethod) {
						byte[] codeCommand = CQRSCommandBuilder.dump(command);
						Class<?> clzCommand = classLoader.defineClass(command.type.getClassName(), codeCommand);
						classLoader.doResolveClass(clzCommand);
					} else {
						byte[] codeCommand = CQRSCommandBuilder.dump(command);
						Class<?> clzCommand = classLoader.defineClass(command.type.getClassName(), codeCommand);
						classLoader.doResolveClass(clzCommand);
					}
				}
			}

			Class<?> clzDomain = classLoader.defineClass(typeDomain.getClassName(), code);
			classLoader.doResolveClass(clzDomain);

			listeners.forEach(l -> l.define(CQRSBuilder.this, typeDomain, cqrs, cr));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return this.classLoader.loadClass(name);
	}

	public <T> Class<T> defineClass(String name, byte[] b) {
		return this.classLoader.defineClass(name, b);
	}

	@Override
	public void doResolveClass(Class<?> clz) {
		this.classLoader.doResolveClass(clz);
	}

	static class MyClassLoader extends ClassLoader {
		File root = new File("target/generated-auto-classes/");

		Map<String, Class<?>> loadedClasses = new HashMap<>();

		@SuppressWarnings("unchecked")
		public <T> Class<T> defineClass(String name, byte[] b) {
			Class<?> clz = defineClass(name, b, 0, b.length);
			loadedClasses.put(name, clz);
			writeToWithPackage(root, name, b);
			return (Class<T>) clz;
		}

		public void doResolveClass(Class<?> clz) {
			super.resolveClass(clz);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			if (name.startsWith("hello")) {
				System.out.println(name);
			}
			if (loadedClasses.containsKey(name)) {
				return loadedClasses.get(name);
			} else {
				return super.findClass(name);
			}
		}

		static private void writeToWithPackage(File root, String name, byte[] code) {
			try {
				int i = name.lastIndexOf(".");
				String packageName = name.substring(0, i).replace('.', '/');
				String simpleCassName = name.substring(i + 1);

				File folder = new File(root, packageName);
				if (!folder.exists()) {
					folder.mkdirs();
				}

				FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, simpleCassName + ".class"));

				fileOutputStream.write(code);
				fileOutputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
