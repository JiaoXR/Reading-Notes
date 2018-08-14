#  第 13 章  字符串

###  13.1 不可变 String

String 对象是不可变的。String 类中每一个看起来会修改 String 的方法，实际上都是创建了一个全新的 String 对象，以包含修改后的字符串内容。而最初的 String 对象则丝毫未动。

每当把 String 对象作为方法的参数时，都会复制一份引用，而该引用所指的对象其实一直待在单一的物理位置上，从未动过。

###  13.2 重载 "+" 与 StringBuilder

两种方式生成 String，示例代码：

```java
public class WitherStringBuilder {
  	//编译器内部会生成多个 StringBuilder 对象
    public String implicit(String[] fields) {
        String result = "";
        for (String field : fields) {
            result += field;
        }
        return result;
    }
    
  	//显式地创建 StringBuilder
	//只生成一个 StringBuilder 对象
    public String explicit(String[] fields) {
        StringBuilder result = new StringBuilder();
        for (String field : fields) {
            result.append(field);
        }
        return result.toString();
    }

}
```

PS: 可以通过 `javap -c WitherStringBuilder` 命令，反编译该类，以查看其字节码。

显式地创建 StringBuilder 还允许预先为其指定大小。若已知最终字符串大概有多长，那么预先指定 StringBuilder 的大小可以避免多次重新分配缓冲。

> 因此，当为一个类编写 toString() 方法时，若字符串操作比较简单，可以信赖编译器；
>
> 但若要在 toString() 方法中使用循环，那么最好自己创建一个 StringBuilder 对象，用它构造最终的结果。

###  13.3 无意识的递归

示例代码：

```java
public class InfiniteRecursion {

    @Override
    public String toString() {
        //这样会产生 toString() 的递归
        return " InfiniteRecursion address: " + this + "\n";
    }

    public static void main(String[] args) {
        List<InfiniteRecursion> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new InfiniteRecursion());
        }
        System.out.println(list);
    }
}
```

###  13.5 格式化输出

`System.out.format()` 与 `System.out.printf()` 是等价的。

Formatter 类：Java 中，所有新的格式化功能都由 `java.util.Formatter` 类处理。

Formatter 的构造器经过重载可以接受多种输出目的地，不过最常用的还是 PrintStream(), OutputStream 和 File.

Formatter 常用的类型转换：

|      |            |      |           |
| ---- | ---------- | ---- | --------- |
| d    | 整数型（十进制）   | e    | 浮点数（科学计数） |
| c    | Unicode 字符 | x    | 整数（十六进制）  |
| b    | Boolean 值  | h    | 散列码（十六进制） |
| s    | String     | %    | 字符 '%'    |
| f    | 浮点数（十六进制）  |      |           |

注意 b 转化：对于 boolean 基本类型或 Boolean 对象，其结果是对应的 true 或 false。但是，对于其他类型的参数，只要该参数不为 null，其转换的结果就为 true。



String.format() 是一个 static 方法，它接受与 Formatter.format() 方法一样的参数，但返回一个 String 对象。

###  13.6 正则表达式

Java 中，`\\` 的意思是“我要插入一个正则表达式的反斜线，所以其后的字符具有特殊的意义。“例如：

若你想表示一位数字，那么正则表达式应该是 `\\d`； 若想插入一个普通的反斜线，则应该写成 `\\\`。



一些创建字符串的典型方式：

| 正则表达式        | 字符类                                      |
| ------------ | ---------------------------------------- |
| .            | 任意字符                                     |
| [abc]        | 包含 a, b 和 c 的任何字符（和 a\|b\|c 作用相同）        |
| [^abc]       | 除了a, b 和 c 之外的任何字符（否定）                   |
| [a-zA-Z]     | 从 a 到 z 或从 A 到 Z 的任何字符（范围）               |
| [abc[hij]]   | 任意 a, b, c, h, i 和 j 字符（与 a\|b\|c\|h\|i\|j 作用相同）（合并） |
| [a-z&&[hij]] | 任意 h, i 或 j（交）                           |
| \s           | 空白符（空格、tab、换行、换页和回车）                     |
| \S           | 非空白符（`[^\s]`）                            |
| \d           | 数字 [0-9]                                 |
| \D           | 非数字 `[^0-9]`                             |
| \w           | 词字符 [a-zA-Z0-9]                          |
| \W           | 非词字符 `[^\w]`                             |

####  13.6.3 量词

量词描述了一个模式吸收输入文本的方式：

- 贪婪型
- 勉强型
- 占有型

####  13.6.4 Pattern 和 Matcher

`java.util.regex.Matcher`

Pattern 对象表示编译后的正则表达式。