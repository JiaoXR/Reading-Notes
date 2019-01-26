#  BeanFactory 与 FactoryBean

##  概述

- BeanFactory: 是一个 Factory，是 IoC 容器（或者对象工厂）

接口定义如下：

![BeanFactory](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/BeanFactory.png)

- FactoryBean: 是一个 Bean，但又不是普通的 Bean，而是一个能产生或修饰对象生成的工厂 Bean

接口定义如下：

![FactoryBean](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/FactoryBean.png)



##  示例代码

一个普通的 Bean：

```java
public class Student {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

一个 FactoryBean：

```java
public class StudentFactoryBean implements FactoryBean<Student> {
    private String type;

    @Nullable
    @Override
    public Student getObject() throws Exception {
        if ("student".equals(type)) {
            return new Student();
        }
        return null;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return Student.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
```

配置文件 `factoryBean.xml` 如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="student" class="com.jaxer.doc.bean.Student"/>

    <bean id="studentFactoryBean" class="com.jaxer.doc.bean.StudentFactoryBean"/>
</beans>
```

测试：

```java
public class FactoryBeanTests {

    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("factoryBean.xml");
        // 获取 FactoryBean (注意需要加 & 前缀)
        StudentFactoryBean factoryBean = context.getBean("&studentFactoryBean", StudentFactoryBean.class);
        
        // 通过 BeanFactory 获取 Student 对象
        Student student = context.getBean("student", Student.class);
        
        // 通过 FactoryBean 获取 Student 对象
        factoryBean.setType("student");
        Student student = factoryBean.getObject();
    }
}
```

