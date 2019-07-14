package com.jaxer.example.reflection;

/**
 * 代码千万行，注释第一行。
 * <p>
 * <p>
 * Created by jaxer on 2019-07-11
 */
public class Person extends Human {
	private String name;

	private int age;

	public Person() {
	}

	private Person(String name) {
		this.name = name;
	}

	protected Person(int age) {
		this.age = age;
	}

	Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	private void test() {
	}

	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", age=" + age +
				'}';
	}
}
