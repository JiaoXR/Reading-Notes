# 反射笔记

一直对反射机制有些陌生，前两天看了一个视频教程，讲得很清晰。其实反射并不难，只是平时接触到的比较少吧了。其实在各种框架的源码中用的还是很多的，这里做个笔记。

### 0. 准备工作

两个普通的 Java 类，如下：

- Human.java

```java
package com.jaxer.example.reflection;

public class Human {
    protected void hello() {
    }
}
```

- Person.java

```java
package com.jaxer.example.reflection;

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
```

### 1. 获取 Class 对象

```java
// 方式一：
Person person = new Person();
System.out.println(person.getClass());

// 方式二：
System.out.println(Person.class);

// 方式三：
Class<?> aClass = Class.forName("com.jaxer.example.reflection.Person");
System.out.println(aClass);

/* 输出结果：
三种方式的输出结果都是
    class com.jaxer.example.reflection.Person
而且可证明，这三种方式得到的同一个 Class 对象，即同一个 Person 类
*/

// 可以使用 Class 对象来创建对象或者获取其父类，例如：
System.out.println(aClass.newInstance()); // 创建 Person 对象
System.out.println("superClass-->" + aClass.getSuperclass()); // 获取 Person 的父类（Human）
```



### 2. 获取构造器

主要有以下四个方法：

- getConstructors(): 获取所有 public 构造器
- getDeclaredConstructors(): 获取所有构造器
- getConstructor(Class<?>... parameterTypes): 获取指定的 public 构造器
- getDeclaredConstructor(Class<?>... parameterTypes): 获取指定的构造器（包括 private）

```java
private static void testConstructors(Class<?> aClass) throws Exception {
    // 获取所有 public 构造器
    Constructor<?>[] constructors = aClass.getConstructors();
    for (Constructor<?> constructor : constructors) {
        System.out.println("constructor-->" + constructor);
    }
    
    // 获取所有构造器
    Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
    for (Constructor<?> declaredConstructor : declaredConstructors) {
        System.out.println("declaredConstructor-->" + declaredConstructor);
    }
    
    // 获取指定的构造器（根据参数类型），这里获取的是构造器 protected Person(int age)
    Constructor<?> constructor = aClass.getDeclaredConstructor(int.class);

    // 设置访问权限（用这种方式可以访问 private 类型构造器，框架中经常使用）
    constructor.setAccessible(true);
    
    // 使用构造器创建对象（注意要传入类型适合的参数）
    Object instance = constructor.newInstance(1);
    System.out.println(instance);
}
```



### 3. 获取属性

主要有以下四个方法：

- getFields(): 获取 public 属性
- getDeclaredFields(): 获取所有属性
- getField(String name): 获取指定名称的 public 属性
- getDeclaredField(String name): 获取指定名称的属性（包括 private）

```java
private static void testFields(Class<?> aClass) throws Exception {
    // 获取 public 属性
    Field[] fields = aClass.getFields();
    for (Field field : fields) {
        System.out.println("field-->" + field);
    }
    

    // 获取所有属性
    Field[] declaredFields = aClass.getDeclaredFields();
    for (Field declaredField : declaredFields) {
        System.out.println("declaredField-->" + declaredField);
    }
    
    // 获取指定的属性（这里是获取 name 属性）
    Field name = aClass.getDeclaredField("name");
    System.out.println("name-->" + name);
    
    // 给指定对象的属性赋值（private 类型不能赋值，需要修改访问权限）
    Object instance = aClass.newInstance();
    name.setAccessible(true);
    name.set(instance, "jack");
    System.out.println("instance-->" + instance);
}
```



### 4. 获取方法

主要有以下四个方法：

- getMethods(): 获取所有 pubic 方法（包括父类）
- getDeclaredMethods(): 获取所有方法（包括 private 方法）
- getMethod(String name, Class<?>... parameterTypes): 获取指定名称和参数的 public 方法
- getDeclaredMethod(String name, Class<?>... parameterTypes): 获取指定名称和参数的方法（包括 private 方法）

```java
private static void testMethods(Class<?> aClass) throws Exception {
    // 获取所有 public 方法（包括父类）
    Method[] methods = aClass.getMethods();
    for (Method method : methods) {
        System.out.println("method-->" + method);
    }
    
    // 获取该类的所有方法
    Method[] declaredMethods = aClass.getDeclaredMethods();
    for (Method declaredMethod : declaredMethods) {
        System.out.println("declaredMethod-->" + declaredMethod);
    }
    
    // 获取指定的方法并调用（这里获取了 private 方法）
    Method method = aClass.getDeclaredMethod("test");
    // 设置访问控制
    method.setAccessible(true);
    System.out.println("方法名：" + method.getName());
    System.out.println("方法修饰符：" + Modifier.toString(method.getModifiers()));
    Object instance = aClass.newInstance();
    // 调用方法（需要传入对象及参数，如果有的话）
    System.out.println(method.invoke(instance));
}
```

