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



# 第 7 章  复用类

- 组合
- 继承
- 代理



析构函数是一种在对象被销毁时可以被自动调用的函数。

向上转型：子类转为父类的类型。由导出类转型成基类，在继承图上是向上移动的，因此一般称为向上转型。

- final 关键字

对于引用对象，final使引用恒定不变，然而，对象其自身是可修改的。

类中所以的 private 方法都隐式地指定为是 final 的。

final 类禁止继承。

类的代码在初次使用时才加载。

初次使用之处也是 static 初始化发生之处。所有的 static 对象和 static 代码段都会在加载时依程序中的顺序（即，定义类时的书写顺序）而依次初始化。当然，定义为 static 的东西只会被初始化依次。



# 第 8 章  多态

将一个方法调用同一个方法主体关联起来被称作 **绑定**。

若在程序执行之前进行绑定（如果有的话，由编译器和连接程序实现），叫做 **前期绑定**。

- 前期绑定

面向过程语言中不需要选择就默认实现的绑定方式。例如 C 语言只有一种方法调用，就是前期绑定。

- 多态

也叫动态绑定、后期绑定或运行时绑定。在运行时根据对象的类型进行绑定。

Java 中除了 static 方法和 final 方法（private 方法属于 final 方法）之外，其他所有的方法都是后期绑定。



### 继承的缺陷

- “覆盖”私有方法

示例代码：

```java
public class PrivateOverride {
    private void f() { System.out.println("private f()"); }

    public static void main(String[] args) {
        PrivateOverride po = new Derived();
        po.f(); // 期望输出："public f()"；实际输出："private f()"
    }
}

public class Derived extends PrivateOverride {
    public void f() {
        System.out.println("public f()");
    }
}
```

原因：由于 private 方法被自动认为是 final 方法，而且对导出类是屏蔽的。因此，Derived 中的 f() 方法就是一个全新的方法。既然基类中的 f() 方法在子类 Derived 中不可见，因此也不能被重载。

> 结论：只有非 private 方法才可以被覆盖。还要密切注意覆盖 private 方法的现象，虽然编译器不会报错，但是通常也不会达到我们期望的效果。因此，在导出类（子类）中，对于基类中的 private 方法，最好采用不同的名字。



- 域与静态方法

示例代码：

```java
public class Super {
    public int field = 0;

    public int getField() {
        return field;
    }
}

public class Sub extends Super {
    public int field = 1;

    @Override
    public int getField() {
        return field;
    }

    public int getSuperField() {
        return super.getField();
    }
}

public class FieldAccess {
    public static void main(String[] args) {
        Super sup = new Sub();
        System.out.println("sup.field: " + sup.field +
        ", sup.getField():" + sup.getField());

        Sub sub = new Sub();
        System.out.println("sub.field: " + sub.field +
                ", sub.getField():" + sub.getField() +
                ", sub.getSuperField():" + sub.getSuperField());
    }
}

/* 输出结果：
sup.field = 0, sup.getField() = 1
sub.field = 1, sub.getField() = 1, sub.getSuperField() = 0
*/
```

当 Sub 对象转型为 Super 引用时，任何域访问操作都将由编译器解析，因此不是多态的。在本例中，为 Super.field 和 Sub.field 分配了不同的存储空间。这样，Sub 实际上包含两个称为 field 的域：它自己的和它从 Super 处得到的。然而，在引用 Sub 中的 field 时所产生的默认域并非 Super 版本的 field 域。因此，为了得到 Super.field，必须显示地指明 super.field.



如果某个方法是静态的，它的行为就不具有多态性。示例代码：

```java
public class StaticSuper {
    public static String staticGet(){
        return "Base staticGet()";
    }
    public String dynamicGet() {
        return "Base dynamicGet()";
    }
}

public class StaticSub extends StaticSuper {
    public static String staticGet() {
        return "Derived staticGet()";
    }
    public String dynamicGet() {
        return "Derived dynamicGet()";
    }
}

public class StaticPolymorphism {
    public static void main(String[] args) {
        StaticSuper sup = new StaticSub();
        System.out.println(sup.staticGet());
        System.out.println(sup.dynamicGet());
    }
}

/* 输出结果：
Base staticGet()
Derived dynamicGet()
*/
```



### 构造器和多态

构造器不同于其他种类的方法。构造器不具有多态性（它们实际上是 static 方法，只不过该 static 声明是隐式的）。

示例代码：

```java
class Meal() {
	Meal() { print("Meal()") }
}

class Bread() {
	Bread() { print("Bread()") }
}

class Cheese() {
	Cheese() { print("Cheese()") }
}

class Lettuce() {
	Lettuce() { print("Lettuce()") }
}

class Lunch extends Meal() {
	Lunch() { print("Meal()") }
}

class PortableLunch extends Meal() {
	PortableLunch() { print("PortableLunch()") }
}

publci class Sandwich extends PortableLunch() {
  	private Bread b = new Bread();
  	private Cheese c = new Cheese();
  	private Lettuce l = new Lettuce();
	Sandwich() { print("Sandwich()") }
	public static void main(String[] args) {
        new Sandwich();
    }
}

/* 输出结果：
Meal()
Lunch()
PortableLunch()
Bread()
Cheese()
Lettuce()
Sandwich()
*/
```

调用构造器的顺序：

1. 调用基类构造器。这个步骤会不断地反复递归下去，直到最底层的导出类。
2. 按声明顺序调用成员的初始化方法。
3. 调用导出类构造器的主体。



- 构造器内部的多态方法的行为

示例代码：

```java
class Glyph {
    void draw() {
        System.out.println("Glyph.draw()");
    }
    Glyph() {
        System.out.println("Glyph() before draw()");
        draw();
        System.out.println("Glyph() after draw()");
    }
}

class RoundGlyph extends Glyph {
    private int radius = 1;

    RoundGlyph(int radius) {
        this.radius = radius;
        System.out.println("RoundGlyph.RoundGlyph(), radius = " + radius);
    }

    @Override
    void draw() {
        System.out.println("RoundGlyph.draw(), radius = " + radius);
    }
}

public class PolyConstructor {
    public static void main(String[] args) {
        new RoundGlyph(5);
    }
}

/* 输出结果：
Glyph() before draw()
RoundGlyph.draw(), radius = 0
Glyph() after draw()
RoundGlyph.RoundGlyph(), radius = 5
*/
```

初始化的实际过程是：

1. 在其他任何事物发生之前，将分配给对象的存储空间初始化为二进制的零。
2. 调用基类构造器。此时，调用被覆盖后的 draw() 方法（要在调用 RoundGlyph 构造器之前调用），由于步骤 1 的缘故，此时 radius 的值为 0.
3. 按照声明的顺序调用成员的初始化方法。
4. 调用导出类的构造器方法。



> 编写构造器一条有效地准则：“用尽可能简单的方法使对象进入正常状态；如果可以的话，避免调用其他方法。”



### 协变返回类型

示例代码：

```java
class Grain {
    public String toString() { return "Grain"; }
}

class Wheat extends Grain {
    public String toString() { return "Wheat"; }
}

class Mill {
    Grain process(){ return new Grain(); }
}

public class WheatMill extends Mill {
    Wheat process() { return new Wheat(); }
}
```

协变返回类型允许返回更具体的 `Wheat` 类型。



# 第 9 章  接口



















