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
    > （所有线程共享）Java堆是所有线程共享的一块区域，在虚拟机启动时创建。用来存放对象实例。是垃圾收集器管理的主要区域。(类似CODE区)
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
### Java堆溢出

- code:
```java
/**
 * @author 庄壮壮
 * @since 2018-06-02 18:24
 */
public class HeapOOM {


    private static class OOMObject {
    }

    /**
     * 测试Java堆溢出
     * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
     */
    public static void main(String[] args) {
        List<OOMObject> objects = new ArrayList<>();
        while (true) {
            objects.add(new OOMObject());
        }
    }
}
```
- output:
```text
F:\DeveloperTools\Sdk\java10.0.1\bin\java.exe -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -javaagent:S:\DeveloperTools\Intellij\ideaIU-2018.1.4\lib\idea_rt.jar=3547:S:\DeveloperTools\Intellij\ideaIU-2018.1.4\bin -Dfile.encoding=UTF-8 -classpath C:\Users\lsk\IdeaProjects\producting\UnderstandingJVM\target\classes;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib-jdk8\1.2.31\kotlin-stdlib-jdk8-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib\1.2.31\kotlin-stdlib-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\annotations\13.0\annotations-13.0.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib-jdk7\1.2.31\kotlin-stdlib-jdk7-1.2.31.jar cap02_java_memory_and_out_of_memory.HeapOOM
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid12368.hprof ...
Heap dump file created [29562804 bytes in 0.080 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at java.base/java.util.Arrays.copyOf(Arrays.java:3719)
	at java.base/java.util.Arrays.copyOf(Arrays.java:3688)
	at java.base/java.util.ArrayList.grow(ArrayList.java:237)
	at java.base/java.util.ArrayList.grow(ArrayList.java:242)
	at java.base/java.util.ArrayList.add(ArrayList.java:467)
	at java.base/java.util.ArrayList.add(ArrayList.java:480)
	at cap02_java_memory_and_out_of_memory.HeapOOM.main(HeapOOM.java:23)

Process finished with exit code 1
```
- 问题解决：
    - 如果是泄露，找到泄露代码，并解决
    - 如果内存中的对象确实都必须存活，设置-Xmx -Xms参数，或者增大机器物理内存，或者代码优化。
    
### 虚拟机栈和本地方法栈溢出
由于在HotSpot中，并不区分虚拟机栈和本地方法栈，因此对于HotSpot来说，虽然-Xoss参数存在，但实际上是无效的，栈容量只由-Xss参数设定。
- 两种异常：
    - 线程请求的栈深度大于虚拟机所允许的最大深度，将抛出StackOverflowError
    - 如果虚拟机在扩展栈时无法申请到足够的内存空间，则抛出OutOfMemoryError
- code
```java

/**
 * @author 庄壮壮
 * @since 2018-06-02 18:54
 */
public class JavaVMStackSOF {

    private int stackLength = 1;

    /**
     * 虚拟机栈和本地方法栈OOM测试
     * VM Args: -Xss10m
     * @param args
     */
    public static void main(String[] args) {
        JavaVMStackSOF oom = new JavaVMStackSOF();
        try {
            oom.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length: " + oom.stackLength);
            throw e;
        }
    }

    private void stackLeak() {
        stackLength++;
        stackLeak();
    }
}
```
- output
```text
F:\DeveloperTools\Sdk\java10.0.1\bin\java.exe -Xss10m -javaagent:S:\DeveloperTools\Intellij\ideaIU-2018.1.4\lib\idea_rt.jar=3605:S:\DeveloperTools\Intellij\ideaIU-2018.1.4\bin -Dfile.encoding=UTF-8 -classpath C:\Users\lsk\IdeaProjects\producting\UnderstandingJVM\target\classes;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib-jdk8\1.2.31\kotlin-stdlib-jdk8-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib\1.2.31\kotlin-stdlib-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\annotations\13.0\annotations-13.0.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib-jdk7\1.2.31\kotlin-stdlib-jdk7-1.2.31.jar cap02_java_memory_and_out_of_memory.JavaVMStackSOF
stack length: 214509
Exception in thread "main" java.lang.StackOverflowError
	at cap02_java_memory_and_out_of_memory.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:28)
	at cap02_java_memory_and_out_of_memory.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:28)
...
```
- 如果多线程开发时候，-Xss设置过大，也会出现StackOverflowError异常。原因解释：为每个线程分配的-Xss过大，导致在内存一定的前提下，可以建立的线程数过少，新线程无法建立，导致SOF错误。
    - 解决办法：通过减小Xss的参数数值。

### 方法区和运行时常量池溢出(HotSpot64-Bit 8.0已经Removed)
- 参数：-XX:PermSize和-XX:MaxPermSize
- code
```java

```
- output
```text

```