#  第 1 章  MySQL架构与历史

- MySQL 最重要、最与众不同的特性是它的存储引擎架构，这种架构的设计将查询处理（Query Processing）及其他系统任务（Server Task）和数据的存储/提取相分离。

##  1.1  MySQL 逻辑架构

MySQL 服务器逻辑架构图如下：

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/MySQL/mysql_architecture.png)

![](https://github.com/JiaoXR/Reading-Notes/blob/master/pics/MySQL/mysql_architecture2.png)

- 其中第二层包含了大多数 MySQL 的核心服务功能，包括查询解析、分析、优化、缓存以及所有的内置函数（例如，日期、时间、数字和加密函数），所有跨存储引擎的功能都在这一层实现：存储过程、触发器、视图等。
- 存储引擎负责 MySQL 中数据的存储和提取，每个存储引擎都有它的优势和劣势。不同存储引擎之间也不会相互通信，只是简单地响应上层服务器的请求。

###  1.1.1  连接管理与安全性

- 每个客户端连接都会在服务器进程中拥有一个线程，这个连接的查询只会在这个单独的线程中执行，该线程只能轮流在某个 CPU 中运行。服务器会负责缓存线程，因此不需要为每一个新建的连接创建或销毁线程。

###  1.1.2  优化与执行

- MySQL 会解析查询，并创建内部数据结构（解析树），然后对其进行各种优化（重写查询、决定表的读取顺序，以及选择合适的索引等）。
- 对于 SELECT 语句，解析查询之前，服务器会先检查查询缓存（Query Cache），若有对应的查询，服务器不必再执行查询解析、优化和执行和执行的整个过程，而是直接返回查询缓存中的结果集。

##  1.2  并发控制

###  1.2.1  读写锁

- 共享锁（shared lock）和排它锁（exclusive lock），也叫读锁（read lock）和写锁（write lock）。
- 在实际的数据库系统中，每时每刻都在发生锁定，当某个用户在修改某一部分数据时，MySQL 会通过锁定防止其他用户读取同一数据。

###  1.2.2  锁粒度

- 锁策略：就是在锁的开销和数据的安全性之间寻求平衡。

####  表锁（table lock）

- 表锁是 MySQL 中最基本的锁策略，并且是开销最小的策略。

####  行级锁（row lock）

- 行级锁可以最大程度地支持并发处理（同时也带来了最大的锁开销），InnoDB 以及其他一些存储引擎中实现了行级锁。
- 行级锁只在存储引擎层实现，而 MySQL 服务器层没有实现。

##  1.3  事务

- 事务就是一组原子性的 SQL 查询，或者说一个独立的工作单元。

- 如果数据库引擎能够成功地对数据库应用该组查询的全部语句，那么就执行该组查询；若其中有任何一条语句无法执行，则所有的语句都不会执行。即，事务内的语句，要么全部执行成功，要么全部执行失败。

- ACID：一个运行良好的事务处理系统，必须具备这些标准特征。

  - 原子性（atomicity）

  整个事务中的所有操作，要么全部成功，要么全部失败回滚。

  - 一致性（consistency）

  - 隔离性（isolation）

  通常来说，一个事务所做的修改在最终提交以前，对其他事务是不可见的。

  - 持久性（durability）

  一旦事务提交，则其所做的修改就会永久保存到数据库中。

- 事务处理过程中的额外的安全性，需要数据库系统做更多的额外工作。一个实现了 ACID 的数据库，相比未实现的通常需要更强的 CPU 处理能力、更大的内存的磁盘空间。

###  1.3.1  隔离级别

- SQL 标准中定义了四种隔离级别，每种都规定了一个事务中所做的修改，哪些在事务内和事务间是可见的、哪些是不可见的。

- 四种隔离级别
  - READ UNCOMMITTED（未提交读）
    - 事务中的修改即使没有提交，对其他事务也都是可见的；
    - 事务可读取未提交的数据，称为脏读（Dirty Read）；
    - 很少使用；
  - READ COMMITTED（提交读）
    - 多数数据库系统默认隔离级别（不包括 MySQL）；
    - 又称为不可重读（nonrepeatable read），因为两次执行同样的查询，可能得到不一样的结果；
  - REPEATABLE READ（可重复读）
    - MySQL 默认事务隔离级别；
    - 解决了脏读的问题，保证了在同一个事务中多次读取同样记录的结果是一致的；
    - 无法解决幻读（Phantom Read）问题；
    - 幻读：某个事务在读取某个范围内的记录时，另一个事务又在该范围内插入了新的记录，当之前的事务再次读取该范围的记录时，会产生幻行（Phantom Row）；
    - InnoDB 和 XtraDB 通过多版本并发控制（MVCC，Multiversion Concurrency Control）解决了幻读的问题。
  - SERIALIZABLE（可串行化）
    - 最高的隔离级别；
    - 强制事务串行执行，避免了幻读问题；
    - 每一行都加锁，可能导致大量的超时和锁争用问题；
    - 实际很少用到，只有在非常需要确保数据的一致性且可接受没有并发时，才考虑使用；

| 隔离级别         | 脏读可能性 | 不可重复读可能性 | 幻读可能性 | 加锁读 |
| ---------------- | ---------- | ---------------- | ---------- | ------ |
| READ UNCOMMITTED | Yes        | Yes              | Yes        | No     |
| READ COMMITTED   | No         | Yes              | Yes        | No     |
| REPEATABLE READ  | No         | No               | Yes        | No     |
| SERIALIZABLE     | No         | No               | No         | Yes    |

###  1.3.2  死锁

- 两个或多个事务在同一资源上相互占用，并请求锁定对方占用的资源，从而导致恶性循环的现象。
- InnoDB 目前处理死锁的方法：将持有最少行级排它锁的事务进行回滚（相对简单的死锁回滚算法）。

###  1.3.3  事务日志

- 可以帮助提高事务的效率。

###  1.3.4  MySQL 中的事务

####  自动提交（AUTOCOMMIT）

- MySQL 默认采用自动提交（AUTOCOMMIT）模式。即，若非显式地开启一个事务，则每个查询都被当做一个事务执行提交操作。

```mysql
-- 查看自动提交模式
SHOW VARIABLES LIKE 'AUTOCOMMIT';

-- 启用自动提交模式
SET AUTOCOMMIT = 1;
```

| Variable_name | Value |
| ------------- | ----- |
| autocommit    | ON    |

- 查看&设置隔离级别

```mysql
-- 查看当前会话隔离级别(注意：8.0以后用第三条)
SELECT @@TX_ISOLATION;
SHOW VARIABLES LIKE 'TX_ISOLATION';
SHOW VARIABLES LIKE 'TRANSACTION_ISOLATION';

-- 查看系统隔离级别
SELECT @@GLOBAL.TX_ISOLATION;

-- 设置当前会话隔离级别(可重读)，新的隔离级别会在下一个事务开始时生效
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- 设置系统隔离级别(可重读)
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
```

####  在事务中混合使用存储引擎

- MySQL 服务器层不管理事务，事务是由下层的存储引擎实现的。因此，在同一个事务中，使用多种存储引擎是不可靠的。

####  隐式和显式锁定

- InnoDB 采用两阶段锁定协议（two-phase locking protocol）：在事务执行过程中，随时都可以执行锁定，锁只有在执行 COMMIT 或 ROLLBACK 时才会释放。
- InnoDB 会根据隔离级别在需要的时候自动加锁。
- 特定的语句显式锁定
  - `SELECT ... LOCK IN SHARE MODE`
  - `SELECT ... FOR UPDATE`

##  1.4  多版本并发控制 (MVCC)

- 不同存储引擎的 MVCC 实现不同，典型的有乐观（optimistic）并发控制和悲观（pessimistic）并发控制。
- InnoDB MVCC 实现原理
  - InnoDB 的 MVCC 是通过在每行记录后面保存两个隐藏的列来实现的；
  - 一列保存了行的创建时间，另一列保存过期（或删除）时间；
  - 但实际存储的是系统版本号（system version number），每开始一个新事务，版本号会递增，事务开始时刻的版本号会作为事务的版本号，用来和查询到的每行记录的版本号进行比较；

##  1.5  MySQL 的存储引擎

- 在文件系统中，MySQL 将每个数据库（也可称为 schema）保存为数据目录下的一个子目录。创建表时，MySQL 会在数据库子目录下创建一个和表同名的 `.frm` 文件保存表的定义。
- 查看表的信息：

```mysql
SHOW TABLE STATUS;
```

###  1.5.1  InnoDB 存储引擎

- InnoDB 是 MySQL 的默认事务型引擎，也是最重要、使用最广泛的存储引擎。除非有非常特别的原因需要使用其他的存储引擎，否则应优先考虑 InnoDB 引擎。
- InnoDB 采用 MVCC 来支持高并发，并且实现了四个标准的隔离级别（默认为 REPEATABLE READ），并且通过间隙锁（next-key locking）策略防止出现幻读【间隙锁使得 InnoDB 不仅仅锁定查询涉及的行，还会对索引中的间隙进行锁定，以防止幻影行的插入】。
- InnoDB 表示基于聚簇索引建立的，其结构与 MySQL 的其他存储引擎有很大的不同，聚簇索引对主键查询有很高的性能。

###  1.5.2  MyISAM 存储引擎

- MySQL 5.1 及以前，MyISAM 是默认的存储引擎。
- MyISAM 特点：
  - 全文索引、压缩、空间函数（GIS）等；
  - 不支持事务和行级锁，崩溃后无法安全恢复；

###  1.5.3  MySQL 内建的其他存储引擎

Archive、Blackhole、CSV、Federated、Memory、NDB

###  1.5.4  第三方存储引擎

OLTP 等几十个

###  1.5.5  选择合适的引擎

- 大部分情况下，InnoDB 都是正确的选择，MySQL 5.5 开始将 InnoDB 作为默认的存储引擎。

###  1.5.6  转换表的引擎

- ALTER TABLE

  示例代码：

  ```mysql
  ALTER TABLE my_table ENGINE=InnoDB;
  ```

  - 优点：使用简单；
  - 缺点：耗时。MySQL 会按行将数据从原表复制到一张新的表中，复制期间可能会消耗系统所有的 I/O 能力，同时原表会加上读锁。
  - 如果转换表的存储引擎，将会失去和原引擎相关的所有特性。

- 导出与导入

- 创建与查询（CREATE 和 SELECT）

