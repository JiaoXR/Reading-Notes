#  第 20 章  注解

- 注解（也被称为元数据）为我们在代码中添加信息提供了一种形式化的方法，使我们可以在稍后某个时刻非常方便地使用这些数据。
- 没有元素的注解称为标记注解（marker annotation）。

####  默认值限制

编译器对元素的默认值有些过分挑剔。

- 元素不能有不确定的值。即，元素必须要么有默认值，要么在使用注解时提供元素的值。
- 对于非基本类型的元素，无论在源代码中声明时，或是在注解接口中定义默认值时，都不能以 null 作为其值。

####  注解不支持继承

- 不能使用关键字 `extends` 来继承某个 `@interface`。



> PS: 之前的笔记链接：https://github.com/JiaoXR/Java-Learning/blob/master/Foundation/Java-Annotation.md

