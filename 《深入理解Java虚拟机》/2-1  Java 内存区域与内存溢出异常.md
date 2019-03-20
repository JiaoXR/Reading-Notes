#  2-1  Java 内存区域与内存溢出异常

##  2.2  运行时数据区域

JVM 在执行 Java 程序的过程中会把它所管理的内存划分为若干个不同的数据区域。如图所示：

![jvm_rt](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/jvm_runtime.png)



###  2.2.1  程序计数器

- 程序计数器（Program Counter Register）是一块较小的内存空间，可以看做当前线程所执行的字节码的行号指示器。

- 每条线程都有一个独立的 PCR，各条线程之间计数器互不影响，独立存储，称这类内存区域为“线程私有”的内存。

> 如果线程正在执行的是一个 Java 方法，这个计数器记录的是正在执行的虚拟机字节码指令的地址；如果正在执行的是 Native 方法，这个计数器值则为空（Undefined）。
>
> 此内存区域是唯一一个在 JVM 规范中没有规定任何 OutOfMemoryError 情况的区域。

###  2.2.2  JVM 栈

- Java 虚拟机栈（Java Virtual Machine Stacks）也是线程私有的，它的生命周期与线程相同。

- JVM Stacks 描述的是 Java 方法执行的内存模型：每个方法在执行时都会创建一个栈帧（Stack Frame）用于存储局部变量表、操作数栈、动态链接、方法出口等信息。每个方法从调用直至执行完成的过程，就对应着一个栈帧在虚拟机中入栈到出栈的过程。

- JVM 规范中该区域定的两种异常状况
    - 若请求栈深度大于虚拟机所允许的深度，抛出 StackOverflowError 异常；
    - 若虚拟机可动态扩展，且扩展时无法申请到足够的内存，抛出 OutOfMemoryError 异常。

###  2.2.3  本地方法栈

- 本地方法栈（Native Method Stack）与虚拟机栈作用相似，区别在于：虚拟机栈为虚拟机执行 Java 方法服务，而本地方法栈则为虚拟机使用到的 Native 方法服务。

- 异常
    - StackOverflowError
    - OutOfMemoryError

###  2.2.4  Java 堆

- 对多数应用来说，Java 堆（Java Heap）是 JVM 管理的内存中最大的一块。Java 堆是被所有线程共享的一块内存区域，在虚拟机启动时创建。

- 唯一目的：存放对象实例（几乎所有的对象实例都在这里分配内存）。

- 垃圾收集器管理的主要区域，也被称为“GC 堆”（Garbage Collected Heap）。可细分为：新生代和老年代。
- 参数：`-Xmx`, `-Xms`
- 异常：OutOfMemoryError

###  2.2.5  方法区

- 方法区（Method Area）与 Java 堆一样是各个线程共享的内存区域，用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。

- 异常：OutOfMemoryError

###  2.2.6  运行时常量池

- 运行时常量池（Runtime Constant Pool）是方法区的一部分。
- Class 文件中除了有类的版本、字段、方法、接口等描述外信息，还有一项信息是常量池（Constant Pool Table），用于存放编译期生成的各种字面量和符号引用，这部分内容将在类加载后进入方法区的运行时常量池中存放。

- 相比于 Class 文件常量池的一个重要特性是动态性。运行期间也可能将新的常量放入池中（例如 String 类的 intern() 方法）。

- 异常：OutOfMemoryError

###  2.2.7  直接内存（Direct Memory）

- 不是虚拟机运行时数据区的一部分，也不是 JVM 规范中定义的内存区域。
- 该部分被频繁地使用，也可能导致 OutOfMemoryError。

> JDK 1.4 引入 NIO（New Input/Output）类，引入了基于通道（Channel）与缓冲区（Buffer）的 I/O 方式，它可以使用 Native 函数库直接分配堆外内存，然后通过一个存储在 Java 堆中的 DirectByteBuffer 对象作为这块内存的引用进行操作。这样能在一些场景中显著提高性能，因为避免了在 Java 堆和 Native 堆中来回复制数据。

##  2.3  HotSpot 虚拟机对象探秘

###  2.3.1  对象的创建

大概流程图如下：

![object](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/JVM/JVM_object_init.svg)

- 虚拟机遇到一条 *new* 指令时，首先检查这个指令的参数是否能在常量池中定位到一个类的符号引用，并且检查这个符号引用所代表的类是否已被加载、解析和初始化过；若没有，则必须先执行类加载过程。

为对象分配空间相当于把一块确定大小的内存从 Java 堆中划分出来。为新生对象分配内存的方式有以下两种：

- 指针碰撞

假设 Java 堆中内存是绝对规整的，所有用过的内存都放在一边，空闲的内存放在一边，中间放着一个指针作为分界点的指示器，那所分配的内存就仅仅是把那个指针向空闲空间那边挪动一段与内存大小相等的距离。这种分配方式称为“指针碰撞”（Bump the Pointer）。

- 空闲列表

若 Java 堆中的内存不规整，已使用的内存和空闲的内存相互交错，那就无法简单地进行指针碰撞了，VM 必须维护一个列表，记录哪些内存块可用，在分配时从列表中找到一块足够大的空间划分给对象实例，并更新列表上的记录。这种分配方式称为“空闲列表”（Free List）。

> 选择哪种分配方式由 Java 堆是否规整决定，而 Java 堆是否规整又由所采用的垃圾收集器是否带有压缩整理功能决定。
>
> 因此，在使用 Serial、ParNew 等带 Compact 过程的收集器时，系统采用的分配算法是指针碰撞；而使用 CMS 这种基于 Mark-Sweep 算法的收集器时，通常采用空闲列表。

####  如何保证内存分配线程安全？

- 同步

CAS + 失败重试

- 本地线程分配缓冲

每个线程在 Java 堆中预先分配一小块内存，称为本地线程分配缓存（Thread Local Allocation Buffer, TLAB）。哪个线程要分配内存，就在哪个线程的 TLAB 上分配，只有 TLAB 用完并分配新的 TLAB 时，才需要同步锁定。

参数：`-XX:+/-UseTLAB` 

- 初始化
    - 内存分配完后，虚拟机需要将分配到的内存空间都初始化为零值（不包括对象头）。
    - VM 对对象进行必要的设置，例如这个对象是哪个类的实例、如何才能找到类的元数据信息、对象的哈希码、对象的 GC 分代年龄等信息。这些信息存放在对象的对象头（Object Header）之中。
    - 执行 *new* 指令之后会接着执行 `<init>` 方法，把对象按照程序猿的意思进行初始化，这样一个真正可用的对象才算完全产生出来。

###  2.3.2  对象的内存布局

> HotSpot 虚拟机中，对象在内存中存储的布局可以分为三块区域：对象头（Header）、实例数据（Instance Data）和对齐填充（Padding）。

HotSpot 虚拟机的对象头包括两部分信息，包括：

- Mark Word

存储对象自身的运行时数据，如哈希码（HashCode）、GC 分代年龄、锁状态标志、线程持有的锁、偏向线程ID、偏向时间戳等，官方称 "Mark Word"。

- 类型指针

即对象指向它的类元数据的指针，虚拟机通过该指针来确定这个对象是哪个类的实例。

###  2.3.3  对象的访问定位

Java 程序需要通过栈上的 reference 数据来操作堆上的具体对象，主流的对象访问方式有两种：

- 句柄

Java 堆中会划出一块内存作为句柄池，reference 中存储的就是对象的句柄地址，句柄中包含了对象实例数据与类型数据各自的具体地址信息，如图所示：

![](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/object_access_handle.png)

优点：reference 中存储的是稳定的句柄地址，在对象被移动（垃圾收集时移动对象是非常普遍的行为）时，只会改变句柄中的实例数据指针，而 reference 本身无需修改。

- 直接指针

Java 堆对象的布局中，reference 中存储的就是对象地址。如图所示：

![](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/object_access_pointer.png)

优点：速度更快，节省了一次指针定位的时间开销（由于对象访问在 Java 中非常频繁，因此这类开销积少成多后也是非常可观的执行成本）。

> HotSpot 使用直接指针进行对象访问。
>
> 从整个软件开发范围看，各种语言和框架使用句柄访问也很常见。



##  2.4   实战：OutOfMemoryError 异常

JVM 规范描述中，除了程序计数器，虚拟机内存的其他几个运行时区域都有发生 OutOfMemoryError（OOM）异常的可能。

###  2.4.1  Java 堆溢出

Java 堆用于存储对象实例，只要不断地创建对象，并且保证 GC Roots 到对象之间有可达路径来避免垃圾回收机制清除这些对象，在对象数量到达最大堆的容量限制后就会产生内存溢出异常。

- 示例代码

```java
public class HeapOOM {
    static class OOMObject {
    }

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();
        while (true) {
            list.add(new OOMObject());
        }
    }
}
```



> VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
>
> 1. 将堆的最小值 -Xms 参数与最大值 -Xmx 参数设置为一样即可避免堆自动扩展；
> 2. 通过参数 -XX:+HeapDumpOnOutOfMemoryError 可以让虚拟机在出现 OOM 时 Dump 出当前的内存堆转储快照以便分析。



- 运行结果

```bash
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid7428.hprof ...
Heap dump file created [27750745 bytes in 0.347 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
    at java.util.Arrays.copyOf(Arrays.java:2245)
    ...
```



###  2.4.2  虚拟机栈和本地方法栈溢出

> HotSpot 虚拟机并不区分虚拟机栈和本地方法栈。因此对于 HotSpot 虚拟机而言，-Xoss 参数（设置本地方法栈大小）虽然存在，但实际无效，栈容量只有 -Xss 参数设定。
>
> JVM 规范中描述了两种异常：StackOverflowError 和 OutOfMemoryError。



- 示例代码

```java
public class JavaVMStackSOF {
    private int stackLength = 1;

    private void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackSOF sof = new JavaVMStackSOF();
        try {
            sof.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length: " + sof.stackLength);
            throw e;
        }
    }
}
```

> VM Args: -Xss160k

- 运行结果

```bash
stack length: 774
Exception in thread "main" java.lang.StackOverflowError
    at com.jaxer.example.jvm.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:13)
    at com.jaxer.example.jvm.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:14)
    ...
```

> 单线程情况下，无论是栈帧太大还是虚拟机容量太小，当内存无法分配的时候，虚拟机抛出的都是 StackOverflowError。



###  2.4.3  方法区和运行时常量池溢出

```java
public class JavaMethodAreaOOM {
    public static void main(String[] args) {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    return methodProxy.invokeSuper(o, objects);
                }
            });
            enhancer.create();
        }
    }

    static class OOMObject {
    }
}
```

- 运行结果

```bash
Exception in thread "main" 
Exception: java.lang.OutOfMemoryError thrown from the UncaughtExceptionHandler in thread "main"
```



PS: JDK 1.8 会有如下警告：

```bash
Java HotSpot(TM) 64-Bit Server VM warning: ignoring option PermSize=10M; support was removed in 8.0
Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=10M; support was removed in 8.0
```



###  2.4.4  本机直接内存溢出

> Unsafe 类的 getUnsafe() 方法限制了只有引导类加载器才会返回实例，也就是设计者希望只有 rt.jar 中的类才能使用 Unsafe 的功能。

- 使用 Unsafe 分配本机内存，示例代码

```java
public class DirectMemoryOOM {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws Exception {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true) {
            unsafe.allocateMemory(_1MB);
        }
    }
}
```



PS: 本人测试未抛出异常，程序一直未结束……

