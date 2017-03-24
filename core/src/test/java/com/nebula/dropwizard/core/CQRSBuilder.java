package com.nebula.dropwizard.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import com.nebula.dropwizard.core.CQRSDomainBuilder.Command;
import com.nebula.dropwizard.core.CQRSDomainBuilder.Event;

public class CQRSBuilder {

	static MyClassLoader classLoader = new MyClassLoader();

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

	static private void dump(byte[] code) {
		ClassReader cr = new ClassReader(code);
		ClassVisitor visitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
		cr.accept(visitor, ClassReader.EXPAND_FRAMES);
	}

	public static Class<?> makeDomainCQRSHelper(String domainClassName) throws Exception {
		File root = new File("target/generated-auto-classes/");

		// MyClassLoader classLoader = new MyClassLoader();
		Class<?> clzHandle = null;
		{
			Type typeDomain = Type.getObjectType(domainClassName.replace('.', '/'));
			Type typeHandler = Type.getObjectType(typeDomain.getInternalName() + "CommandHandler");

			ClassReader cr = new ClassReader(typeDomain.getClassName());
			CQRSDomainAnalyzer analyzer = new CQRSDomainAnalyzer(Opcodes.ASM5);
			cr.accept(analyzer, 0);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			TraceClassVisitor traceClassVisitorToConsole = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
			CQRSDomainBuilder cqrs = new CQRSDomainBuilder(Opcodes.ASM5, traceClassVisitorToConsole, analyzer.methods);
			cr.accept(cqrs, 0);

			System.out.println(cqrs.toString());

			byte[] code = cw.toByteArray();
			writeToWithPackage(root, typeDomain.getClassName(), code);

			{
				for (Event event : cqrs.events) {
					if (event.realEvent == null) {
						byte[] eventCode = CQRSEventRealBuilder.dump(event);
						writeToWithPackage(root, cqrs.fullnameOf(event.simpleClassName), eventCode);
						classLoader.defineClass(cqrs.fullnameOf(event.simpleClassName), eventCode);
					}
				}
				for (Event event : cqrs.events) {
					if (event.realEvent != null) {
						byte[] eventCode = CQRSEventAliasBuilder.dump(event);
						writeToWithPackage(root, cqrs.fullnameOf(event.simpleClassName), eventCode);
						classLoader.defineClass(cqrs.fullnameOf(event.simpleClassName), eventCode);
					}
				}
				for (Command command : cqrs.commands) {
					if (command.ctorMethod) {
						byte[] codeCommand = CQRSCommandBuilder.dump(command);
						writeToWithPackage(root, command.type.getClassName(), codeCommand);
						Class<?> clzCommand = classLoader.defineClass(command.type.getClassName(), codeCommand);
						classLoader.doResolveClass(clzCommand);

						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCtorCallerBuilder.dump(typeDomain, typeHandler, command);
						writeToWithPackage(root, typeInvoke.getClassName(), codeCommandHandlerInvoke);
						Class<?> clzCommandHandlerInvoke = classLoader.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
						classLoader.doResolveClass(clzCommandHandlerInvoke);
					} else {
						byte[] codeCommand = CQRSCommandBuilder.dump(command);
						writeToWithPackage(root, command.type.getClassName(), codeCommand);
						Class<?> clzCommand = classLoader.defineClass(command.type.getClassName(), codeCommand);
						classLoader.doResolveClass(clzCommand);

						Type typeInvoke = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.simpleClassName);
						byte[] codeCommandHandlerInvoke = CQRSCommandHandlerCallerBuilder.dump(typeDomain, typeHandler, command);
						writeToWithPackage(root, typeInvoke.getClassName(), codeCommandHandlerInvoke);
						Class<?> clzCommandHandlerInvoke = classLoader.defineClass(typeInvoke.getClassName(), codeCommandHandlerInvoke);
						classLoader.doResolveClass(clzCommandHandlerInvoke);
					}
				}
			}

			Class<?> clzDomain = classLoader.defineClass(typeDomain.getClassName(), code);
			classLoader.doResolveClass(clzDomain);

			Constructor<?> con31 = clzDomain.getDeclaredConstructor();
			con31.setAccessible(true);
			con31.newInstance();

			byte[] codeHandler = new CQRSCommandHandlerBuilder().dump(cqrs.commands, typeDomain, typeHandler);
			writeToWithPackage(root, typeHandler.getClassName(), codeHandler);
			clzHandle = classLoader.defineClass(typeHandler.getClassName(), codeHandler);
			classLoader.doResolveClass(clzHandle);
		}

		return clzHandle;
	}

	static class MyClassLoader extends ClassLoader {
		@SuppressWarnings("unchecked")
		public <T> Class<T> defineClass(String name, byte[] b) {
			return (Class<T>) defineClass(name, b, 0, b.length);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			System.out.println("loadClass >>>   " + name);
			return super.loadClass(name);
		}

		public void doResolveClass(Class<?> clz) {
			super.resolveClass(clz);
		}

	}
}
