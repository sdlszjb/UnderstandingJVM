package cap02_java_memory_and_out_of_memory;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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