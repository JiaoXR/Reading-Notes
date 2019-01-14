#  Spring IoC 容器启动过程

之前对 IoC 容器的启动过程做了一个概述  [1-Spring IoC容器概述](https://github.com/JiaoXR/Reading-Notes/blob/master/%E3%80%8ASpring%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95%E3%80%8B/1-Spring%20IoC%E5%AE%B9%E5%99%A8%E6%A6%82%E8%BF%B0.md)

这里主要以 `ApplicationContext` 的实现类  `FileSystemXmlApplicationContext` 为例，详细说明 Spring IoC 容器的启动过程。`FileSystemXmlApplicationContext` 类的继承结构如图所示：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/FileSystemXmlApplicationContext.png)

> PS: 本代码 Spring 版本为 5.1.3.RELEASE.

##  Spring IoC 容器启动过程

###  准备工作

- 我们以编程方式跟踪 Spring IoC 容器的启动过程，示例代码如下：

```java
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans.xml");
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

- `FileSystemXmlApplicationContext` 有多个构造器，但最终都是调用下面这个构造器进行实际操作的：

```java
public class FileSystemXmlApplicationContext extends AbstractXmlApplicationContext {
    // 其他构造器
    
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

其中 `refresh()` 方法是整个 IoC 容器启动（实际是重启）的核心方法。该方法内部调用比较复杂，为了有个整体认识，便于分析，这里贴一个方法调用的时序图：

![IoC-FileSystemXmlApplicationContext](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/IoC-FileSystemXmlApplicationContext.png)

####  1. super(parent)

其中 `super(parent)` 依次调用父类，直至 `AbstractApplicationContext`，如下：

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
		implements ConfigurableApplicationContext {
		
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

该方法调用了父类 `AbstractRefreshableConfigApplicationContext` 的实现，如下：

```java
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
		implements BeanNameAware, InitializingBean {
		
	@Nullable
	private String[] configLocations;

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

`refresh()` 方法实际是父类 `AbstractApplicationContext` 的 `refresh()` 方法，实现如下：

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
		implements ConfigurableApplicationContext {

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

`prepareRefresh()` 方法主要做了一些准备工作：记录开始时间，设置状态变量等，代码如下：

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
    
	protected void prepareRefresh() {
		this.startupDate = System.currentTimeMillis();
		this.closed.set(false);
		this.active.set(true);

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

接下来通过 `obtainFreshBeanFactory()` 方法创建一个 `BeanFactory` 实例，如下：

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
	
	protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;
}
```

实际上该方法是一个抽象方法，具体实现由子类 `AbstractRefreshableApplicationContext` 完成，如下：

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
    
	protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
			throws BeansException, IOException;
}
```

可以看到这里初始化了一个 `DefaultListableBeanFactory` 实例，也就是 Spring 默认的 IoC 容器。时序图如下：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/IoC-FileSystemXmlApplicationContext-1.png)

BeanFactory 实例创建后，开始执行 `loadBeanDefinitions` 方法，从名字可以看出其作用是载入 `BeanDefinition`。该方法在 `AbstractRefreshableApplicationContext` 中也是一个抽象方法，具体交由 `AbstractXmlApplicationContext` 来实现，代码如下：

```java
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {
    
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

    // 读取 BeanDefinition 由 XmlBeanDefinitionReader 来完成
    // 有两个方法：分别是读取 Resource[] 和 String[] 类型，由父类 AbstractBeanDefinitionReader 实现
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
		Resource[] configResources = getConfigResources();
		if (configResources != null) {
			reader.loadBeanDefinitions(configResources);
		}
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			reader.loadBeanDefinitions(configLocations);
		}
	}
}
```

这里创建了一个 `BeanDefinitionReader` 的实例对象 `XmlBeanDefinitionReader`，用于读取定义 bean 的文件。`XmlBeanDefinitionReader` 类继承结构如下：

![XmlBeanDefinitionReader](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/Spring/XmlBeanDefinitionReader.png)

上面两个 `XmlBeanDefinitionReader` 的 `loadBeanDefinitions` 方法在其父类 `AbstractBeanDefinitionReader` 中，如下：

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

实际上这两个 `loadBeanDefinitions` 方法最后还是由子类 `XmlBeanDefinitionReader` 实现：

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
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

    // 实际上读取 XML 文件的方法
	protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource)
			throws BeanDefinitionStoreException {

		try {
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
		BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
		int countBefore = getRegistry().getBeanDefinitionCount();
		documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
		return getRegistry().getBeanDefinitionCount() - countBefore;
	}
}
```

`registerBeanDefinitions` 由 `BeanDefinitionDocumentReader` 的实现类 `DefaultBeanDefinitionDocumentReader` 完成：

```java
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {
	public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;
	public static final String NESTED_BEANS_ELEMENT = "beans";
	public static final String IMPORT_ELEMENT = "import";
	public static final String ALIAS_ELEMENT = "alias";
    
    /**
	 * Register each bean definition within the given root {@code <beans/>} element.
	 */
	@SuppressWarnings("deprecation")  // for Environment.acceptsProfiles(String...)
	protected void doRegisterBeanDefinitions(Element root) {
		// Any nested <beans> elements will cause recursion in this method. In
		// order to propagate and preserve <beans> default-* attributes correctly,
		// keep track of the current (parent) delegate, which may be null. Create
		// the new (child) delegate with a reference to the parent for fallback purposes,
		// then ultimately reset this.delegate back to its original (parent) reference.
		// this behavior emulates a stack of delegates without actually necessitating one.
		BeanDefinitionParserDelegate parent = this.delegate;
		this.delegate = createDelegate(getReaderContext(), root, parent);

		if (this.delegate.isDefaultNamespace(root)) {
			String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
			if (StringUtils.hasText(profileSpec)) {
				String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
						profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
				// We cannot use Profiles.of(...) since profile expressions are not supported
				// in XML config. See SPR-12458 for details.
				if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec +
								"] not matching: " + getReaderContext().getResource());
					}
					return;
				}
			}
		}

		preProcessXml(root);
		parseBeanDefinitions(root, this.delegate);
		postProcessXml(root);

		this.delegate = parent;
	}

	protected BeanDefinitionParserDelegate createDelegate(
			XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {

		BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
		delegate.initDefaults(root, parentDelegate);
		return delegate;
	}
    
	/**
	 * Parse the elements at the root level in the document:
	 * "import", "alias", "bean".
	 * @param root the DOM root element of the document
	 */
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		if (delegate.isDefaultNamespace(root)) {
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if (node instanceof Element) {
					Element ele = (Element) node;
					if (delegate.isDefaultNamespace(ele)) {
						parseDefaultElement(ele, delegate);
					}
					else {
						delegate.parseCustomElement(ele);
					}
				}
			}
		}
		else {
			delegate.parseCustomElement(root);
		}
	}
	
	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
			importBeanDefinitionResource(ele);
		}
		else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
			processAliasRegistration(ele);
		}
		else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
			processBeanDefinition(ele, delegate);
		}
		else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
			// recurse, 这里递归调用 doRegisterBeanDefinitions 方法
			doRegisterBeanDefinitions(ele);
		}
	}
    
	/**
	 * Process the given bean element, parsing the bean definition
	 * and registering it with the registry.
	 */
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
        // 这里由代理类 BeanDefinitionParserDelegate 执行解析
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		if (bdHolder != null) {
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance.
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}
}
```



```java
public class BeanDefinitionParserDelegate {
	public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";
    
	public static final String BEAN_ELEMENT = "bean";
	public static final String DEFAULT_LAZY_INIT_ATTRIBUTE = "default-lazy-init";
	public static final String DEFAULT_VALUE = "default";
	public static final String FALSE_VALUE = "false";
	public static final String DEFAULT_MERGE_ATTRIBUTE = "default-merge";
	public static final String DEFAULT_AUTOWIRE_ATTRIBUTE = "default-autowire";
	public static final String AUTOWIRE_NO_VALUE = "no";
	public static final String DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE = "default-autowire-candidates";
	public static final String DEFAULT_INIT_METHOD_ATTRIBUTE = "default-init-method";
	public static final String DEFAULT_DESTROY_METHOD_ATTRIBUTE = "default-destroy-method";

	@Nullable
	public BeanDefinitionHolder parseBeanDefinitionElement(Element ele) {
		return parseBeanDefinitionElement(ele, null);
	}

	/**
	 * Parses the supplied {@code <bean>} element. May return {@code null}
	 * if there were errors during parse. Errors are reported to the
	 * {@link org.springframework.beans.factory.parsing.ProblemReporter}.
	 */
	@Nullable
	public BeanDefinitionHolder parseBeanDefinitionElement(Element ele, @Nullable BeanDefinition containingBean) {
		String id = ele.getAttribute(ID_ATTRIBUTE);
		String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);

		List<String> aliases = new ArrayList<>();
		if (StringUtils.hasLength(nameAttr)) {
			String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
			aliases.addAll(Arrays.asList(nameArr));
		}

		String beanName = id;
		if (!StringUtils.hasText(beanName) && !aliases.isEmpty()) {
			beanName = aliases.remove(0);
			if (logger.isTraceEnabled()) {
				logger.trace("No XML 'id' specified - using '" + beanName +
						"' as bean name and " + aliases + " as aliases");
			}
		}

		if (containingBean == null) {
			checkNameUniqueness(beanName, aliases, ele);
		}

		AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(ele, beanName, containingBean);
		if (beanDefinition != null) {
			if (!StringUtils.hasText(beanName)) {
				try {
					if (containingBean != null) {
						beanName = BeanDefinitionReaderUtils.generateBeanName(
								beanDefinition, this.readerContext.getRegistry(), true);
					}
					else {
						beanName = this.readerContext.generateBeanName(beanDefinition);
						// Register an alias for the plain bean class name, if still possible,
						// if the generator returned the class name plus a suffix.
						// This is expected for Spring 1.2/2.0 backwards compatibility.
						String beanClassName = beanDefinition.getBeanClassName();
						if (beanClassName != null &&
								beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() &&
								!this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
							aliases.add(beanClassName);
						}
					}
					if (logger.isTraceEnabled()) {
						logger.trace("Neither XML 'id' nor 'name' specified - " +
								"using generated bean name [" + beanName + "]");
					}
				}
				catch (Exception ex) {
					error(ex.getMessage(), ele);
					return null;
				}
			}
			String[] aliasesArray = StringUtils.toStringArray(aliases);
			return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
		}

		return null;
	}

	/**
	 * Parse the bean definition itself, without regard to name or aliases. May return
	 * {@code null} if problems occurred during the parsing of the bean definition.
	 */
	@Nullable
	public AbstractBeanDefinition parseBeanDefinitionElement(
			Element ele, String beanName, @Nullable BeanDefinition containingBean) {

		this.parseState.push(new BeanEntry(beanName));

		String className = null;
		if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
			className = ele.getAttribute(CLASS_ATTRIBUTE).trim();
		}
		String parent = null;
		if (ele.hasAttribute(PARENT_ATTRIBUTE)) {
			parent = ele.getAttribute(PARENT_ATTRIBUTE);
		}

		try {
			AbstractBeanDefinition bd = createBeanDefinition(className, parent);
            // 这里是解析 bean 的配置
			parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
			bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));

			parseMetaElements(ele, bd);
			parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
			parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
            // 构造器注入，setter 注入
			parseConstructorArgElements(ele, bd);
			parsePropertyElements(ele, bd);
			parseQualifierElements(ele, bd);

			bd.setResource(this.readerContext.getResource());
			bd.setSource(extractSource(ele));

			return bd;
		}
		catch (ClassNotFoundException ex) {
			error("Bean class [" + className + "] not found", ele, ex);
		}
		catch (NoClassDefFoundError err) {
			error("Class that bean class [" + className + "] depends on not found", ele, err);
		}
		catch (Throwable ex) {
			error("Unexpected failure during bean definition parsing", ele, ex);
		}
		finally {
			this.parseState.pop();
		}

		return null;
	}

	/**
	 * Parse constructor-arg sub-elements of the given bean element.
	 */
	public void parseConstructorArgElements(Element beanEle, BeanDefinition bd) {
		NodeList nl = beanEle.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, CONSTRUCTOR_ARG_ELEMENT)) {
				parseConstructorArgElement((Element) node, bd);
			}
		}
	}
    
	/**
	 * Initialize the default lazy-init, autowire, dependency check settings,
	 * init-method, destroy-method and merge settings. Support nested 'beans'
	 * element use cases by falling back to the given parent in case the
	 * defaults are not explicitly set locally.
	 * @see #populateDefaults(DocumentDefaultsDefinition, DocumentDefaultsDefinition, org.w3c.dom.Element)
	 * @see #getDefaults()
	 */
	public void initDefaults(Element root, @Nullable BeanDefinitionParserDelegate parent) {
		populateDefaults(this.defaults, (parent != null ? parent.defaults : null), root);
		this.readerContext.fireDefaultsRegistered(this.defaults);
	}
	
	/**
	 * Populate the given DocumentDefaultsDefinition instance with the default lazy-init,
	 * autowire, dependency check settings, init-method, destroy-method and merge settings.
	 * Support nested 'beans' element use cases by falling back to {@code parentDefaults}
	 * in case the defaults are not explicitly set locally.
	 * @param defaults the defaults to populate
	 * @param parentDefaults the parent BeanDefinitionParserDelegate (if any) defaults to fall back to
	 * @param root the root element of the current bean definition document (or nested beans element)
	 */
	protected void populateDefaults(DocumentDefaultsDefinition defaults, @Nullable DocumentDefaultsDefinition parentDefaults, Element root) {
		String lazyInit = root.getAttribute(DEFAULT_LAZY_INIT_ATTRIBUTE);
		if (DEFAULT_VALUE.equals(lazyInit)) {
			// Potentially inherited from outer <beans> sections, otherwise falling back to false.
			lazyInit = (parentDefaults != null ? parentDefaults.getLazyInit() : FALSE_VALUE);
		}
		defaults.setLazyInit(lazyInit);

		String merge = root.getAttribute(DEFAULT_MERGE_ATTRIBUTE);
		if (DEFAULT_VALUE.equals(merge)) {
			// Potentially inherited from outer <beans> sections, otherwise falling back to false.
			merge = (parentDefaults != null ? parentDefaults.getMerge() : FALSE_VALUE);
		}
		defaults.setMerge(merge);

		String autowire = root.getAttribute(DEFAULT_AUTOWIRE_ATTRIBUTE);
		if (DEFAULT_VALUE.equals(autowire)) {
			// Potentially inherited from outer <beans> sections, otherwise falling back to 'no'.
			autowire = (parentDefaults != null ? parentDefaults.getAutowire() : AUTOWIRE_NO_VALUE);
		}
		defaults.setAutowire(autowire);

		if (root.hasAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE)) {
			defaults.setAutowireCandidates(root.getAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE));
		}
		else if (parentDefaults != null) {
			defaults.setAutowireCandidates(parentDefaults.getAutowireCandidates());
		}

		if (root.hasAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE)) {
			defaults.setInitMethod(root.getAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE));
		}
		else if (parentDefaults != null) {
			defaults.setInitMethod(parentDefaults.getInitMethod());
		}

		if (root.hasAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE)) {
			defaults.setDestroyMethod(root.getAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE));
		}
		else if (parentDefaults != null) {
			defaults.setDestroyMethod(parentDefaults.getDestroyMethod());
		}

		defaults.setSource(this.readerContext.extractSource(root));
	}
}
```

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

可以看到这里将 BeanDefinition 注册到了一个 ConcurrentHashMap 中，其中 key 是 beanName，value 是 BeanDefinition。





