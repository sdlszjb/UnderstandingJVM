# Java内存区域与内存溢出异常

## 概述

## 运行时数据区域
- 程序记数器(Program Counter Register)
    > 线程私有。可以把它看作是当前线程所执行的字节码的等号指示器。类似SP
- Java虚拟机栈(Java Virtual Machine Stacks)
    > 线程私有。描述的是**Java方法执行**的内存模型：每个方法在执行的同时都会创建一个栈帧（Stack Frame）用于存储局部变量表、操作数栈、动态链接、方法出口等信息。
- 本地方法栈(Native Method Stack)
    > 本地方法栈为虚拟机使用到的Native服务(HotSpot虚拟机中，将它与Java虚拟机栈合二为一)。
- Java堆(Java Heap)
    > （所有线程共享）Java堆是所有线程共享的一块区域，在虚拟机启动时创建。用来存放对象实例。是垃圾收集器管理的主要区域。
- 方法区(Method Area)
    > （所有线程共享）用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。（类似DATA区）
- 运行时常量池(Runtime Constant Pool)
    > 是方法区的一部分。Class文件中除了有类的版本、字段、方法、接口等描述信息外，还有一项信息是常量池(Constant Pool Table)，用于存放编译期生成的各种字面量和符号引用，这部分内容将在类加载后进入方法区的运行时常量池中存放。
- 直接内存(Direct Memory)
    > 基于通道（Channel）与缓冲区（Buffer）的I/O方式，可以使用Native函数直接分配堆外内存，然后通过一个存储在Java堆中的DirectByteBuffer对象作为这块内存的引用进行操作。
    
## HotSpot虚拟机对象探秘
1. 对象的创建

类加载 -> 分配内存 -> 内存初始化 -> 执行init方法

2. 对象的内存布局

对象头(Header)、实例数据(Instance Data)、对齐填充(Padding)

3. 对象的访问定位
    - 句柄
    - 直接指针

## 实战：OutOfMemoryError异常
1. Java堆溢出

code:
```java
/**
 * @author 庄壮壮
 * @since 2018-06-02 18:24
 */
public class ClientTest {

    private static class OOMObject {
    }

    /**
     * 测试Java堆溢出
     * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
     */
    @Test
    public void headOOM() {
        List<OOMObject> objects = new ArrayList<>();
        while (true) {
            objects.add(new OOMObject());
        }
    }
}
```
output:
```text
F:\DeveloperTools\Sdk\java10.0.1\bin\java.exe -ea -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -Didea.test.cyclic.buffer.size=1048576 -javaagent:S:\DeveloperTools\Intellij\ideaIU-2018.1.4\lib\idea_rt.jar=1391:S:\DeveloperTools\Intellij\ideaIU-2018.1.4\bin -Dfile.encoding=UTF-8 -classpath S:\DeveloperTools\Intellij\ideaIU-2018.1.4\lib\idea_rt.jar;S:\DeveloperTools\Intellij\ideaIU-2018.1.4\plugins\junit\lib\junit-rt.jar;S:\DeveloperTools\Intellij\ideaIU-2018.1.4\plugins\junit\lib\junit5-rt.jar;C:\Users\lsk\IdeaProjects\producting\UnderstandingJVM\target\test-classes;C:\Users\lsk\IdeaProjects\producting\UnderstandingJVM\target\classes;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib-jdk8\1.2.31\kotlin-stdlib-jdk8-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib\1.2.31\kotlin-stdlib-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\annotations\13.0\annotations-13.0.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib-jdk7\1.2.31\kotlin-stdlib-jdk7-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-test-junit\1.2.31\kotlin-test-junit-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-test\1.2.31\kotlin-test-1.2.31.jar;C:\Users\lsk\.m2\repository\junit\junit\4.12\junit-4.12.jar;C:\Users\lsk\.m2\repository\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 -junit4 cap02_java_memory_and_out_of_memory.ClientTest,headOOM
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid8880.hprof ...
Heap dump file created [30899517 bytes in 0.087 secs]

java.lang.OutOfMemoryError: Java heap space

	at java.base/java.util.Arrays.copyOf(Arrays.java:3719)
	at java.base/java.util.Arrays.copyOf(Arrays.java:3688)
	at java.base/java.util.ArrayList.grow(ArrayList.java:237)
	at java.base/java.util.ArrayList.grow(ArrayList.java:242)
	at java.base/java.util.ArrayList.add(ArrayList.java:467)
	at java.base/java.util.ArrayList.add(ArrayList.java:480)
	at cap02_java_memory_and_out_of_memory.ClientTest.headOOM(ClientTest.java:27)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:564)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:47)
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)

Process finished with exit code -1
```

问题解决：
    - 如果是泄露，找到泄露代码，并解决
    - 如果内存中的对象确实都必须存活，设置-Xmx -Xms参数，或者增大机器物理内存，或者代码优化。