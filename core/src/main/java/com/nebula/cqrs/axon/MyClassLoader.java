package com.nebula.cqrs.axon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClassLoader extends ClassLoader {
	private final static Logger LOGGER = LoggerFactory.getLogger(MyClassLoader.class);

	File root = new File("target/classes/");

	protected final ConcurrentMap<String, byte[]> typeDefinitions;

	public MyClassLoader(Map<String, byte[]> typeDefinitions) {
		this.typeDefinitions = new ConcurrentHashMap<>(typeDefinitions);
	}

	public MyClassLoader() {
		this.typeDefinitions = new ConcurrentHashMap<>();
		String cp = System.getProperty("java.class.path");
		cp.concat(";").concat(root.getName());
	}

	public void load(String name, byte[] binaryRepresentation) {
		writeToWithPackage(root, name, binaryRepresentation);
		typeDefinitions.putIfAbsent(name, binaryRepresentation);
	}

	public void load(String name, byte[] binaryRepresentation, boolean debug) {
		LOGGER.debug("Load class [{}]", name);
		load(name, binaryRepresentation);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		LOGGER.debug("Find class [{}]", name);
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