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

接口也可以包含域，但这些域隐式地是 static 和 final 的（不能是“空 final”，但可以被非常量表达式初始化）。这些域不是接口的一部分，它们的值被存储在该接口的静态存储区域内。

PS: 用接口可便捷地创建常量组。主要用于 Java SE5 之前。Java SE5 之后，创建常量组主要使用 `enum` 关键字来实现。



创建一个能够根据所传递的参数对象的不同而具有不同行为的方法，称为*策略模式*。这类方法包含所要执行的算法中固定不变的部分，而“策略”包含变化的部分。



- 接口 & 抽象类的选择

如果要创建不带任何方法定义和成员变量的基类，应选接口而非抽象类。事实上，若某事物应该成为一个基类，那么第一选择应该是使它成为一个接口。



# 第 10 章  内部类

将一个类的定义放在另一个类的定义内部，叫内部类。



 ### 使用 .this 与 .new

若需要生成对外部类对象的引用，可以使用外部类的名字后面紧跟圆点和 **this**。这样产生的引用自动地具有正确的类型，这一点在编译期就被知晓并受到检查，因此没有任何运行时开销。示例代码：

```java
public class DotThis {
    void f() {
        System.out.println("DotThis.f()");
    }

    public class Inner {
        public DotThis outer() {
            return DotThis.this; // 注意写法
        }
    }

    public Inner inner() {
        return new Inner();
    }

    public static void main(String[] args) {
        DotThis dt = new DotThis();
        DotThis.Inner dti = dt.inner();
        dti.outer().f();
    }
}
```

有时可能想告知其他对象，去创建某个内部类的对象。要实现此目的，必须在 new 表达式中提供对其他外部类对象的引用，需要使用 `.new` 语法。示例代码：

```java
public class DotNew {
    public class Inner {}

    public static void main(String[] args) {
        DotNew dn = new DotNew();
        DotNew.Inner dni = dn.new Inner(); // 注意写法
    }
}
```

> 要想直接创建内部类的对象，必须使用外部类的对象来创建该内部类对象。

> 在拥有外部类对象之前是不可能创建内部类对象的。因为内部类对象会暗暗地连接到创建它的外部类对象上。但是若创建的是嵌套类（静态内部类），那么它就不需要对外部类对象的引用。



### 在方法和作用域内的内部类

- 局部内部类

可以在方法的作用域内（而不是在其他类的作用域内）创建一个完整的类，者被称作局部内部类。示例代码：

```java
public class Parcel5 {
    class PDestination {} // 此处同名不影响
    
    public Destination destination(String s) {
        class PDestination implements Destination {
            private String label;

            private PDestination(String whereTo) {
                label = whereTo;
            }

            @Override
            public String readLabel() {
                return label;
            }
        }
        return new PDestination(s);
    }

    public static void main(String[] args) {
        Parcel5 p = new Parcel5();
        Destination d = p.destination("Beijing");
    }
}
```

PDestination 类是 destination() 方法的一部分，而不是 Parcel5 的一部分。所以，在 destination() 之外不能访问 PDestination。

- 匿名内部类

示例代码：

```java
public class Parcel7 {
    public Contents contents() {
        return new Contents() {
            private int i = 11; 
            @Override
            public int value() { return i; }
        }; // 注意分号
    }
    
    public static void main(String[] args) {
        Parcel7 p = new Parcel7();
        Contents c = p.contents();
    }
}
```

该写法是下述写法的简写：

```java
public class Parcel7 {
  	class MyContents implemets Contents {
		private int i = 11; 
		@Override
		public int value() { return i; }
    }
  
    public Contents contents() {
        return new MyContents();
    }
    
    public static void main(String[] args) {
        Parcel7 p = new Parcel7();
        Contents c = p.contents();
    }
}
```

- 嵌套类

若不需要内部类对象与其外围类对象之间有联系，那么可以将内部类声明为 static。这通常称为嵌套类。

嵌套类特点：

1. 要创建嵌套类的对象，并不需要其外围类的对象。
2. 不能从嵌套类的对象中访问非静态的外围类对象。

嵌套类与普通的内部类还有一个区别：普通内部类的字段与方法，只能放在类的外部层次上，所以普通的内部类不能有 static 数据和 static 字段，也不能包含嵌套类。而嵌套类可以包含所有这些东西。

- 接口内部的类

示例代码：

```java
public interface ClassInterface {
    void howdy();
    class Test implements ClassInterface {
        @Override
        public void howdy() {
            System.out.println("Howdy");
        }

        public static void main(String[] args) {
            new Test().howdy();
        }
    }
}
```

- 为什么需要内部类

每个内部类都能独立地继承自一个（接口的）实现，所以无论外围类是否已经继承了某个（接口的）实现，对于内部类都没有影响。

内部类使得多重继承的解决方案变得完整，有效地实现了“多重继承”。



- 闭包与回调

。。。

- 内部类的继承

示例代码：

```java
class WithInner {
    class Inner {}
}

class InheritInner extends WithInner.Inner {
    // InheritInner() {} error!
  	InheritInner(WithInner wi) {
        wi.super();
    }
  	public static void main(String[] args) {
        WithInner wi = new WithInner();
      	InheriteInner ii = new InheritInner(wi);
    }
}
```



- 内部类标识符

每个类都会产生一个 .class 文件，包含了如何创建该类型的对象的全部信息（此信息产生一个 “meta-class”，叫做 Class 对象）。

内部类也必须生成一个 .class 文件以包含它们的 Class 对象信息。 命名规则：外围类的名字$内部类的名字。例如：

```java
Counter.class
LocalInnerClass$1.class
LocalInnerClass$1LocalCounter.class
```

若类是匿名的，编译器会简单地产生一个数字作为其标识。



# 第 11 章  持有对象

### 容器

- Collection

Arrays.asList() : 底层是数组，不能调整尺寸。

Collection.addAll();

- Map



- 容器的打印

数组的可打印表示需要使用 `Arrays.toString()`   方法，容器打印则无需任何帮助。

- 特点

  - List : 有序，可重复

    - ArrayList : 随机访问较快，插入移除慢；
    - LinkedList : 插入删除快，随机访问较慢；

  - Set : 无序，不重复

    - HashSet : 查找最快，无序（使用的是散列函数）；
    - TreeSet : 按照比较结果升序排列（使用的是红-黑树结构）；
    - LinkedHashSet : 按照被添加的顺序保存对象；

  - 队列

    - Queue : 一端进，另一端出

    队列是一个典型的先进先出（FIFO）的容器。

    ​	先进先出描述了最典型的队列规则。队列规则是指在给定的一组队列中的元素的情况下，确定下一个弹出队列的元素的规则。先进先出声明的是下一个元素应该是等待最长的元素。

    - PriorityQueue

    优先队列声明下一个弹出元素是最需要的元素（优先级最高的）。


  - Map : 键值对
    - HashMap : 查找最快，无序；
    - TreeMap : 按照比较结果的升序保存“键”；
    - LinkedHashMap : 按照插入顺序保存键，同时还保留了 HashMap 的查找速度；
  - Stack

  “栈”通常是指“后进先出（LIFO）”的容器。有时也称为叠加栈。

  - 不推荐使用过时的 Vector, HashTable 和 Stack.

- 一些方法

Collections.sort();

Collections.shuffle();



###  迭代器

- Iterator

迭代器是一个对象，它的工作是遍历并选择序列中的对象，且不必知道或关心该序列底层的结构。迭代器统一了对容器的访问方式。

示例代码：

```java
List<Pet> pets = Pets.arrayList(12);
Iterator<Pet> it = pets.iterator();
while (it.hasNext()) {
    Pet p = it.next();
}
```

若只是向前遍历 List，而不打算修改 List，则 foreach 语法会更简洁。 

- ListIterator

ListIterator 是一个更加强大的 Iterator 的子类型，它只能用于各种 List 的访问。Iterator 只能向前移动，而  ListIterator 可以双向移动。

示例代码：

```java
List<Pet> pets = Pets.arrayList(12);
ListIterator<Pet> it = pets.listIterator();
while (it.hasNext()) {
  System.out.println(it.next() + ", " + it.nextIndex() + ", " + it.previousIndex());
}
```

- Arrays.asList() 方法

示例代码：

```java
public class ModifyingArraysAsList {
    public static void main(String[] args) {
        Random random = new Random(47);
        Integer[] ia = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        List<Integer> list1 = new ArrayList<>(Arrays.asList(ia)); // 不会修改数组 ia
        System.out.println("Before shuffling: " + list1);
        Collections.shuffle(list1, random);
        System.out.println("After shuffling: " + list1);
        System.out.println("array:" + Arrays.toString(ia));

        System.out.println();

        List<Integer> list2 = Arrays.asList(ia); // 会修改数组 ia
        System.out.println("Before shuffling: " + list2);
        Collections.shuffle(list2, random);
        System.out.println("After shuffling: " + list2);
        System.out.println("array:" + Arrays.toString(ia));
    }
}

/* 输出结果：
	Before shuffling: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
	After shuffling: [4, 6, 3, 1, 8, 7, 2, 5, 10, 9]
	array:[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

	Before shuffling: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
	After shuffling: [9, 1, 6, 3, 7, 2, 5, 10, 4, 8]
	array:[9, 1, 6, 3, 7, 2, 5, 10, 4, 8]
*/
```

第一种情况：Arrays.asList() 的输出被传递给了 ArrayList() 的构造器，将创建一个引用 ia 的元素的 ArrayList，因此打乱这些引用不会修改该数组。

但若直接使用 Array.asList(ia) 的结果，就会修改 ia 的顺序。使用 Arrays.asList() 产生的 List 对象会使用底层数组作为其物理实现。

- 小结

简单的容器分类：

![container](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/thinking/container.png)

其实只有四种容器：Map, List, Set, Queue, 它们各自有两到三个实现版本。常用的容器用黑色粗线表示。

点线框表示接口，实线框表示普通的（具体的）类。带有空心箭头的电线表示一个特定的类实现了一个接口，实心箭头表示某个类可以生成箭头所指向类的对象。例如，任意的 Collection 接口可以生成 Iterator（也能生成普通的 Iterator，因为 List 继承自 Collection）。

