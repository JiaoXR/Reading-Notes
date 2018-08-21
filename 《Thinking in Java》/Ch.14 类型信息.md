# 第 14 章  类型信息
Java 可以让我们在运行时识别对象和类的信息，主要有两种方式：

1. “传统的” RTTI，它假定我们在编译时已经知道了所有的类型；
2. “反射”机制，允许我们在运行时发现和使用类的信息。

###  类字面常量

- Java 提供了另一种方法来生成对 Class 对象的引用，即使用类字面常量。例如：`FancyToy.class`
- 这样做更简单、更安全，因为它在编译时会受到检查（因此不需要置于 try 语句块中）。并且根除了对 `forName()` 方法的调用，因此更高效。
- 类字面常量不仅可以应用于普通的类，也可以应用于接口、数组以及基本数据类型。另外，对于基本数据类型的包装器类，还有一个标准字段 `TYPE` .TYPE 字段是一个引用，指向对应的基本数据类型的 Class 对象，如下：

| 等价于        | 等价于         |
| ------------- | -------------- |
| boolean.class | Boolean.TYPE   |
| char.class    | Character.TYPE |
| byte.class    | Byte.TYPE      |
| short.class   | Short.TYPE     |
| int.class     | Integer.TYPE   |
| long.class    | Long.TYPE      |
| float.class   | Float.TYPE     |
| double.class  | Double.TYPE    |
| void.class    | Void.TYPE      |

- 使用泛型后，`newInstance()` 方法将返回该对象的确切类型，而不仅仅只是 Object 类型

####  instanceof 与 Class 的等价性

示例代码：

https://github.com/JiaoXR/Reading-Notes/blob/master/codes/TestCode/src/com/jaxer/example/rtti/instance/FamilyVsExtraType.java

###  反射：运行时的类加载

- 人们想要在运行时获取类信息的另一个动机，便是希望提供在跨网络的远程平台上创建和运行对象的能力，称为远程方法调用（Remote Method Invocation, RMI）。

- Class 类与 `java.lang.reflect` 类库一起对反射的概念进行了支持，该类库包含了 Field、Method 以及 Constructor 类（每个类都实现了 Member 接口）。

示例代码：

https://github.com/JiaoXR/Java-Learning/blob/master/Foundation/Java%20%E5%8F%8D%E5%B0%84%E6%9C%BA%E5%88%B6.md

###  动态代理

Java 的动态代理比代理的思想更向前迈进了一步，因为它可以动态地创建代理并动态地处理对所代理方法的调用。在动态代理上所做的所有调用都会被重定向到单一的调用处理器上，它的工作是揭示调用的类型并确定相应的对策。

示例代码：

https://github.com/JiaoXR/Reading-Notes/tree/master/codes/TestCode/src/com/jaxer/example/rtti/proxy/dynamic

###  空对象

###  接口与类型信息

