#  Spring IoC 容器依赖注入 - 从缓存获取对象

上文「[Spring IoC 依赖注入 - 概述](https://github.com/JiaoXR/Reading-Notes/blob/master/%E3%80%8ASpring%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95%E3%80%8B/Spring%20IoC%E4%BE%9D%E8%B5%96%E6%B3%A8%E5%85%A5-1-%E6%A6%82%E8%BF%B0.md)」概述了 IoC 依赖注入的过程（也即是 getBean 方法的实现），本文主要分析其中的第一步：从缓存中获取单例对象。



###  处理传入的 name

由于用户传入的 name 不一定规范（可能是 alias、或者 FactoryBean 的名称等），因此首先要做的就是规范化传入的 bean 名称，将其转为规范的 beanName。如下：

```java
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
    /**
     * 剥离 FactoryBean 的标识，并转为规范名
     * Return the bean name, stripping out the factory dereference prefix if necessary,
     * and resolving aliases to canonical names.
     * @param name the user-specified name
     * @return the transformed bean name
     */
    protected String transformedBeanName(String name) {
        return canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }
}
```

若传入的是 FactoryBean 格式（名字以 & 开头），则剥离开头的 & 符号：

```java
public abstract class BeanFactoryUtils {
    /**
     * <name, beanName> 映射
     * Cache from name with factory bean prefix to stripped name without dereference.
     */
    private static final Map<String, String> transformedBeanNameCache = new ConcurrentHashMap<>();

    /**
     * 剥离 & 前缀
     * Return the actual bean name, stripping out the factory dereference
     * prefix (if any, also stripping repeated factory prefixes if found).
     */
    public static String transformedBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");
        // 若不是 FactoryBean，直接返回(FactoryBean 名字以 & 开头)
        if (!name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
            return name;
        }
        // 若以 & 开头，循环删除 & 前缀
        // PS: 之所以用循环，是考虑开头可能会有多个 & 的情况
        return transformedBeanNameCache.computeIfAbsent(name, beanName -> {
            do {
                beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
            }
            while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
            return beanName;
        });
    }
}
```

将 name 解析为规范名（传入的 name 可能是别名）：

```java
public class SimpleAliasRegistry implements AliasRegistry {
    /** Map from alias to canonical name. */
    private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);

    /**
     * Determine the raw name, resolving aliases to canonical names.
     */
    public String canonicalName(String name) {
        String canonicalName = name;
        // Handle aliasing...
        String resolvedName;
        do {
            // alias 可能还有 alias
            resolvedName = this.aliasMap.get(canonicalName);
            if (resolvedName != null) {
                canonicalName = resolvedName;
            }
        }
        while (resolvedName != null);
        return canonicalName;
    }
}
```



###  从缓存中获取单例对象

从缓存中获取所需的 Bean 对象，如下：

```java
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
    /** Cache of singleton objects: bean name to bean instance. */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /** Cache of early singleton objects: bean name to bean instance. */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    
    /** Names of beans that are currently in creation. */
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));    

    /**
     * Return whether the specified singleton bean is currently in creation
     * (within the entire factory).
     * @param beanName the name of the bean
     */
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }
    
    @Override
    @Nullable
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }
    
    /**
     * Return the (raw) singleton object registered under the given name.
     * Checks already instantiated singletons and also allows for an early
     * reference to a currently created singleton (resolving a circular reference).
     * @param beanName the name of the bean to look for
     * @param allowEarlyReference whether early references should be created or not
     * @return the registered singleton object, or {@code null} if none found
     */
    @Nullable
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        // 先从单例缓存（singletonObjects）中取对象；若已存在则直接返回
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        // 使用 ObjectFactory 创建 Bean
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singletonObject;
    }
}
```

若缓存中已存在所需对象，对其进行如下处理：

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
        implements AutowireCapableBeanFactory {

    /**
     * The name of the currently created bean, for implicit dependency registration
     * on getBean etc invocations triggered from a user-specified Supplier callback.
     */
    private final NamedThreadLocal<String> currentlyCreatedBean = new NamedThreadLocal<>("Currently created bean");

    /**
     * Overridden in order to implicitly register the currently created bean as
     * dependent on further beans getting programmatically retrieved during a
     * {@link Supplier} callback.
     */
    @Override
    protected Object getObjectForBeanInstance(
            Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

        String currentlyCreatedBean = this.currentlyCreatedBean.get();
        if (currentlyCreatedBean != null) {
            registerDependentBean(beanName, currentlyCreatedBean);
        }

        return super.getObjectForBeanInstance(beanInstance, name, beanName, mbd);
    }
}
```



```java
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
    
    /** Map between dependent bean names: bean name to Set of dependent bean names. */
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

    /** Map between depending bean names: bean name to Set of bean names for the bean's dependencies. */
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);

    /**
     * 为给定的 bean 注册一个依赖 bean，在销毁给定 bean 之前销毁它
     * Register a dependent bean for the given bean,
     * to be destroyed before the given bean is destroyed.
     * @param beanName the name of the bean
     * @param dependentBeanName the name of the dependent bean
     */
    public void registerDependentBean(String beanName, String dependentBeanName) {
        String canonicalName = canonicalName(beanName);

        synchronized (this.dependentBeanMap) {
            Set<String> dependentBeans =
                    this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
            if (!dependentBeans.add(dependentBeanName)) {
                return;
            }
        }

        synchronized (this.dependenciesForBeanMap) {
            Set<String> dependenciesForBean =
                    this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
            dependenciesForBean.add(canonicalName);
        }
    }

}
```



```java
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
    /**
     * Get the object for the given bean instance, either the bean
     * instance itself or its created object in case of a FactoryBean.
     * @param beanInstance the shared bean instance
     * @param name name that may include factory dereference prefix
     * @param beanName the canonical bean name
     * @param mbd the merged bean definition
     * @return the object to expose for the bean
     */
    protected Object getObjectForBeanInstance(
            Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

        // 判断要获取的 Bean 是否为 FactoryBean
        // Don't let calling code try to dereference the factory if the bean isn't a factory.
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            if (beanInstance instanceof NullBean) {
                return beanInstance;
            }
            if (!(beanInstance instanceof FactoryBean)) {
                throw new BeanIsNotAFactoryException(transformedBeanName(name), beanInstance.getClass());
            }
        }

        // Now we have the bean instance, which may be a normal bean or a FactoryBean.
        // If it's a FactoryBean, we use it to create a bean instance, unless the
        // caller actually wants a reference to the factory.
        // 若不是 FactoryBean，则返回该对象??
        if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
            return beanInstance;
        }

        Object object = null;
        if (mbd == null) {
            object = getCachedObjectForFactoryBean(beanName);
        }
        if (object == null) {
            // Return bean instance from factory.
            FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
            // Caches object obtained from FactoryBean if it is a singleton.
            if (mbd == null && containsBeanDefinition(beanName)) {
                mbd = getMergedLocalBeanDefinition(beanName);
            }
            boolean synthetic = (mbd != null && mbd.isSynthetic());
            object = getObjectFromFactoryBean(factory, beanName, !synthetic);
        }
        return object;
    }

}
```


