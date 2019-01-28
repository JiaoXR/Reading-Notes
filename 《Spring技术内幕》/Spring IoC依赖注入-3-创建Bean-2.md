#  Spring IoC依赖注入-3-创建Bean

上文「[Spring IoC 容器依赖注入-创建Bean-1](https://github.com/JiaoXR/Reading-Notes/blob/master/%E3%80%8ASpring%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95%E3%80%8B/Spring%20IoC%E4%BE%9D%E8%B5%96%E6%B3%A8%E5%85%A5-3-%E5%88%9B%E5%BB%BABean.md)」分析了 Spring IoC 容器在创建 Bean 之前的一些准备工作，本文将进一步分析。



*AbstractBeanFactory* 类创建 Bean 对象部分的代码如下：

```java
// 创建单例 Bean
// Create bean instance.
if (mbd.isSingleton()) {
	sharedInstance = getSingleton(beanName, () -> {
		try {
			return createBean(beanName, mbd, args);
		}
		catch (BeansException ex) {
			// Explicitly remove instance from singleton cache: It might have been put there
			// eagerly by the creation process, to allow for circular reference resolution.
			// Also remove any beans that received a temporary reference to the bean.
			destroySingleton(beanName);
			throw ex;
		}
	});
	bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
}
// 创建 prototype Bean
else if (mbd.isPrototype()) {
	// It's a prototype -> create a new instance.
	Object prototypeInstance = null;
	try {
		beforePrototypeCreation(beanName);
		prototypeInstance = createBean(beanName, mbd, args);
	}
	finally {
		afterPrototypeCreation(beanName);
	}
	bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
}
// 根据 scope 创建 Bean
else {
	String scopeName = mbd.getScope();
	final Scope scope = this.scopes.get(scopeName);
	if (scope == null) {
		throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
	}
	try {
		Object scopedInstance = scope.get(beanName, () -> {
			beforePrototypeCreation(beanName);
			try {
				return createBean(beanName, mbd, args);
			}
			finally {
				afterPrototypeCreation(beanName);
			}
		});
		bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
	}
	catch (IllegalStateException ex) {
		throw new BeanCreationException(beanName,
				"Scope '" + scopeName + "' is not active for the current thread; consider " +
				"defining a scoped proxy for this bean if you intend to refer to it from a singleton",
				ex);
	}
}
```

上述过程大概可以分为三种情况：

1. 创建单例 Bean 对象；
2. 创建 prototype Bean 对象；
3. 根据其他 scope 创建 Bean 对象。



###  创建单例 Bean 对象

- 创建单例对象会调用 *DefaultSingletonBeanRegistry* 的 *getSingleton(String, ObjectFactory<?>)* 方法，如下：

```java
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {

	/** Cache of singleton objects: bean name to bean instance. */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

	/** Flag that indicates whether we're currently within destroySingletons. */
	private boolean singletonsCurrentlyInDestruction = false;

	/**
	 * Return the (raw) singleton object registered under the given name,
	 * creating and registering a new one if none registered yet.
	 * @param beanName the name of the bean
	 * @param singletonFactory the ObjectFactory to lazily create the singleton
	 * with, if necessary
	 * @return the registered singleton object
	 */
	public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(beanName, "Bean name must not be null");
		synchronized (this.singletonObjects) {
			Object singletonObject = this.singletonObjects.get(beanName);
			if (singletonObject == null) {
				if (this.singletonsCurrentlyInDestruction) {
					throw new BeanCreationNotAllowedException(beanName,
							"Singleton bean creation not allowed while singletons of this factory are in destruction " +
							"(Do not request a bean from a BeanFactory in a destroy method implementation!)");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
				}
                // 创建对象之前
				beforeSingletonCreation(beanName);
				boolean newSingleton = false;
				boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
				if (recordSuppressedExceptions) {
					this.suppressedExceptions = new LinkedHashSet<>();
				}
				try {
                    // 创建 Bean 对象的方法
                    // 这里调用的是 AbstractAutowireCapableBeanFactory 的 createBean 方法
					singletonObject = singletonFactory.getObject();
					newSingleton = true;
				}
				catch (IllegalStateException ex) {
					// Has the singleton object implicitly appeared in the meantime ->
					// if yes, proceed with it since the exception indicates that state.
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						throw ex;
					}
				}
				catch (BeanCreationException ex) {
					if (recordSuppressedExceptions) {
						for (Exception suppressedException : this.suppressedExceptions) {
							ex.addRelatedCause(suppressedException);
						}
					}
					throw ex;
				}
				finally {
					if (recordSuppressedExceptions) {
						this.suppressedExceptions = null;
					}
                    // 创建对象之后
					afterSingletonCreation(beanName);
				}
                // 若创建成功，则添加到缓存中
				if (newSingleton) {
					addSingleton(beanName, singletonObject);
				}
			}
			return singletonObject;
		}
	}
}
```

- *beforeSingletonCreation* & *afterSingletonCreation*

```java
/** Names of beans that are currently in creation. */
private final Set<String> singletonsCurrentlyInCreation =
		Collections.newSetFromMap(new ConcurrentHashMap<>(16));

/** Names of beans currently excluded from in creation checks. */
private final Set<String> inCreationCheckExclusions =
		Collections.newSetFromMap(new ConcurrentHashMap<>(16));

/**
 * Return whether the specified singleton bean is currently in creation
 * (within the entire factory).
 */
public boolean isSingletonCurrentlyInCreation(String beanName) {
	return this.singletonsCurrentlyInCreation.contains(beanName);
}

/**
 * Callback before singleton creation.
 * The default implementation register the singleton as currently in creation.
 */
protected void beforeSingletonCreation(String beanName) {
	if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
		throw new BeanCurrentlyInCreationException(beanName);
	}
}

/**
 * Callback after singleton creation.
 * The default implementation marks the singleton as not in creation anymore.
 */
protected void afterSingletonCreation(String beanName) {
	if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
		throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
	}
}
```

该方法的第二个参数为 *ObjectFactory* 类型，其定义如下：

```java
@FunctionalInterface
public interface ObjectFactory<T> {

	/**
	 * Return an instance (possibly shared or independent)
	 * of the object managed by this factory.
	 */
	T getObject() throws BeansException;
}
```

可以看到这是一个函数接口。这里传入的是一个方法（也是创建 Bean 对象的地方），具体实现在 *createBean* 方法中，如下：

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
		implements AutowireCapableBeanFactory {

	/**
	 * Central method of this class: creates a bean instance,
	 * populates the bean instance, applies post-processors, etc.
	 * @see #doCreateBean
	 */
	@Override
	protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
			throws BeanCreationException {

		if (logger.isTraceEnabled()) {
			logger.trace("Creating instance of bean '" + beanName + "'");
		}
		RootBeanDefinition mbdToUse = mbd;

		// 确保此时实际解析了 bean 类，并在动态解析的 Class 的情况下克隆 bean 定义，
		// 该类无法存储在共享的合并 bean 定义中。
		// Make sure bean class is actually resolved at this point, and
		// clone the bean definition in case of a dynamically resolved Class
		// which cannot be stored in the shared merged bean definition.
		Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
		if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
			mbdToUse = new RootBeanDefinition(mbd);
			mbdToUse.setBeanClass(resolvedClass);
		}

		// Prepare method overrides.
		try {
			mbdToUse.prepareMethodOverrides();
		}
		catch (BeanDefinitionValidationException ex) {
			throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
					beanName, "Validation of method overrides failed", ex);
		}

		try {
			// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
			Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
			if (bean != null) {
				return bean;
			}
		}
		catch (Throwable ex) {
			throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
					"BeanPostProcessor before instantiation of bean failed", ex);
		}

		try {
			// 实际创建 Bean 对象的地方
			Object beanInstance = doCreateBean(beanName, mbdToUse, args);
			if (logger.isTraceEnabled()) {
				logger.trace("Finished creating instance of bean '" + beanName + "'");
			}
			return beanInstance;
		}
		catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
			// A previously detected exception with proper bean creation context already,
			// or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
			throw ex;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
		}
	}

}
```

然而，这里还不是最终创建 Bean 对象的地方，最终的地方在 *doCreateBean* 方法中，下文再进行分析。

