#  2-1  Java 内存区域与内存溢出异常

##  2.2  运行时数据区域

JVM 在执行 Java 程序的过程中会把它所管理的内存划分为若干个不同的数据区域。如图所示：

![jvm_rt](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/jvm_runtime.png)



###  2.2.1  程序计数器

程序计数器（Program Counter Register）是一块较小的内存空间，可以看做当前线程所执行的字节码的行号指示器。

每条线程都有一个独立的 PCR，各条线程之间计数器互不影响，独立存储，称这类内存区域为“线程私有”的内存。

###  2.2.2  Java 虚拟机栈

Java 虚拟机栈（Java Virtual Machine Stacks）也是线程私有的，它的生命周期与线程相同。

> JVM Stacks 描述的是 Java 方法执行的内存模型：每个方法在执行时都会创建一个栈帧（Stack Frame）用于存储局部变量表、操作数栈、动态链接、方法出口等信息。每个方法从调用直至执行完成的过程，就对应着一个栈帧在虚拟机中入栈到出栈的过程。

在 JVM 规范中，对该区域定了两种异常状况：

1. 若请求栈深度大于虚拟机所允许的深度，抛出 StackOverflowError 异常；
2. 若虚拟机可动态扩展，且扩展时无法申请到足够的内存，抛出 OutOfMemoryError 异常。

###  2.2.3  本地方法栈

本地方法栈（Native Method Stack）与虚拟机栈作用相似，区别在于：虚拟机栈为虚拟机执行 Java 方法服务，而本地方法栈则为虚拟机使用到的 Native 方法服务。

###  2.2.4  Java 堆

对多数应用来说，Java 堆（Java Heap）是 JVM 管理的内存中最大的一块。Java 堆是被所有线程共享的一块内存区域，在虚拟机启动时创建。唯一目的就是存放对象实例。

该区域是垃圾收集器管理的主要区域，也被称为“GC 堆”（Garbage Collected Heap）。可细分为：新生代和老年代。

###  2.2.5  方法区

方法区（Method Area）与 Java 堆一样是各个线程共享的内存区域，用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。

无法满足内存分配需求时，抛出 OutOfMemoryError 异常。

###  2.2.6  运行时常量池

运行时常量池（Runtime Constant Pool）是方法区的一部分。

###  2.2.7  直接内存

直接内存（Direct Memory）不是虚拟机运行时数据区的一部分，也不是 JVM 规范中定义的内存区域。













