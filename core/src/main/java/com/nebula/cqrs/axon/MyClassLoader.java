package com.nebula.cqrs.axon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class MyClassLoader extends ClassLoader {
	File root = new File("target/generated-auto-classes/");

	protected final ConcurrentMap<String, byte[]> typeDefinitions;

	public MyClassLoader(Map<String, byte[]> typeDefinitions) {
		this.typeDefinitions = new ConcurrentHashMap<>(typeDefinitions);
	}

	public MyClassLoader() {
		this.typeDefinitions = new ConcurrentHashMap<>();
	}

	public Class<?> define(String name, byte[] binaryRepresentation) {
		writeToWithPackage(root, name, binaryRepresentation);
		System.out.println("defineClass > " + name);
		typeDefinitions.putIfAbsent(name, binaryRepresentation);
		Class<?> type = defineClass(name, binaryRepresentation, 0, binaryRepresentation.length);;
		return type;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith("hello")) {
			System.out.println(name);
		}
		if (typeDefinitions.containsKey(name)) {
			byte[] binaryRepresentation = typeDefinitions.get(name);
			Class<?> type = defineClass(name, binaryRepresentation, 0, binaryRepresentation.length);;
			return type;
		} else {
			throw new ClassNotFoundException(name);
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