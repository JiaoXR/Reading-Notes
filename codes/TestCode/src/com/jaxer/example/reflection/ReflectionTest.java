package com.jaxer.example.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 代码千万行，注释第一行。
 * 测试反射
 * <p>
 * Created by jaxer on 2019-07-11
 */
public class ReflectionTest {
	public static void main(String[] args) throws Exception {

//		testClass();

		Class<?> aClass = Class.forName("com.jaxer.example.reflection.Person");
//		// 使用无参构造器创建实例
		System.out.println(aClass.newInstance());
		System.out.println("superClass-->" + aClass.getSuperclass());

//		testConstructors(aClass);

//		testFields(aClass);

		testMethods(aClass);
	}

	// 获取方法
	private static void testMethods(Class<?> aClass) throws Exception {
		// 获取所有public方法（包括父类）
		Method[] methods = aClass.getMethods();
		for (Method method : methods) {
//			System.out.println("method-->" + method);
		}
		// 获取该类的所有方法
		Method[] declaredMethods = aClass.getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			System.out.println("declaredMethod-->" + declaredMethod);
		}
		// 获取指定的方法并调用
		Method method = aClass.getDeclaredMethod("test");
		method.setAccessible(true);
		System.out.println("方法名：" + method.getName());
		System.out.println("方法修饰符：" + Modifier.toString(method.getModifiers()));
		Object instance = aClass.newInstance();
		System.out.println(method.invoke(instance));
	}

	// 获取属性
	private static void testFields(Class<?> aClass) throws Exception {
		// 获取public属性
		Field[] fields = aClass.getFields();
		for (Field field : fields) {
			System.out.println("field-->" + field);
		}

		System.out.println("-------分割线---------");

		// 获取所有属性
		Field[] declaredFields = aClass.getDeclaredFields();
		for (Field declaredField : declaredFields) {
			System.out.println("declaredField-->" + declaredField);
		}

		// 获取指定的属性
		Field name = aClass.getDeclaredField("name");
		System.out.println("name-->" + name);
		// 给指定对象的属性赋值（private类型不能赋值，需要修改访问权限）
		Object instance = aClass.newInstance();
		name.setAccessible(true);
		name.set(instance, "jack");
		System.out.println("instance-->" + instance);
	}

	// 获取构造器
	private static void testConstructors(Class<?> aClass) throws Exception {
		// 获取所有public构造器
		Constructor<?>[] constructors = aClass.getConstructors();
		for (Constructor<?> constructor : constructors) {
			System.out.println("constructor-->" + constructor);
		}

		// 获取所有构造器
		Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
		for (Constructor<?> declaredConstructor : declaredConstructors) {
			System.out.println("declaredConstructor-->" + declaredConstructor);
		}

		System.out.println("-------分割线---------");

		// 获取指定的构造器（根据参数类型）
		Constructor<?> constructor = aClass.getDeclaredConstructor(int.class);
//		constructor.setAccessible(true);
		Object instance = constructor.newInstance(1);
		System.out.println(instance);
	}

	private static void testClass() throws ClassNotFoundException {
		Person person = new Person();
		System.out.println(person.getClass());
		System.out.println(Person.class);

		Class<?> aClass = Class.forName("com.jaxer.example.reflection.Person");
		System.out.println(aClass);
	}
}
