#  5-1  Java 内存模型与线程



衡量一个服务性能的高低好坏，每秒事务处理数（Transactions Per Second, TPS）是最重要的指标之一，它代表着一秒内服务端平均能响应的请求总数，而 TPS 的值与程序的并发能力又有非常密切的关系。



处理器、高速缓存（Cache）和主内存之间的交互关系如图 12-1 所示：

![12-1](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/cache_coherence.png)



缓存一致性（Cache Conherence）

内存模型，可以理解为在特定的操作协议下，对特定的内存或高速缓存进行读写访问的过程抽象。



##  12.3  Java 内存模型

Java 虚拟机规范中试图定义一种 Java 内存模型（Java Memory Model, JMM）来屏蔽掉各种硬件和操作系统的内存访问诧异，以实现让 Java 程序在各种平台下都能达到一致的内存访问效果。

> 根据 Java 虚拟机规范的规定，volatile 变量依然有工作内存的拷贝，但是由于它特殊的操作顺序规定，所以看起来如同直接在主内存中读写访问一般。



###  12.3.1  主内存与工作内存

Java 线程、主内存和工作内存三者的交互关系如图 12-2 所示（与图 12-2 对比）：

![12-2](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/working_memory.png)



###  12.3.2  内存交互操作

关于主内存与工作内存之间具体的交互协议，即一个变量如何从主内存拷贝到工作内存、如何从工作内存同步回主内存之类的实现细节，Java 内存模型中定义了以下 8 种操作来完成，虚拟机实现时必须保证下面提及的每一种操作都是原子的、不可再分的（对于 double 和 long 类型的变量来说，load、store、read 和 write 操作在某些平台上允许有例外）。

8 种操作如下：

| 操作          | 作用范围 | 作用                                                         |
| ------------- | -------- | ------------------------------------------------------------ |
| lock (锁定)   | 主内存   | 把变量标识为一条线程独占的状态                               |
| unlock (解锁) | 主内存   | 释放锁定状态的变量（释放后才可以被其他线程锁定）             |
| read (读取)   | 主内存   | 把变量的值从主内存传输到线程工作内存（以便随后的 read 使用） |
| load (载入)   | 工作内存 | 把 read 得到的变量值放入工作内存的变量副本中                 |
| use (使用)    | 工作内存 | 把工作内存中变量的值传递给执行引擎，<br />虚拟机遇到需要使用到变量值的字节码指令时执行该操作 |
| assign (赋值) | 工作内存 | 把从执行引擎接收到的值赋给工作内存的变量，<br />虚拟机遇到给变量赋值的字节码执行时执行该操作 |
| store (存储)  | 工作内存 | 把工作内存中变量的值传送到主内存中（以便随后的 write 使用）  |
| write (写入)  | 主内存   | 把 store 从工作内存中得到的变量的值放入主内存变量中          |

若要把变量从主内存复制到工作内存，就要顺序的执行 read 和 load 操作；

若要把变量从工作内存同步回主内存，就要顺序的执行 store 和 write 操作。

> 注意：二者有先后顺序要求，但不保证连续。



JMM 规定了在执行上述 8 种基本操作时必须满足以下规则：

- 不允许 read 和 load、store 和 write 操作之一单独出现（即，要成对出现）
- 不允许线程丢弃它最近的 assign 操作（即，变量在工作内存中改变之后必须同步回主内存）
- 不允许线程无原因地（无任何 assign 操作）把数据从工作内存同步回主内存
- 新的变量只能在主内存“诞生”，不允许工作内存中直接使用未被初始化（load 和 assign）的变量（即 load 后才能 use，assign 后才能 store）
- 一个变量在同一时刻只允许一条线程对其进行 lock，但 lock 可被同一线程重复执行多次（需要相同次数的 unlock 才能解锁）
- 若对变量执行 lock 操作，将会清空工作内存中该变量的值，执行引擎使用该变量前，需重新 load 或 assign
- 若变量未被 lock 锁定，则不允许对其进行 unlock；也不允许 unlock 其他线程锁定的变量
- 对变量执行 unlock 前，必须先把该变量同步回主内存中（执行 store、write 操作）

###  12.3.3  对 volatile 型变量的特殊规则

关键字 volatile 可以说是 JVM 提供的最轻量级的同步机制。该关键字有两个特性：

1. 可见性。保证 volatile 变量的可见性（即，当一条线程修改了该变量的值，新值对于其他线程来说是可以立即得知的。普通变量不能做到这一点，普通变量的值在线程间传递均需要通过主内存来完成）

2. 禁止指令重排序优化。普通变量仅会保证在该方法的执行过程中所有依赖赋值结果的地方都能获取到正确的结果，而不能保证变量赋值操作的顺序与程序代码中的执行顺序一致。即 JMM 描述的“线程内表现为串行的语义（Within-Thread-AS-If-Serial Semantics）”。

> volatile 变量对应的操作相当于一个内存屏障（Memory Barrier 或 Memory Fence，指令重排序时不能把后面的指令重排序到内存屏障之前的位置），只有一个 CPU 访问内存时，无需内存屏障；若有多个 CPU 访问同一块内存，且其中一个在观测另一个，则需内存屏障来保证一致性。
>
> 
>
> PS : 实际是在汇编指令中多了一个 lock 操作，使得本 CPU 的 Cache 写入了内存，并引起其他 CPU 或内核无效化（Invalidate）其 Cache。因此可以让 volatile 变量的修改对其他 CPU 立即可见。

示例代码：

```java
Map configOptions;
char[] configText;
// 此变量必须定义为 volatile
volatile boolean initialized = false;

// 假设以下代码在线程 A 中执行
// 模拟读配置信息，当读取完成后将 initialized 设置为 true 以通知其他线程配置可用
configOptions = new HashMap();
configText = readConfigFile(fileName);
processConfigOptions(configText, configOptions);
initialized = true;

// 假设以下代码在线程 B 中执行
// 等待 initialized 为 true，代表线程 A 已经把配置信息初始化完成
while (!initialized) {
    sleep();
}
// 使用线程 A 中初始化好的配置信息
doSomethingWithConfig();
```

> volatile 变量在各个线程的工作内存中不存在一致性问题（在各个线程的工作内存中，volatile 变量也可以不存在一致性的情况，但由于每次使用之前都要进行刷新，执行引擎看不到不一致的情况，因此可以认为不存在一致性的问题），但 Java 里的运算并非原子操作，导致 volatile 变量的运算在并发下一样不安全。

####  小结

JMM 对 volatile 变量的特殊规则：

- 在工作内存中，每次使用 volatile 变量前，都必须先从主内存刷新最新的值，保证看起其他线程的修改；
- 在工作内存中，每次修改 volatile 变量后，都必须立即同步回主内存中，保证其他线程可以看到修改；
- volatile 变量不会被指令重排序优化，保证代码执行顺序与程序的顺序相同。



###  12.3.4  对于 long 和 double 型变量的特殊规则

> 实际开发中，目前各种平台下的商用 JVM 几乎都把 64 位数据的读写作为原子操作对待，因此不需把用到的 long 和 double 变量专门声明为 volatile。



###  12.3.5  原子性、可见性与有序性

JMM 是围绕着在并发过程中如何处理原子性、可见性和有序性这三个特征来建立的。

- 原子性（Atomicity）


- 可见性（Visibility）

可见性是指当一个线程修改了共享变量的值，其他线程能够立即得知这个修改。

JMM 是通过在变量修改后将新值同步回主内存，在变量读取前从主内存刷新变量值这种依赖主内存作为传递媒介的方式来实现可见性的，无论是普通变量还是 volatile 变量都是如此。

> 普通变量与 volatile 变量的区别：volatile 的特殊规则保证了新值能立即同步到主内存，以及每次使用前立即从主内存刷新。因此可以说 volatile 保证了多线程操作时变量的可见性，而普通变量则不能保证这一点。

除了 volatile，Java 还有两个关键字能实现可见性：synchronized 和 final。

1. 同步代码块可见性：“对一个变量执行 unlock 之前，必须先把此变量同步回主内存（执行 store、write）”
2. final 可见性：被 final 修饰的字段在构造器中一旦初始化完成，并且构造器没有把“this“引用传递出去，其他线程中就能看见 final 字段的值。

示例代码：

```java
// 变量 i 和 j 都具备可见性
public static final int i;
public final int j;

static {
    i = 0;
}

{
    // 或者在构造器初始化
    j = 0;
}
```

- 有序性（Ordering）

Java 提供了 volatile 和 synchronized 关键字来保证线程之间操作的有序性。

volatile：禁止指令重排序；

synchronized：一个变量在同一时刻只允许一条线程对其进行 lock 操作。

















