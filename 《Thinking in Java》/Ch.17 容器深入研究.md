# 第 17 章  容器深入研究


####  填充容器

Collections 方法：

- Collections.nCopies: 可以给集合类赋值；
- Collections.fill: 替换已存在的元素；

##  Set 和存储顺序

| 类（接口）     | 功能                                                         |
| -------------- | ------------------------------------------------------------ |
| Set(interface) | 存入 Set 的每个元素都必须是唯一的，加入 Set 的元素必须定义 equals 方法以确保对象的唯一性，Set 接口不保证维护元素的次序。 |
| HashSet        | 为快速查找设计的 Set，存入元素必须定义 hashCode()。          |
| TreeSet        | 有序的 Set，底层为红黑树，元素必须实现 Comparable 接口。     |
| LinkedHashSet  | 具有 HashSet 的查询速度，且内部使用链表维护元素的顺序（插入的次序），元素也必须定义 hashCode 方法。 |

##  理解 Map

| 类（接口）        | 功能                                                         |
| ----------------- | ------------------------------------------------------------ |
| HashMap           | Map基于散列表的实现（取代了Hashtable）                       |
| LinkedHashMap     | 类似HashMap，取得键值对的顺序是其插入顺序，或者为 LRU 次序，比HashMap略慢，使用链表维护内部顺序 |
| TreeMap           | 基于红黑树的实现，键有序（由Comparable或Comparator决定），有subMap方法，可以返回一个子树 |
| WeakHashMap       | 弱键（weak key）映射，                                       |
| ConcurrentHashMap | 线程安全的Map                                                |
| IdentityHashMap   | 使用 == 替代 equals() 对键进行比较的散列映射，为解决特殊问题而设计 |

##  散列与散列码

- 散列的价值在于速度：散列使得查询得以快速进行。
- 由于散列表中的“槽位”（slot）通常称为桶位（bucket），因此我们将表示实际散列表的数组命名为 bucket。

###  equals 方法

正确的 equals 方法必须满足下列五个条件：

- 自反性

对于任何非空引用值 x，x.equals(x) 应该返回 true。

- 对称性

对于任何非空引用值 x 和 y，当且仅当 y.equals(x) 返回 true 时，x.equals(y) 才返回 true。

- 传递性

对于任何非空引用值 x，y 和 z，如果 x.equals(y) 返回 true，y.equals(z) 返回 true，则 x.equals(z) 应该返回 true。

- 一致性

对于任何非空引用值 x 和 y，如果在对象的 equals 比较中使用的信息没有被修改，则 x.equals(y) 的多个调用始终返回 true 或返回 false。

- 对于任何非 null 的引用值 x，x.equals(null) 应该返回 false。

###  hashCode 方法

hashCode 方法的规定：

- 在 Java 程序执行期间，若对象用于 equals 方法比较时所用的信息没有被修改，则对同一对象多次调用 hashCode 方法的返回值不变；
- 若根据 equals 方法，两个对象相等，那么这两个对象的 hashCode 方法返回值相同；
- 若根据 equals 方法，两个对象不相等，则两个对象的 hashCode 方法可能相等【推荐不相等，可提高哈希表性能】。

##  选择接口的不同实现

Hashtable、Vector 和 Stack 是过去遗留下来的类，目的只是为了支持老的程序（最好不要在新的程序中使用它们）。

###  HashMap 的性能因子

- 容量：表中的桶位数；
- 初始容量：表在创建时所拥有的桶位数。HashMap 和 HashSet 都有允许指定初始容量的构造器；
- 尺寸（size）：表中当前存储的项数；
- 负载因子：尺寸/容量。空表的负载因子为 0，半满表的负载因子为 0.5，以此类推。
  - 负载轻的表产生冲突的可能性小，因此对于插入和查找都是最理想的（但会减慢使用迭代器进行遍历的过程）。HashMap 和 HashSet 都有允许指定负载因子的构造器，表示当负载情况达到该负载因子的水平时，容器将自动增加其容量（桶位数），实现方式是使容量大致加倍，并重新将现有对象分布到新的桶位集中（称为再散列）。
  - HashMap 使用的默认负载因子是 0.75（只有当表达到 3/4 时，才进行再散列），这个因子在时间和空间代价之间达到了平衡：更高的负载因子可以降低表所需的空间，但是会增加查找代价。

##  实用方法

如图所示：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/thinking/Collections.png)

###  快速报错

- Java 容器类类库采用快速报错（fail-fast）机制。它会探查容器上任何除了当前进程所进行的操作以外的所有变化，一旦发现其他进程修改了容器，会抛出 ConcurrentModificationException。

##  持有引用

`java.lang.ref` 类库