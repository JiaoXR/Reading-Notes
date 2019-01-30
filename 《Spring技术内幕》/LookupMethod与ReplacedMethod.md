#  LookupMethod 与 ReplacedMethod

##  0. 准备工作

两个 POJO：

```java
public class User {
    public String show() {
        System.out.println("Hello, I am a user");
        return "user";
    }
}
```



```java
public class Teacher extends User {
    @Override
    public String show() {
        System.out.println("Hello, I am a teacher");
        return "teacher";
    }
}
```



##  1. lookup-method



```java
public abstract class LookupMethod {

    public String show() {
        return getUser().show();
    }

    // 这里可以是抽象方法
//    abstract User getUser();

    public User getUser() {
        return null;
    }
}
```

*method.xml* 文件配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- 测试 lookup-method -->
    <bean id="lookupMethod" class="com.jaxer.doc.method.lookup.LookupMethod">
        <lookup-method bean="teacher" name="getUser"/>
    </bean>

    <bean id="teacher" class="com.jaxer.doc.method.lookup.Teacher"/>
</beans>
```

测试类：

```java
public class LookupMethodTests {

    @Test
    public void test() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("method.xml");
        LookupMethod lookupMethod = context.getBean("lookupMethod", LookupMethod.class);
        System.out.println(lookupMethod.show());
    }
}
```



##  2. replaced-method

自定义替换方法实现类：

```java
public class ReplacedMethod implements MethodReplacer {

    @Override
    public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
        if ("show".equals(method.getName())) {
            System.out.println("method '" + method.getName() + "()' has been replaced");
            return "cat";
        }
        return null;
    }
}
```

*method.xml* 文件配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- 测试 replaced-method -->
    <bean id="teacher" class="com.jaxer.doc.method.lookup.Teacher">
        <replaced-method name="show" replacer="replaceMethod"/>
    </bean>

    <bean id="replaceMethod" class="com.jaxer.doc.method.replace.ReplacedMethod"/>
</beans>
```

测试类：

````java
public class LookupMethodTests {

    @Test
    public void test() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("method.xml");
        Teacher teacher = context.getBean("teacher", Teacher.class);
        System.out.println(teacher.show());
    }
}
````





> 参考：https://my.oschina.net/zudajun/blog/664659