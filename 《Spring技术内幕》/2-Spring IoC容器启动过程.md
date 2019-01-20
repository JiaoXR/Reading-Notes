#  Spring IoC 容器启动过程

之前对 IoC 容器的启动过程做了一个概述  [1-Spring IoC容器概述](https://github.com/JiaoXR/Reading-Notes/blob/master/%E3%80%8ASpring%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95%E3%80%8B/1-Spring%20IoC%E5%AE%B9%E5%99%A8%E6%A6%82%E8%BF%B0.md)

这里主要以 `ApplicationContext` 的实现类  `FileSystemXmlApplicationContext` 为例，详细说明 Spring IoC 容器的启动过程。`FileSystemXmlApplicationContext` 类的继承结构如图所示：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/FileSystemXmlApplicationContext.png)

> PS: 此处 Spring 版本为 5.1.3.RELEASE.

##  Spring IoC 容器的启动过程

###  准备工作

- 我们以编程方式跟踪 Spring IoC 容器的启动过程，示例代码如下：

```java
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans.xml");
context.getBean("employeeService", EmployeeService.class);
```

其中 `beans.xml` 是 bean 的配置文件，示例代码：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="employee" class="com.jaxer.doc.Employee"/>

</beans>
```

###  断点跟踪分析流程

我们以断点形式追踪 IoC 容器的初始化过程。

- `FileSystemXmlApplicationContext` 有多个构造器，但最终都是通过调用如下构造器进行实际操作的：

```java
public class FileSystemXmlApplicationContext extends AbstractXmlApplicationContext {
    // 其他构造器
	
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
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent) throws BeansException {

		super(parent);
		setConfigLocations(configLocations);
		if (refresh) {
			refresh();
		}
	}
}
```

其中 `refresh()` 方法是整个 IoC 容器启动（实际是重启）的核心方法。

该方法内部调用比较复杂，为了有个整体认识，便于分析，先贴一个方法调用时序图（顺序从左到右）：

![IoC-FileSystemXmlApplicationContext](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/IoC-FileSystemXmlApplicationContext.png)

####  1. super(parent)

其中 `super(parent)` 依次调用父类，直至 `AbstractApplicationContext`，如下：

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
		implements ConfigurableApplicationContext {
	
	/** ResourcePatternResolver used by this context. */
	private ResourcePatternResolver resourcePatternResolver;

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
}
```

该方法初始化了 `ResourcePatternResolver` 的实现类 `PathMatchingResourcePatternResolver` 的一个对象，其继承结构如下：

![PathMatchingResourcePatternResolver](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/PathMatchingResourcePatternResolver.png)

可以看出它是一个 `ResourceLoader`。即 `super()` 方法主要创建了一个 `ResourceLoader` 对应的实例。

####  2. setConfigLocations(configLocations)

该方法是父类 `AbstractRefreshableConfigApplicationContext` 的方法，如下：

```java
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
		implements BeanNameAware, InitializingBean {
		
	@Nullable
	private String[] configLocations;

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
}
```

主要设置了配置文件的位置。

####  3. refresh()

`refresh()` 实际是父类 `AbstractApplicationContext` 的方法，实现如下：

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

`prepareRefresh()` 方法主要做一些准备工作：记录开始时间，设置状态变量等，源码注释很详细，代码如下：

```java
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
		implements BeanNameAware, InitializingBean {
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

接下来通过 `obtainFreshBeanFactory()` 方法获取一个 `BeanFactory` 实例，如下：

```java
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
		implements BeanNameAware, InitializingBean {
			
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
}
```

该方法其实是一个抽象方法，具体实现由其子类 `AbstractRefreshableApplicationContext` 完成，如下：

```java
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
	/**
	 * This implementation performs an actual refresh of this context's underlying
	 * bean factory, shutting down the previous bean factory (if any) and
	 * initializing a fresh bean factory for the next phase of the context's lifecycle.
	 */
	@Override
	protected final void refreshBeanFactory() throws BeansException {
        // 若 BeanFactory 已存在，则将其内部的 Bean 销毁，并关闭该 BeanFactory
        // 因此，该 refresh 方法实际上是重启 IoC 容器
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

可以看到这里初始化了一个 `DefaultListableBeanFactory` 实例，也是 Spring 默认的 IoC 容器。

目前为止，已经创建了 BeanFactory，过程还不是很复杂，但这只是开始🤣。为了便于理解，画了一个上述调用过程的时序图，如下：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/IoC-Sequence-Diagram-1.png)



BeanFactory 实例创建后，开始执行 `loadBeanDefinitions` 方法，从名字可以看出其作用是载入 `BeanDefinition`。该方法在 `AbstractRefreshableApplicationContext` 中也是一个抽象方法，由于此处读取的是 XML 配置，具体交由 `AbstractXmlApplicationContext` 来实现，代码如下：

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

这里创建了一个 `BeanDefinitionReader` 的实例对象 `XmlBeanDefinitionReader`，用于读取定义 bean 的文件。`XmlBeanDefinitionReader` 类继承结构与相关代码如下：

![XmlBeanDefinitionReader](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/XmlBeanDefinitionReader.png)

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
	/**
	 * Create new XmlBeanDefinitionReader for the given bean factory.
	 * @param registry the BeanFactory to load bean definitions into,
	 * in the form of a BeanDefinitionRegistry
	 */
	public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
		super(registry);
	}
}
```

```java
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader, EnvironmentCapable {
	private final BeanDefinitionRegistry registry;

    @Nullable
	private ResourceLoader resourceLoader;
    
	protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		this.registry = registry;

		// Determine ResourceLoader to use.
		if (this.registry instanceof ResourceLoader) {
			this.resourceLoader = (ResourceLoader) this.registry;
		}
		else {
			this.resourceLoader = new PathMatchingResourcePatternResolver();
		}

		// Inherit Environment if possible
		if (this.registry instanceof EnvironmentCapable) {
			this.environment = ((EnvironmentCapable) this.registry).getEnvironment();
		}
		else {
			this.environment = new StandardEnvironment();
		}
	}
}
```

上面两个 `XmlBeanDefinitionReader` 的 `loadBeanDefinitions` 方法在其父类 `AbstractBeanDefinitionReader` 中如下：

```java
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader, EnvironmentCapable {
    
	@Override
	public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
		Assert.notNull(resources, "Resource array must not be null");
		int count = 0;
		for (Resource resource : resources) {
			count += loadBeanDefinitions(resource);
		}
		return count;
	}
	
	@Override
	public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
		Assert.notNull(locations, "Location array must not be null");
		int count = 0;
		for (String location : locations) {
			count += loadBeanDefinitions(location);
		}
		return count;
	}
    
	int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;    
}
```

这里稍微有点绕，但实际上这两个 `loadBeanDefinitions` 方法最后还是由子类 `XmlBeanDefinitionReader` 实现的：

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
	private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded =
			new NamedThreadLocal<>("XML bean definition resources currently being loaded");

    /**
	 * Load bean definitions from the specified XML file.
	 * @param resource the resource descriptor for the XML file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	@Override
	public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource));
	}

	/**
	 * Load bean definitions from the specified XML file.
	 * @param encodedResource the resource descriptor for the XML file,
	 * allowing to specify an encoding to use for parsing the file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		if (logger.isTraceEnabled()) {
			logger.trace("Loading XML bean definitions from " + encodedResource);
		}

		Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
		if (currentResources == null) {
			currentResources = new HashSet<>(4);
			this.resourcesCurrentlyBeingLoaded.set(currentResources);
		}
		if (!currentResources.add(encodedResource)) {
			throw new BeanDefinitionStoreException(
					"Detected cyclic loading of " + encodedResource + " - check your import definitions!");
		}
		try {
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
                // 通过 IO 操作读取 XML 文件
				InputSource inputSource = new InputSource(inputStream);
				if (encodedResource.getEncoding() != null) {
					inputSource.setEncoding(encodedResource.getEncoding());
				}
				return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
			}
			finally {
				inputStream.close();
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"IOException parsing XML document from " + encodedResource.getResource(), ex);
		}
		finally {
			currentResources.remove(encodedResource);
			if (currentResources.isEmpty()) {
				this.resourcesCurrentlyBeingLoaded.remove();
			}
		}
	}

	// 实际读取 XML 文件的方法
	protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource)
			throws BeanDefinitionStoreException {

		try {
            // TODO
			Document doc = doLoadDocument(inputSource, resource);
			int count = registerBeanDefinitions(doc, resource);
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded " + count + " bean definitions from " + resource);
			}
			return count;
		}
		catch (BeanDefinitionStoreException ex) {
			throw ex;
		}
		// 捕获各种 exception...
	}

	/**
	 * Actually load the specified document using the configured DocumentLoader.
	 * @param inputSource the SAX InputSource to read from
	 * @param resource the resource descriptor for the XML file
	 * @return the DOM Document
	 * @throws Exception when thrown from the DocumentLoader
	 * @see #setDocumentLoader
	 * @see DocumentLoader#loadDocument
	 */
	protected Document doLoadDocument(InputSource inputSource, Resource resource) throws Exception {
		return this.documentLoader.loadDocument(inputSource, getEntityResolver(), this.errorHandler,
				getValidationModeForResource(resource), isNamespaceAware());
	}

	/**
	 * Register the bean definitions contained in the given DOM document.
	 * Called by {@code loadBeanDefinitions}.
	 * <p>Creates a new instance of the parser class and invokes
	 * {@code registerBeanDefinitions} on it.
	 * @param doc the DOM document
	 * @param resource the resource descriptor (for context information)
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of parsing errors
	 * @see #loadBeanDefinitions
	 * @see #setDocumentReaderClass
	 * @see BeanDefinitionDocumentReader#registerBeanDefinitions
	 */
	public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
        // 这里得到 BeanDefinitionDocumentReader 来对 XML 的 BeanDefinition 进行解析
		BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
		int countBefore = getRegistry().getBeanDefinitionCount();
        // 具体解析过程在 registerBeanDefinitions 中完成
		documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
		return getRegistry().getBeanDefinitionCount() - countBefore;
	}
}
```

`registerBeanDefinitions` 由 `BeanDefinitionDocumentReader` 的实现类 `DefaultBeanDefinitionDocumentReader` 完成。具体实现有待进一步分析……



上述方法解析后，得到一个 `BeanDefinitionHolder` 类，里面包含了解析得到的 `BeanDefinition`，然后通过 `BeanDefinitionReaderUtils.registerBeanDefinition` 注册该 `BeanDefinition`：

```java
public abstract class BeanDefinitionReaderUtils {
	/**
	 * Register the given bean definition with the given bean factory.
	 * @param definitionHolder the bean definition including name and aliases
	 * @param registry the bean factory to register with
	 * @throws BeanDefinitionStoreException if registration failed
	 */
	public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

		// Register bean definition under primary name.
		String beanName = definitionHolder.getBeanName();
		registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

		// Register aliases for bean name, if any.
		String[] aliases = definitionHolder.getAliases();
		if (aliases != null) {
			for (String alias : aliases) {
				registry.registerAlias(beanName, alias);
			}
		}
	}
}
```

实际注册 `BeanDefinition` 的地方：

```java
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
		implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {

	/** Map of bean definition objects, keyed by bean name. */
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

			@Override
			public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
					throws BeanDefinitionStoreException {
		
				Assert.hasText(beanName, "Bean name must not be empty");
				Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		
				if (beanDefinition instanceof AbstractBeanDefinition) {
					try {
						((AbstractBeanDefinition) beanDefinition).validate();
					}
					catch (BeanDefinitionValidationException ex) {
						throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
								"Validation of bean definition failed", ex);
					}
				}
		
				BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
				if (existingDefinition != null) {
					// 若 IoC 容器中已存在同名的 BeanDefinition 且不允许覆盖，则抛异常
					if (!isAllowBeanDefinitionOverriding()) {
						throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
					}
					else if (existingDefinition.getRole() < beanDefinition.getRole()) {
						// e.g. was ROLE_APPLICATION, now overriding with ROLE_SUPPORT or ROLE_INFRASTRUCTURE
						if (logger.isInfoEnabled()) {
							logger.info("Overriding user-defined bean definition for bean '" + beanName +
									"' with a framework-generated bean definition: replacing [" +
									existingDefinition + "] with [" + beanDefinition + "]");
						}
					}
					else if (!beanDefinition.equals(existingDefinition)) {
						if (logger.isDebugEnabled()) {
							logger.debug("Overriding bean definition for bean '" + beanName +
									"' with a different definition: replacing [" + existingDefinition +
									"] with [" + beanDefinition + "]");
						}
					}
					else {
						if (logger.isTraceEnabled()) {
							logger.trace("Overriding bean definition for bean '" + beanName +
									"' with an equivalent definition: replacing [" + existingDefinition +
									"] with [" + beanDefinition + "]");
						}
					}
					this.beanDefinitionMap.put(beanName, beanDefinition);
				}
				else {
					if (hasBeanCreationStarted()) {
						// Cannot modify startup-time collection elements anymore (for stable iteration)
						synchronized (this.beanDefinitionMap) {
							this.beanDefinitionMap.put(beanName, beanDefinition);
							List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
							updatedDefinitions.addAll(this.beanDefinitionNames);
							updatedDefinitions.add(beanName);
							this.beanDefinitionNames = updatedDefinitions;
							if (this.manualSingletonNames.contains(beanName)) {
								Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
								updatedSingletons.remove(beanName);
								this.manualSingletonNames = updatedSingletons;
							}
						}
					}
					else {
						// Still in startup registration phase
						this.beanDefinitionMap.put(beanName, beanDefinition);
						this.beanDefinitionNames.add(beanName);
						this.manualSingletonNames.remove(beanName);
					}
					this.frozenBeanDefinitionNames = null;
				}
		
				if (existingDefinition != null || containsSingleton(beanName)) {
					resetBeanDefinition(beanName);
				}
			}		
}
```

可以看到这里将 BeanDefinition 注册到了一个 ConcurrentHashMap 中，其中 key 是 beanName，value 是 BeanDefinition。上述时序图如下：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/IoC-Sequence-Diagram-2.png)

到这里，Spring 已经大体完成了从 XML 中读取 bean 配置，并转为 BeanDefinition，注册到 BeanFactory。

但是，此时并不包含依赖注入。即，Spring 此时尚未根据 XML 配置文件生成相应的 Bean 对象。

