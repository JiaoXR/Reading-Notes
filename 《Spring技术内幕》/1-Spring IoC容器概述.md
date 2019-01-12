#  Spring IoC 容器初概述

> IoC: Inversion of Control, 控制反转（”依赖对象的获得被反转了“），最常见的方式 DI（Dependency Injection, 依赖注入）。

>IoC 容器要解决的核心问题：如何反转对依赖的控制，把控制权从具体业务对象手中转交到平台或者框架。降低面向对象系统设计复杂性和提高面向对象系统可测试性。

- IoC 原理在不同语言有多种实现，同一语言也有多个产品，Spring 是 Java 语言实现中最著名的一个。也是 Spring 框架的核心。

- IoC 容器系列的实现：BeanFactory 和 ApplicationContext，可以认为 BeanFactory 是实现 IoC 容器的基本形式，而各种 ApplicationContext 的实现是 IoC 容器的高级表现形式（水桶的比喻）。接口设计如图：

![IoC](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/IoC.png)

BeanFactory 接口结构如下：

![BeanFactory](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/BeanFactory.png)

ApplicationContext 接口结构如下：

![ApplicationContext](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/ApplicationContext.png)



##  IoC 容器的初始化过程

大概包括三个步骤：BeanDefinition 的 Resource 定位、载入和注册。

###  Resource 的定位

- 定位用户定义 Bean 的文件；
- 由 ResourceLoader 通过 Resource 接口完成；
- 相关类和接口：Resource (FileSystemResource, ClassPathResource 等)、ResourceLoader

###  BeanDefinition 的载入

- 把用户在文件中定义好的 Bean 转为 Spring IoC 内部的数据结构（BeanDefinition）
- 相关类和接口：BeanDefinition

###  BeanDefinition 的注册

- 通过 BeanDefinitionRegistry，将 BeanDefinition 注册到 IoC 容器的 HashMap 中。



> PS: IoC 容器的初始化过程不包含依赖注入（即生成 Bean）的过程，依赖注入一般发生在应用第一次通过 getBean 向容器索取 Bean 的时候。

