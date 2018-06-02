package cap02_java_memory_and_out_of_memory;



import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author 庄壮壮
 * @since 2018-06-02 21:00
 */
public class DirectMemoryOOM {

    private static final int _MB = 1024 * 1024;

    /**
     * 使用unsafe分配本机内存
     * VM Args: -Xmx20M -XX:MaxDirectMemorySize=10M
     *
     * @param args
     * @throws IllegalAccessException
     */
    public static void main(String[] args) throws IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);

        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true) {
            unsafe.allocateMemory(_MB);
        }
    }
}
