# Spring IoC 容器启动过程-2

上文 「[Spring IoC容器启动过程-1](https://github.com/JiaoXR/Reading-Notes/blob/master/%E3%80%8ASpring%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95%E3%80%8B/Spring%20IoC%E5%AE%B9%E5%99%A8%E5%90%AF%E5%8A%A8%E8%BF%87%E7%A8%8B-1.md)」 分析了 IoC 容器启动的一些准备工作，本文开始分析 IoC 容器启动的核心方法 *refresh()*，该方法较长，因此分多个部分分析。



##  refresh()

*refresh()* 方法在 AbstractApplicationContext 类中，其代码如下：

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    /** Synchronization monitor for the "refresh" and "destroy". */
    private final Object startupShutdownMonitor = new Object();

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        synchronized (this.startupShutdownMonitor) {
            // 1. Prepare this context for refreshing.
            prepareRefresh();

            // 2. Tell the subclass to refresh the internal bean factory.
            // 该方法创建了一个 IoC 容器，是 refresh 的核心部分
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

            // 3. Prepare the bean factory for use in this context.
            prepareBeanFactory(beanFactory);

            try {
                // 4. Allows post-processing of the bean factory in context subclasses.
                postProcessBeanFactory(beanFactory);

                // 5. Invoke factory processors registered as beans in the context.
                invokeBeanFactoryPostProcessors(beanFactory);

                // 6. Register bean processors that intercept bean creation.
                registerBeanPostProcessors(beanFactory);

                // 7. Initialize message source for this context.
                initMessageSource();

                // 8. Initialize event multicaster for this context.
                initApplicationEventMulticaster();

                // 9. Initialize other special beans in specific context subclasses.
                onRefresh();

                // 10. Check for listener beans and register them.
                registerListeners();

                // 11. Instantiate all remaining (non-lazy-init) singletons.
                finishBeanFactoryInitialization(beanFactory);

                // 12. Last step: publish corresponding event.
                finishRefresh();
            }

            catch (BeansException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
                }

                // Destroy already created singletons to avoid dangling resources.
                destroyBeans();

                // Reset 'active' flag.
                cancelRefresh(ex);

                // Propagate exception to caller.
                throw ex;
            }

            finally {
                // Reset common introspection caches in Spring's core, since we
                // might not ever need metadata for singleton beans anymore...
                resetCommonCaches();
            }
        }
    }
}
```



### prepareRefresh()

*prepareRefresh()* 方法在 AbstractApplicationContext 类中，代码如下：

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    /** Flag that indicates whether this context is currently active. */
    private final AtomicBoolean active = new AtomicBoolean();

    /** Flag that indicates whether this context has been closed already. */
    private final AtomicBoolean closed = new AtomicBoolean();
    
    /** ApplicationEvents published early. */
    @Nullable
    private Set<ApplicationEvent> earlyApplicationEvents;
    
    /**
     * Prepare this context for refreshing, setting its startup date and
     * active flag as well as performing any initialization of property sources.
     */    
    protected void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);

        if (logger.isDebugEnabled()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Refreshing " + this);
            }
            else {
                logger.debug("Refreshing " + getDisplayName());
            }
        }        

        // Initialize any placeholder property sources in the context environment
        initPropertySources();

        // Validate that all properties marked as required are resolvable
        // see ConfigurablePropertyResolver#setRequiredProperties
        getEnvironment().validateRequiredProperties();

        // Allow for the collection of early ApplicationEvents,
        // to be published once the multicaster is available...
        this.earlyApplicationEvents = new LinkedHashSet<>();
    }
}
```

该方法主要做了一些准备工作：记录开始时间，设置状态变量等。



### obtainFreshBeanFactory()

接下来通过 *obtainFreshBeanFactory()* 方法创建 IoC 容器，代码如下：

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {

    /**
     * Tell the subclass to refresh the internal bean factory.
     * @return the fresh BeanFactory instance
     * @see #refreshBeanFactory()
     * @see #getBeanFactory()
     */
    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        return getBeanFactory();
    }
    
    // 抽象方法，实现交给子类
    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;
    
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
```

这里的 *refreshBeanFactory()* 和 *getBeanFactory()* 方法都是抽象方法，它们的实现都交给子类。



### refreshBeanFactory()

根据之前分析的继承关系，可以在 AbstractApplicationContext 的子类 AbstractRefreshableApplicationContext 找到上述两个方法的实现，如下：

```java
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    
    /** Bean factory for this context. */
    @Nullable
    private DefaultListableBeanFactory beanFactory;    
    
    /** Synchronization monitor for the internal BeanFactory. */
    private final Object beanFactoryMonitor = new Object();    

    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("BeanFactory not initialized or already closed - " +
                        "call 'refresh' before accessing beans via the ApplicationContext");
            }
            return this.beanFactory;
        }
    }

    /**
     * This implementation performs an actual refresh of this context's underlying
     * bean factory, shutting down the previous bean factory (if any) and
     * initializing a fresh bean factory for the next phase of the context's lifecycle.
     */
    @Override
    protected final void refreshBeanFactory() throws BeansException {
        // 若 BeanFactory 已存在，则将其内部的 Bean 销毁，并关闭该 BeanFactory
        // 因此，refresh 方法实际上是重启 IoC 容器
        if (hasBeanFactory()) {
            destroyBeans();
            closeBeanFactory();
        }
        try {
            // 该方法创建了一个 DefaultListableBeanFactory 实例
            DefaultListableBeanFactory beanFactory = createBeanFactory();
            beanFactory.setSerializationId(getId());
            customizeBeanFactory(beanFactory);
            loadBeanDefinitions(beanFactory);
            synchronized (this.beanFactoryMonitor) {
                this.beanFactory = beanFactory;
            }
        }
        catch (IOException ex) {
            throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
        }
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(getInternalParentBeanFactory());
    }
    
    // 这里是使用 BeanDefinitionReader 载入 Bean 定义的地方
    // 因为允许多种载入方式(XML 最常见)，因此这里是抽象方法，具体实现交给子类
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
            throws BeansException, IOException;
}
```

对其中的方法进行分析如下：

- *getBeanFactory()*

*getBeanFactory()* 方法很简单，就是获取 BeanFactory 实例，也即是 IoC 容器。从这里可以看出，Spring IoC 容器是 DefaultListableBeanFactory。

- *hasBeanFactory()*

从 *hasBeanFactory()* 方法判断的操作可以得知：如果已经存在 BeanFactory，则将其内部的 Bean 销毁，并关闭 BeanFactory。其实就是重启 IoC 容器，也正符合该方法的名字 "刷新"。

- *createBeanFactory()*

从 *createBeanFactory()* 方法可以进一步证实：这里创建的 IoC 容器是 DefaultListableBeanFactory。但是，这只是一个裸的 BeanFactory，基本不包含任何信息。后面需要对其进行装饰。

- *loadBeanDefinitions()*

该方法也是一个抽象方法，其实现在子类 AbstractXmlApplicationContext 中。



### loadBeanDefinitions(DefaultListableBeanFactory)

AbstractXmlApplicationContext 中的 *loadBeanDefinitions()* 方法代码如下：

```java
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

    /**
     * Loads the bean definitions via an XmlBeanDefinitionReader.
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     * @see #initBeanDefinitionReader
     * @see #loadBeanDefinitions
     */    
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        // 根据传入的 BeanFactory 实例创建了一个 XmlBeanDefinitionReader
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        // 以 Resource 方式获得配置文件的资源位置
        Resource[] configResources = getConfigResources();
        if (configResources != null) {
            reader.loadBeanDefinitions(configResources);
        }
        // 以 String 的形式获取配置文件的位置
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            reader.loadBeanDefinitions(configLocations);
        }
    }
}
```

可以看到，在 *loadBeanDefinitions(DefaultListableBeanFactory)* 方法中，首先创建了一个 XmlBeanDefinitionReader 对象，之后继续将该对象传入了另一个 *loadBeanDefinitions(XmlBeanDefinitionReader)*  方法中。



- *loadBeanDefinitions (XmlBeanDefinitionReader)*

这里有两种配置类型，分别为 Resource 和 String 类型。根据上文的代码：

```java
ApplicationContext context = new FileSystemXmlApplicationContext("classpath:beans.xml");
```

这里的资源属于第二种情况。

- *getConfigLocations()*

跟进 *getConfigLocations()* 方法，其代码如下：

```java
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
        implements BeanNameAware, InitializingBean {

    @Nullable
    private String[] configLocations;

    @Nullable
    protected String[] getConfigLocations() {
        return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
    }

    @Nullable
    protected String[] getDefaultConfigLocations() {
        return null;
    }
}
```

这里的 configLocations 就是上文 「[Spring IoC 容器启动过程-1#2.2 setConfigLocations(configLocations)](https://github.com/JiaoXR/Reading-Notes/blob/master/%E3%80%8ASpring%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95%E3%80%8B/Spring%20IoC%E5%AE%B9%E5%99%A8%E5%90%AF%E5%8A%A8%E8%BF%87%E7%A8%8B-1.md#22-setconfiglocationsconfiglocations)」 方法中所做的准备工作中的配置文件路径。因此，接下来就是要读取配置 Bean 定义的 XML 文件。

- *loadBeanDefinitions(String... locations)*

跟踪该方法，可以发现其代码实现在 AbstractBeanDefinitionReader 类中，如下：

```java
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader, EnvironmentCapable {
    
    @Override
    public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
        Assert.notNull(locations, "Location array must not be null");
        int count = 0;
        for (String location : locations) {
            count += loadBeanDefinitions(location);
        }
        return count;
    }

    @Override
    public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(location, null);
    }

    public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
        ResourceLoader resourceLoader = getResourceLoader();
        if (resourceLoader == null) {
            throw new BeanDefinitionStoreException(
                    "Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
        }

        if (resourceLoader instanceof ResourcePatternResolver) {
            // Resource pattern matching available.
            try {
                Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
                int count = loadBeanDefinitions(resources);
                if (actualResources != null) {
                    Collections.addAll(actualResources, resources);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
                }
                return count;
            }
            catch (IOException ex) {
                throw new BeanDefinitionStoreException(
                        "Could not resolve bean definition resource pattern [" + location + "]", ex);
            }
        }
        else {
            // Can only load single resources by absolute URL.
            Resource resource = resourceLoader.getResource(location);
            int count = loadBeanDefinitions(resource);
            if (actualResources != null) {
                actualResources.add(resource);
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
            }
            return count;
        }
    }

    @Override
    public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
        Assert.notNull(resources, "Resource array must not be null");
        int count = 0;
        for (Resource resource : resources) {
            count += loadBeanDefinitions(resource);
        }
        return count;
    }
    
    int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;        
}
```



PS: 如果跟踪上面两个方法 *reader.loadBeanDefinitions(configResources)* 和 *reader.loadBeanDefinitions(configLocations)*，可以发现，它们两个最后还是”殊途同归“：即最终都会调用 *loadBeanDefinitions(Resource)* 方法，而该方法也是一个抽象方法，它的实现在子类 XmlBeanDefinitionReader 中（真正干活的地方）。



经过一系列铺垫，总算来到了读取 Bean 配置文件的地方，下文再分析 XmlBeanDefinitionReader 如何读取 XML 文件中的内容。