#  第 19 章  枚举类型

###  基本 enum 特性

- 创建 enum 时，编译器会生成一个相关的类，这个类继承自 `java.lang.Enum` ；

- ordinal() 方法返回一个 int 值，这是每个 enum 实例在声明时的次序，从 0 开始；
- 可以使用 `==` 比较 enum 实例，编译器会自动提供 equals 和 hashCode 方法；
- Enum 类实现了 Comparable 接口，所以它有 compareTo 方法，同时还实现了 Serializable 接口；
- 在 enum 实例上调用 getDeclaringClass 方法，可以知道其所属的 enum 类；
- name 方法返回 enum 实例声明时的名字，与使用 toString 方法效果相同。

###  向 enum 中添加新的方法

- 除了不能继承自一个 enum 之外，基本上可以将 enum 看做一个常规的类。即，可以向 enum 中添加方法。
- enum 中的构造器与方法和普通的类没有区别。因为除了有少许限制之外，enum 就是一个普通的类。

###  switch 语句中的 enum

在 switch 中使用 enum，是 enum 提供的一项非常便利的功能。一般来说，在 switch 中只能使用整数值，而枚举实例天生就具备整数值的次序。

###  values 方法

- values() 方法是由编译器添加的 static 方法。
- 由于 values() 方法是由编译器插入到 enum 定义中的 static 方法，所以，如果将 enum 实例向上转型为 Enum，那么 values() 方法就不可用了。但可以使用 Class 中的 getEnumConstants() 方法。

###  实现，而非继承

创建一个新的 enum 时，可以同时实现一个或多个接口。

###  使用接口组织枚举

- 对于 enum 而言，实现接口是使其子类化的唯一办法。
- 在一个接口的内部，创建实现该接口的枚举，以此将元素进行分组，可以达到将枚举元素分类组织的目的。示例代码：

```java
public interface Food {
    enum Appetizer implements Food {
        SALAD, SOUP, SPRING_ROLLS;
    }

    enum MainCourse implements Food {
        LASAGNE, BURRITO, VINDALOO;
    }

    enum Coffee implements Food {
        BLACL_COFFEE, TEA;
    }

    public static void main(String[] args) {
        Food food = Appetizer.SALAD;
        food = MainCourse.BURRITO;
        food = Coffee.BLACL_COFFEE;
        System.out.println(food);
    }
}
```

###  EnumSet

###  EnumMap

###  常量相关的方法

Java 允许为 enum 实例编写方法，因而为每个 enum 实例赋予不同的行为。示例代码：

```java
public enum ConstantSpecificMethod {
    DATE_TIME {
        String getInfo() {
            return DateFormat.getDateInstance().format(new Date());
        }
    },
    CLASS_PATH {
        String getInfo() {
            return System.getenv("CLASSPATH");
        }
    };

    abstract String getInfo();

    public static void main(String[] args) {
        for (ConstantSpecificMethod csm : values()) {
            System.out.println(csm.getInfo());
        }
    }
}
```

###  多路分发

> PS: 由于本章最后几个小结平时几乎不用，有些草率地一带而过。