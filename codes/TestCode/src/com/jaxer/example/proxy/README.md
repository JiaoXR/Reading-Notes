# 代理模式笔记

代理模式之前有过接触，但没做过总结，过段时间又忘了，因此这里做个笔记。

代理通常分两种：静态代理和动态代理。

- 静态代理：编码时期代码写好代理类（使用较少）。
- 动态代理：运行时动态生成代理对象（使用较多，尤其是各种框架中）。

## 0. 准备工作

一个普通的接口及其实现类，代码如下：

```java
// 接口
public interface UserService {
    void save(String name);
}

// 实现类
public class UserServiceImpl implements UserService {
    @Override
    public void save(String name) {
        System.out.println("保存用户：" + name);
    }
}
```

## 1. 静态代理

- 示例代码

```java
public class UserServiceProxy implements UserService {
    private UserService userService = new UserServiceImpl();

    @Override
    public void save(String name) {
        System.out.println("---静态代理类---");
        userService.save(name);
        System.out.println("---静态代理类---");
    }
}
```

- 测试代码

```java
private static void testStaticProxy() {
    UserServiceProxy proxy = new UserServiceProxy();
    proxy.save("jack");
}

/* 运行结果：
	---静态代理类---
	保存用户：jack
	---静态代理类---
*/
```

## 2. 动态代理

动态代理通常有两种实现：JDK 动态代理和 CGLIB 动态代理。

### 2.1 JDK 动态代理

- 示例代码

```java
// JDK 代理工厂，作用是生成代理对象
public class JDKProxyFactory {
    // 获取代理对象
    public Object getProxy(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), new MyInvocationHandler(target));
    }

    // 自定义 InvocationHandler
    private static class MyInvocationHandler implements InvocationHandler {
        private Object target;

        public MyInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("方法执行前-----");
            // 执行被代理对象的方法
            Object result = method.invoke(target, args);
            System.out.println("方法执行后-----");
            return result;
        }
    }
}
```

- 测试代码

```java
private static void testJdkProxy() {
    UserService userService = new UserServiceImpl();
    JDKProxyFactory jdkProxyFactory = new JDKProxyFactory();
    UserService proxy = (UserService) jdkProxyFactory.getProxy(userService);
    proxy.save("jack");
}

/* 运行结果：
    方法执行前-----
    保存用户：jack
    方法执行后-----
*/
```

### 2.2 CGLIB 动态代理

- 示例代码

```java
// CGLIB 工厂，用于生成代理对象
public class CglibProxyFactory {
    // 获取代理对象
    public Object getProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MyMethodInterceptor());
        return enhancer.create();
    }

    // 自定义方法拦截 MethodInterceptor
    private static class MyMethodInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("cglib方法调用前");
            Object o = proxy.invokeSuper(obj, args);
            System.out.println("cglib方法调用后");
            return o;
        }
    }
}
```

- 测试代码

```java
private static void testCglibProxy() {
    UserService userService = new UserServiceImpl();
    CglibProxyFactory cglibProxyFactory = new CglibProxyFactory();
    UserService proxy = (UserService) cglibProxyFactory.getProxy(userService.getClass());
    proxy.save("jack");
}

/* 运行结果：
	cglib方法调用前
	保存用户：jack
	cglib方法调用后
*/
```

## 3. 小结

JDK 动态代理和 CGLIB 动态代理异同：

- 相同点：二者都是对目标对象的功能进行增强；
- 不同点
    - JDK 动态代理是根据接口生成另一个代理类
    - CGLIb 是通过生成子类
- 性能：1.8 之前 JDK 较差，之后二者差不多

