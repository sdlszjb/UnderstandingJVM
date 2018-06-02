package cap02_java_memory_and_out_of_memory;

import java.util.ArrayList;
import java.util.List;

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
