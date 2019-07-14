package com.jaxer.example.jvm;

import java.io.IOException;
import java.io.InputStream;

/**
 * 测试ClassLoader
 * <p>
 * Created by jaxer on 2019-04-22
 */
public class ClassLoaderTest {
	public static void main(String[] args) throws Exception {
		ClassLoader myLoader = new ClassLoader() {
			@Override
			public Class<?> loadClass(String name) throws ClassNotFoundException {
//				return super.loadClass(name);
				try {
					String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
					InputStream inputStream = getClass().getResourceAsStream(fileName);
					if (inputStream == null) {
						return super.loadClass(name);
					}
					byte[] bytes = new byte[inputStream.available()];
					inputStream.read(bytes);
					return defineClass(name, bytes, 0, bytes.length);
				} catch (IOException e) {
					throw new ClassNotFoundException();
				}
			}
		};

		Object obj = myLoader.loadClass("com.jaxer.example.jvm.ClassLoaderTest").newInstance();
		System.out.println(obj.getClass());
		System.out.println(obj instanceof ClassLoaderTest);
	}
}
