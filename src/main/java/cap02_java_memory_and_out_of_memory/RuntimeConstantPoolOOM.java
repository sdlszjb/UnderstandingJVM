package cap02_java_memory_and_out_of_memory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 庄壮壮
 * @since 2018-06-02 19:06
 */
public class RuntimeConstantPoolOOM {

    /**
     * 方法区和运行时常量池溢出
     * VM Args: -XX:PermSize=10M -XX:MaxPermSize=10M
     * @param args
     */
    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        int i=0;
        while (true) {
            list.add(String.valueOf(i++).intern());
        }
    }
}
