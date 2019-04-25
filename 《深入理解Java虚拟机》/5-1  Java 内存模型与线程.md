#  5-1  Java 内存模型与线程

衡量一个服务性能的高低好坏，每秒事务处理数（Transactions Per Second, TPS）是最重要的指标之一，它代表着一秒内服务端平均能响应的请求总数，而 TPS 的值与程序的并发能力又有非常密切的关系。



处理器、高速缓存（Cache）和主内存之间的交互关系如图 12-1 所示：

![12-1](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/cache_coherence.png)

缓存一致性（Cache Conherence）

内存模型，可以理解为在特定的操作协议下，对特定的内存或高速缓存进行读写访问的过程抽象。



##  12.3  Java 内存模型

Java 虚拟机规范中试图定义一种 Java 内存模型（Java Memory Model, JMM）来屏蔽掉各种硬件和操作系统的内存访问差异，以实现让 Java 程序在各种平台下都能达到一致的内存访问效果。

> 根据 Java 虚拟机规范的规定，volatile 变量依然有工作内存的拷贝，但是由于它特殊的操作顺序规定，所以看起来如同直接在主内存中读写访问一般。



###  12.3.1  主内存与工作内存

Java 线程、主内存和工作内存三者的交互关系如图 12-2 所示（与图 12-1 对比）：

![12-2](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/working_memory.png)



###  12.3.2  内存交互操作

关于主内存与工作内存之间具体的交互协议，即一个变量如何从主内存拷贝到工作内存、如何从工作内存同步回主内存之类的实现细节，Java 内存模型中定义了以下 8 种操作来完成，虚拟机实现时必须保证下面提及的每一种操作都是原子的、不可再分的（对于 double 和 long 类型的变量来说，load、store、read 和 write 操作在某些平台上允许有例外）。

8 种操作如下：

| 操作          | 作用范围 | 作用                                                         |
| ------------- | -------- | ------------------------------------------------------------ |
| lock (锁定)   | 主内存   | 把变量标识为一条线程独占的状态                               |
| unlock (解锁) | 主内存   | 释放锁定状态的变量（释放后才可以被其他线程锁定）             |
| read (读取)   | 主内存   | 把变量的值从主内存传输到线程工作内存（以便随后的 load 使用） |
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

    适用场景举例：

    ```java
    public class VolatileTest {
        volatile boolean shutdownRequested;
    
        public void shutdown() {
            shutdownRequested = true;
        }
    
        public void doWork() {
            while (!shutdownRequested) {
                // do stuff
            }
        }
    }
    ```

2. 禁止指令重排序优化。普通变量仅会保证在该方法的执行过程中所有依赖赋值结果的地方都能获取到正确的结果，而不能保证变量赋值操作的顺序与程序代码中的执行顺序一致。即 JMM 描述的“线程内表现为串行的语义（Within-Thread-AS-If-Serial Semantics）”。

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

> volatile 变量对应的操作相当于一个内存屏障（Memory Barrier 或 Memory Fence，指令重排序时不能把后面的指令重排序到内存屏障之前的位置），只有一个 CPU 访问内存时，无需内存屏障；若有多个 CPU 访问同一块内存，且其中一个在观测另一个，则需内存屏障来保证一致性。
>
> 
>
> PS : 实际是在汇编指令中多了一个 lock 操作，使得本 CPU 的 Cache 写入了内存，并引起其他 CPU 或内核无效化（Invalidate）其 Cache。因此可以让 volatile 变量的修改对其他 CPU 立即可见。
>
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

####  12.3.6  先行发生原则

先行发生（happens-before）原则，是判断数据是否存在竞争、线程是否安全的主要依据。

“先行发生”是 JMM 中定义的两项操作之间的偏序关系，若操作 A “先行发生”于操作 B，意味着操作 B 发生之前，操作 A 产生的影响能被操作 B 观察到。“影响”包括修改了内存中共享变量的值、发送了消息、调用了方法等。

#### JMM 中的一些先行发生关系

- 程序次序规则（Program Order Rule）

  线程内按照程序代码顺序，在前面的操作先行发生于后面的操作（准确说应该是控制流顺序）。

- 管程锁定规则（Monitor Lock Rule）

  一个 unlock 操作先行发生于后面对同一个锁的 lock 操作。

- volatile 变量规则（Volatile Variable Rule）

  对 volatile 变量的写操作先行发生于后面对这个变量的读操作。

- 线程启动规则（Thread Start Rule）

  Thread 对象的 start() 方法先行发生于此线程的每一个动作。

- 线程终止规则（Thread Termination Rule）

  线程中的所有操作都先行发生于对此线程的终止检测。

- 线程中断规则（Thread Interruption Rule）

  对线程 interrupt() 方法的调用先行发生于被中断线程的代码检测（Thread.interrupt）到中断事件的发生。

- 对象终结规则（Finalizer Rule）

  对象的初始化完成（构造函数执行结束）先行发生于它的 finalize() 方法的开始。

- 传递性（Transitivity）

  若操作 A 先行发生于操作 B，操作 B 先行发生于操作 C，则操作 A 先行发生于操作 C。

#### 先行发生原则举例

代码如下：

```java
private int value = 0;

public void setValue(int value) {
    this.value = value;
}

public int getValue() {
    return value;
}
```

假设存在线程 A 和 B，线程 A 先（时间上的先后）调用了 "setValue(1)"，然后线程 B 调用了同一个对象的 "getValue()"，那么线程 B 的返回值是什么呢？

答案是"不确定"。因为无法从上述"先行发生"原则中的任一条推断出来。

修复方法：

1. getter() / setter() 方法都定义为 synchronized 方法；
2. 把 value 定义为 volatile 变量。

> 结论：时间先后顺序与先行发生原则之间没有必然联系。因此，衡量并发安全问题的时候不要受到时间顺序的干扰，一切必须以先行发生原则为准。

##  12.4  Java 与线程

并发不一定要依赖多线程（例如 PHP 中的多进程并发）。

###  12.4.1  线程的实现

实现线程主要有三种方式：使用内核线程实现、使用用户线程实现和使用用户线程加轻量级进程混合实现。

####  1. 使用内核线程实现

内核线程（Kernel-Level Thread，KLT）就是直接由操作系统内核（Kernel）支持的线程，这种线程由内核来完成线程切换，内核通过操作调度器（Scheduler）对线程进行调度，并负责将线程的任务映射到各个处理器上。

支持多线程的内核叫做多线程内核（Multi-Threads Kernel）。

程序一般不会直接使用内核线程，而是去使用内核线程的一种高级接口——轻量级进程（Light Weight Process，LWP），轻量级进程就是我们通常意义上的线程。它们之间的对应关系如图所示：

![thread](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/LWP_KLT.png)

#### 轻量级进程优缺点

- 优点
  - 某一个阻塞时，不会影响整个进程继续工作
- 缺点
  - 各种线程操作（创建、同步等）需要系统调用，代价相对较高（需在 User Mode 和 Kernel Model 之间来回切换）；
  - LWP 要消耗一定的内核资源，因此一个系统支持轻量级进程的数量是有限的。

####  2. 使用用户线程

用户线程（User Thread，UT），使用 UT 实现的程序一般都比较复杂，现在越来越少了。

####  3. 使用用户线程加轻量级进程混合实现

UT 和 LWP 的数量比不确定，即 N:M 的关系。许多 Unix 系列操作系统提供该线程模型实现。

####  4. Java 线程的实现

目前的 JDK 版本中，操作系统支持怎样的线程模型，很大程度上决定了 JVM 的线程是怎样映射的，不同的平台无法达成一致。

###  12.4.2  Java 线程调度

线程调度是指系统为线程分配处理器使用权的过程，主要调度方式有两种：协同式线程调度（Cooperative Threads-Scheduling）和抢占式线程调度（Preemptive Threads-Scheduling）。

#### 协同式线程调度

线程的执行时间由线程本身控制，线程执行完自己的工作，要主动通知系统切换到另一个线程。

- 好处：实现简单，无线程同步问题
- 坏处：线程执行时间不可控制

#### 抢占式线程调度

线程由系统分配执行时间，线程的切换不由本身决定。

Java 使用的就是抢占式线程调度。

###  12.4.3  状态转换

Java 定义了 5 种线程状态，任意时间点，一个线程有且仅有其中一个状态。

#### 新建（New）

创建后尚未启动。

#### 运行（Runnable）

包括操作系统线程状态中的 Running 和 Ready。此时线程可能在执行，也可能等待 CPU 分配时间。

#### 无限期等待（Waiting）

处于该状态的线程不会被分配 CPU 时间，需等待被其他线程显式唤醒。以下方法会让线程进入该状态：

1. Object.wait();
2. Thread.join();
3. LockSupport.park();

#### 限期等待（Timed Waiting）

处于该状态的线程不会被分配 CPU 时间，但无须其他线程显式唤醒，一段时间后由系统自动唤醒。以下方法会让线程进入该状态：

1. Thread.sleep(Timeout);
2. Object.wait(Timeout);
3. Thread.join(Timeout);
4. LockSupport.parkNanos();
5. LockSupport.parkUntil();

#### 阻塞（Blocking）

"阻塞" 与 "等待" 的区别：

1. "阻塞" 在等待获取一个排它锁，另一个线程放弃该锁的时候发生；

2. "等待" 则是等待一段时间，或者等待唤醒动作的发生。

#### 结束（Terminated）

线程结束执行。



线程状态之间的转换关系图：

![thread](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/thread_status.png)