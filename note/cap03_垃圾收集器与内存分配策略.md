# 垃圾收集器与内存分配策略
## 概述
## 对象已死吗
- 引用计数算法
- 可达性分析算法

### 引用
- 强引用
- 软引用
- 弱引用
- 虚引用

### 对象的自我拯救
- code
```java
/**
 * @author 庄壮壮
 * @since 2018-06-03 18:43
 *
 * 此代码演示了两点：
 * 1. 对象可以在被GC时自我拯救
 * 2. 这种自救的机会只有一次，因为一个对象的finalize()方法最多只会被系统自动调用一次。
 *
 * finalize()方法在Java9已经过期了
 */
public class FinalizeEscapeGC {

    public static FinalizeEscapeGC SAVE_HOOK = null;

    public void isAlive() {
        System.out.println("Yes, i am still alive :)");
    }

    @Override
    @Deprecated(since="9")
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize method executed!");
        FinalizeEscapeGC.SAVE_HOOK = this;
    }

    public static void main(String[] args) throws InterruptedException {
        SAVE_HOOK = new FinalizeEscapeGC();

        // 对象第一次拯救自己
        SAVE_HOOK = null;
        System.gc();

        // 因为finalize方法优先级很低，所以暂停0.5秒以等待它
        Thread.sleep(500);

        if (SAVE_HOOK != null) {
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("No, i am dead :(");
        }

        // 下面这段代码与上面的完全相同，但是这次自救却失败了
        SAVE_HOOK = null;
        System.gc();

        // 因为finalize方法优先级很低，所以暂停0.5秒以等待它
        Thread.sleep(500);

        if (SAVE_HOOK != null) {
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("No, i am dead :(");
        }
    }
}
```

- output
```text
F:\DeveloperTools\Sdk\java10.0.1\bin\java.exe -javaagent:S:\DeveloperTools\Intellij\ideaIU-2018.1.4\lib\idea_rt.jar=14742:S:\DeveloperTools\Intellij\ideaIU-2018.1.4\bin -Dfile.encoding=UTF-8 -classpath C:\Users\lsk\IdeaProjects\producting\UnderstandingJVM\target\classes;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib-jdk8\1.2.31\kotlin-stdlib-jdk8-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib\1.2.31\kotlin-stdlib-1.2.31.jar;C:\Users\lsk\.m2\repository\org\jetbrains\annotations\13.0\annotations-13.0.jar;C:\Users\lsk\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib-jdk7\1.2.31\kotlin-stdlib-jdk7-1.2.31.jar cap03_garbage_collection_and_memory_stratergy.FinalizeEscapeGC
finalize method executed!
Yes, i am still alive :)
No, i am dead :(

Process finished with exit code 0
```

- 注意：在Java的开发中，应当尽量避免使用finalize()方法，使用try-finally方法可能会更好一些。

### 回收方法区
- 回收常量：当前系统没有任何一个String的对象叫做“abc”，也没有其他地方引用了这个字面量，如果这时发生内存回收，而且有必要的话，这个“abc”常量就会被系统清理出常量池。
- 回收类：
    - 该类所有的实例都已经被回收
    - 加载类的ClassLoader已经被回收
    - 该类对应的java.lang.Class对象没有在任何地方被引用，无法在任何地方通过反射访问该类的方法。
    > HotSpot虚拟机提供了-Xnoclassgc参数进行控制，还可以使用-verbose:class、-XX:+TraceClassLoading、-XX:+TraceClassUnLoading查看类加载和卸载信息。在大量使用反射、动态代理、CGLib等ByteCode框架、动态生成JSP以及OSGi这类频繁自定义ClassLoader的场景都需要虚拟机具备类卸载的功能，以保证永久代不会溢出。