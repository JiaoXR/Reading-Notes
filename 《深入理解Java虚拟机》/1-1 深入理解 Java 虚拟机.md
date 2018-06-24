# 第 1 章  走近 Java

###  Java 技术体系

Sun 官方所定义的 Java 技术体系包括：

- Java 程序设计语言
- 各种硬件平台上的 Java 虚拟机
- Class 文件格式
- Java API 类库
- 来自商业机构和开源社区的第三方 Java 类库

> 我们可以把 Java 程序设计语言、Java 虚拟机、Java API 类库这三部分统称为 JDK（Java Development Kit），JDK 是用于支持 Java 程序开发的最小环境。
>
> 另外，可以把 Java API 类库中的 Java SE API 子集和 Java 虚拟机这两部分称为 JRE（Java Runtime Environment），JRE 是支持 Java 程序运行的标准环境。



![](https://github.com/JiaoXR/ReadingNotes/blob/master/pics/JVM/Java_Language.png)

图片来源：https://docs.oracle.com/javase/7/docs/



###  Java 虚拟机发展史

- Sun Classic/Exact VM

“世界上第一款商用 Java 虚拟机”。

- Sun HotSpot VM

Sun JDK 和 OpenJDK 中所带的虚拟机，也是目前使用范围最广的 Java 虚拟机。

- Sun Mobile-Embedded VM/Meta-Circular VM
- BEA JRocket/IBM J9 VM
- Azul VM/BEA Liquid VM

- Apache Harmony/Google Android Dalvik VM
- Microsoft JVM 及其他

我们平时所提及的“高性能 Java 虚拟机”一般是指 HotSpot、JRocket、J9 这类在通用平台上运行的商用虚拟机。



###  展望 Java 技术的未来

- 模块化
- 混合语言
- 多核并行
- 进一步丰富语法
- 64 位虚拟机


