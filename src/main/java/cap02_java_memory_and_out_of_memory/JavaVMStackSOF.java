package cap02_java_memory_and_out_of_memory;

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
