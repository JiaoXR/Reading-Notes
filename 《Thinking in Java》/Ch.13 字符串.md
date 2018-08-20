#  第 13 章  字符串

###  13.1 不可变 String

- String 对象是不可变的。
- String 类中每一个看起来会修改 String 的方法（例如 `toUpperCase()`），实际上都是创建了一个全新的 String 对象，以包含修改后的字符串内容。而最初的 String 对象则丝毫未动。
- 每当把 String 对象作为方法的参数时，都会复制一份引用，而该引用所指的对象其实一直待在单一的物理位置上，从未动过。

###  13.2 重载 "+" 与 StringBuilder

- 操作符重载

用于 String 的 "+" 与 "+=" 是 Java 中仅有的两个重载过的操作符，而 Java 并不允许程序员重载任何操作符。

- 两种方式生成 String，示例代码：

```java
public class WitherStringBuilder {
  	/**
     * 隐式使用 StringBuilder
     * 该方式会创建多个 StringBuilder 对象
     */
    public String implicit(String[] fields) {
        String result = "";
        for (String field : fields) {
            result += field;
        }
        return result;
    }
    
    /**
     * 显式使用 StringBuilder
     * 只生产一个 StringBuilder 对象
     */
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

- 显式地创建 StringBuilder 还允许预先为其指定大小。若已知最终字符串大概有多长，那么预先指定 StringBuilder 的大小可以避免多次重新分配缓冲。

> 因此，当为一个类编写 toString() 方法时，若字符串操作比较简单，可以信赖编译器；
>
> 但若要在 toString() 方法中使用循环，那么最好自己创建一个 StringBuilder 对象，用它构造最终的结果。

###  13.3 无意识的递归

示例代码：

```java
public class InfiniteRecursion {

    @Override
    public String toString() {
        /*
         * 这里发生了自动类型转换：由 InfiniteRecursion 类型转为 String 类型。
         * 由于编译器看到一个 String 对象后面跟着一个 "+"，之后的对象不再是 String，
         * 于是编译器试着将 this 转为一个 String，会调用 this 的 toString 方法，
         * 这样会产生 toString 递归调用，导致 StackOverflowError
         */
//        return " InfiniteRecursion address: " + this + "\n";
        return super.toString(); //ok
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

- `System.out.format()` 与 `System.out.printf()` 是等价的。
- Formatter 类：Java 中，所有新的格式化功能都由 `java.util.Formatter` 类处理。
- Formatter 的构造器经过重载可以接受多种输出目的地，不过最常用的还是 PrintStream(), OutputStream 和 File.
- Formatter 常用的类型转换字符：

| 字符 | 类型               | 字符 | 类型               |
| ---- | ------------------ | ---- | ------------------ |
| d    | 整数型（十进制）   | e    | 浮点数（科学计数） |
| c    | Unicode 字符       | x    | 整数（十六进制）   |
| b    | Boolean 值         | h    | 散列码（十六进制） |
| s    | String             | %    | 字符 '%'           |
| f    | 浮点数（十六进制） |      |                    |

> 注意 b 转化：对于 boolean 基本类型或 Boolean 对象，其结果是对应的 true 或 false。但是，对于其他类型的参数，只要该参数不为 null，其转换的结果就为 true。

- String.format() 是一个 static 方法，它接收与 Formatter.format() 方法一样的参数，但返回一个 String 对象。

###  13.6 正则表达式

> 在其他语言中，`\\` 表示：**我想要在正则表达式中插入一个普通的（字面上的）反斜杠，请不要给它任何特殊的意义。**在 Java 中，`\\` 表示：**我要插入一个正则表达式的反斜线，所以其后的字符具有特殊的意义。**

所以，在其他的语言中（如 Perl），一个反斜杠 \ 就足以具有转义的作用，而在 Java 中正则表达式中则需要有两个反斜杠才能被解析为其他语言中的转义作用。也可以简单的理解在 Java 的正则表达式中，两个 `\\` 代表其他语言中的一个 \，这也就是为什么表示一位数字的正则表达式是 `\\d`，而表示一个普通的反斜杠是 `\\\\`。

- 一些创建字符串的典型方式

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

####  实例

| 正则表达式       | 描述                                                         |
| ---------------- | ------------------------------------------------------------ |
| this is text     | 匹配字符串 "this is text"                                    |
| this\s+is\s+text | 注意字符串中的 **\s+**。<br />匹配单词 "this" 后面的 **\s+** 可以匹配多个空格，之后匹配 is 字符串，再之后 **\s+** 匹配多个空格然后再跟上 text 字符串。<br />可以匹配这个实例：this is text |
| ^\d+(\\.\d+)?    | ^ 定义了以什么开始<br />\d+ 匹配一个或多个数字<br />? 设置括号内的选项是可选的<br />\\. 匹配 "."<br />可以匹配的实例："5", "1.5", "2.21" |

> 参考：http://www.runoob.com/java/java-regular-expressions.html

####  13.6.3 量词

量词描述了一个模式吸收输入文本的方式：

- 贪婪型
- 勉强型
- 占有型

####  13.6.4 Pattern 和 Matcher

`java.util.regex.Matcher`

Pattern 对象表示编译后的正则表达式。

