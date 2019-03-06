# Spring IoC 容器启动过程-1

前面对 IoC 容器的启动过程做了一个概述  「[1-Spring IoC容器概述](https://github.com/JiaoXR/Reading-Notes/blob/master/%E3%80%8ASpring%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95%E3%80%8B/1-Spring%20IoC%E5%AE%B9%E5%99%A8%E6%A6%82%E8%BF%B0.md)」

接下来主要以 ApplicationContext 的实现类  FileSystemXmlApplicationContext 为例，详细说明 Spring IoC 容器的启动过程。FileSystemXmlApplicationContext 类的继承结构如图所示：

![FileSystemXmlApplicationContext](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/FileSystemXmlApplicationContext.png)



> PS: 此处 Spring 版本为 5.1.3.RELEASE.



为了容器启动过程有个整体了解，这里先给出主要方法调用的时序图，如下：

图一：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/Sequence/SequenceDiagram1.svg)

图二：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/Sequence/SequenceDiagram2.svg)



由于 IoC 容器的启动过程比较复杂，代码比较长，因此打算分拆解开进行分析。本文先分析 IoC 容器启动时的一些准备工作。



##  1. 文件准备

- 首先定义一个包含 Bean 定义的 XML 文件 *beans.xml*, 如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="employee" class="com.jaxer.doc.Employee"/>

</beans>
```



- 然后创建 FileSystemApplicationContext 解析该 XML 文件，如下：

```java
ApplicationContext context = new FileSystemXmlApplicationContext("classpath:beans.xml");
context.getBean("employee", Employee.class);
```



断点跟踪和分析，进入 FileSystemApplicationContext 内部可以看到调用了如下构造器：

```java
public FileSystemXmlApplicationContext(String configLocation) throws BeansException {
    this(new String[] {configLocation}, true, null);
}

/**
 * Create a new FileSystemXmlApplicationContext with the given parent,
 * loading the definitions from the given XML files.
 * @param configLocations array of file paths
 * @param refresh whether to automatically refresh the context,
 * loading all bean definitions and creating all singletons.
 * Alternatively, call refresh manually after further configuring the context.
 * @param parent the parent context
 * @throws BeansException if context creation failed
 * @see #refresh()
 */
public FileSystemXmlApplicationContext(
        String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
        throws BeansException {

    super(parent);
    setConfigLocations(configLocations);
    if (refresh) {
        refresh();
    }
}
```

实际上调用的是下面这个构造器。而其中的 *refresh()* 方法是整个 IoC 容器启动的核心方法，也比较复杂，后面再拆开分析，这里先分析其前面的 *super()* 和 *setConfigLocations()* 方法。



##  2. 方法分析

### 2.1 super(parent)



*super()* 方法依次调用父类 AbstractXmlApplicationContext、AbstractRefreshableConfigApplicationContext、AbstractRefreshableApplicationContext，一直到 AbstractApplicationContext。AbstractApplicationContext 的构造器代码如下：

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    /** Parent context. */
    @Nullable
    private ApplicationContext parent;
    
    /** Environment used by this context. */
    @Nullable
    private ConfigurableEnvironment environment;

    /** ResourcePatternResolver used by this context. */
    private ResourcePatternResolver resourcePatternResolver;

    // super(parent) 方法最终到了这里
    public AbstractApplicationContext(@Nullable ApplicationContext parent) {
        this();
        setParent(parent);
    }
    
    public AbstractApplicationContext() {
        this.resourcePatternResolver = getResourcePatternResolver();
    }
    
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver(this);
    }
    
    @Override
    public void setParent(@Nullable ApplicationContext parent) {
        this.parent = parent;
        if (parent != null) {
            Environment parentEnvironment = parent.getEnvironment();
            if (parentEnvironment instanceof ConfigurableEnvironment) {
                getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
            }
        }
    }
    
    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }
    
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }
}
```



这里主要做了哪些工作？

1. 创建了一个 ResourcePatternResolver 的实例 PathMatchingResourcePatternResolver；
2. 初始化 parent (ApplicationContext)。



### 2.2 setConfigLocations(configLocations) 

该方法是父类 AbstractRefreshableConfigApplicationContext 的方法，如下：

```java
/**
 * Set the config locations for this application context.
 * If not set, the implementation may use a default as appropriate.
 */
public void setConfigLocations(@Nullable String... locations) {
    if (locations != null) {
        Assert.noNullElements(locations, "Config locations must not be null");
        this.configLocations = new String[locations.length];
        for (int i = 0; i < locations.length; i++) {
            this.configLocations[i] = resolvePath(locations[i]).trim();
        }
    }
    else {
        this.configLocations = null;
    }
}

/**
 * Resolve the given path, replacing placeholders with corresponding
 * environment property values if necessary. Applied to config locations.
 * @param path the original file path
 * @return the resolved file path
 * @see org.springframework.core.env.Environment#resolveRequiredPlaceholders(String)
 */
protected String resolvePath(String path) {
	return getEnvironment().resolveRequiredPlaceholders(path);
}
```



该方法主要是设置给定的配置文件位置。



这两个方法做了一些准备工作，下面进入核心的 *refresh()* 方法。

