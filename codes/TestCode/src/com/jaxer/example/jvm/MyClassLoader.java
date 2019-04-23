package com.jaxer.example.jvm;

import java.io.*;

/**
 * Created by jiaoxiangru on 15:04 2019-04-23
 */
public class MyClassLoader extends ClassLoader {
    private String root;

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = loadClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        return defineClass(name, classData, 0, classData.length);
    }

    private byte[] loadClassData(String className) {
//        String fileName = root + File.separatorChar + className.replace('.', File.separatorChar) + ".class";
        String fileName = root + File.separatorChar + className.replace('.', File.separatorChar) + ".class";
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public static void main(String[] args) {
        MyClassLoader myClassLoader = new MyClassLoader();
//        myClassLoader.setRoot("/Users/dasouche/Documents/Test/SimpleTest/out/production/SimpleTest");
        myClassLoader.setRoot("/Users/dasouche/Desktop");

        try {
            Class<?> aClass = myClassLoader.loadClass("com.jaxer.test.TestSomething");
            Object obj = aClass.newInstance();
            System.out.println(obj.getClass().getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
