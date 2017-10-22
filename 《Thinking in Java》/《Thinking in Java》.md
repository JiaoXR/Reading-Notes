# 第 2 章  一切都是对象

Java 要确定每种基本类型所占存储空间的大小（不随机器硬件架构的变化而改变）。

|  基本类型   |   大小    | 最小值  | 最大值  |   包装器类型   |
| :-----: | :-----: | :--: | :--: | :-------: |
| boolean |    -    |  -   |  -   |  Boolean  |
|  char   | 16-bits |      |      | Character |
|  byte   | 8 bits  | -128 | +127 |   Byte    |
|  short  | 16 bits |      |      |   Short   |
|   int   | 32 bits |      |      |  Integer  |
|  long   | 64 bits |      |      |   Long    |
|  float  | 32 bits |      |      |   Float   |
| double  | 64 bits |      |      |  Double   |
|  void   |    -    |  -   |  -   |   Void    |



- 高精度数字

Java 提供了两个高精度计算的类：**BigInteger** 和 **BigDecimal** 。**BigInteger** 支持任意精度的整数，**BigDecimal** 支持任何精度的定点数。二者没有对应的基本类型。



- 方法

方法名和参数列表（它们合起来被称为“方法签名”）唯一地标识出某个方法。



```java
System.getProperties().list(System.out);
System.out.println(System.getProperty("user.name")); // jaxer
System.out.println(System.getProperty("java.library.path"));
```



# 第 3 章  操作符

- 静态导入

静态导入（static import），示例代码：

```java
import static net.mindview.util.Print.*;
```

- 移位操作符

移位操作符的对象也是二进制的“位”。

`<<`: 左移操作符；

`>>`: “”有符号右移操作符；

`>>>`: “无符号”右移位操作符，无论正负，都在高位插入 0.

- sizeof()

Java 没有 `sizeof()` 操作符，因为所有数据类型在所有机器中的大小都是相同的。



# 第 4 章  控制执行流程

Java 不允许我们将一个数字作为布尔值使用，虽然这在 C 和 C++ 里是允许的（在这些语言里，“真”是非零，“假”是零）。



若在返回 void 的方法中没有 return 语句，那么在该方法的结尾处会有一个隐式的 return。



- break & continue

break: 强制退出循环，不执行循环中剩余的语句。

continue: 停止执行当前的迭代，开始下一次循环。



规则：

1. 一般的 continue 会退回到最内层循环的开头（顶部），并继续执行。
2. 带标签的 continue 会到达标签的位置，并重新进入紧接在那个标签后面的循环。
3. 一般的 break 会中断并跳出当前循环。
4. 带标签的 break 会中断并跳出标签所指的循环。

注意：在 Java 里需要使用标签的唯一理由就是因为有循环嵌套存在，而且想从多层嵌套中 break 或 continue。

示例代码：

```java
outer: // 循环的标签
for (int i=0; i<10; i++) {
	for (int j=0; j<10; j++) {
		System.out.println(i + "--" + j);
		if (j == 5) {
			continue outer; // 相当于 break
			// break; // break 会跳出内层循环，外层进行下一次循环
			// return; // return 会跳出整个循环
		}
	}
}
```



# 第 5 章  初始化与清理

根据方法的返回值来区分重载方法是不行🚫的！



- 构造器中调用构造器

可以用 this 调用一个构造器，但却不能调用两个。此外，必须将构造器调用置于最起始处，否则编译器会报错。



## 构造器初始化

可以用构造器来进行初始化。但要牢记：无法阻止自动初始化的进行，它将在构造器被调用之前发生。示例代码：

```java
public class Counter {
  int i;
  Counter() { i = 7; }
  // ...
}
```

i 首先会被置 0，然后变成 7.



- 初始化顺序

在类的内部，变量定义的先后顺序决定了初始化的顺序。即使变量定义散布于方法定义之间，它们仍旧会在任何方法（包括构造器方法）被调用之前得到初始化。

- 静态数据的初始化

无论创建了多少个对象，静态数据都只占用一份存储区域。

静态初始化只有在必要时刻才会进行。只有在第一个对象被创建（或者第一次访问静态数据）的时候，它们才会被初始化。此后，静态对象不会再被初始化。

- 显示的静态初始化

Java 允许将多个静态初始化动作组织成一个特殊的“静态子句”（有时候也叫“静态块”）。示例代码：

```java
public class Spoon {
	static int i;
	static {
      i = 3;
	}
}
```

虽然上面代码看起来像个方法，但它实际只是一段跟在 static 关键字后面的代码。与其他静态初始化动作一样，这段代码仅执行一次：当首次生成这个类的一个对象时，或者首次访问属于那个类的静态数据成员时。

- 数组初始化

编译器不允许指定数组的大小。

```java
int[] a = {1, 2, 3, 4, 5};
System.out.println(Arrays.toString(a)); // 输出结果：[1, 2, 3]
```

- 枚举类型

创建 `enum` 时，编译器会自动添加一些有用的特性，例如，

1. `toString()` 方法（显示某个 enum 实例的名字）；
2. `oridinal()` 方法（表示某个特定 enum 常量的声明顺序）；
3. `static values()` 方法（用来按照 enum 常量的声明顺序，产生由这些常量值构成的数组）。

示例代码：

```java
public enum Day {
    MON, TUE, WEN, THU, FRI, SAT, SUN
}

	public static void main(String[] args) {
		for (Day day : Day.values()) {
			System.out.println(day + ", ordinal " + day.ordinal());
		}
	}
```



# 第 6 章  访问权限控制

访问控制权限的等级，从最大权限到最小权限依次为：

1. public：接口访问权限；
2. protected：继承访问权限；
3. 包访问权限（没有关键词）；
4. private：你无法访问。



### 类的访问权限

注意：类既不可以是 private 的（这样会使得除该类之外，其他任何类都不可以访问它），也不可以是 protected 的。所以对于类的访问权限，只有两个选择：包访问权限或 public。





































