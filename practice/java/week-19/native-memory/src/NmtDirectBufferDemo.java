import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class NmtDirectBufferDemo {
    public static void main(String[] args) throws Exception {
        List<ByteBuffer> buffers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            buffers.add(ByteBuffer.allocateDirect(10 * 1024 * 1024)); // 10MB each, 100MB total
        }
        System.out.println("PID=" + ProcessHandle.current().pid() + " allocated 100MB direct, sleeping...");
        Thread.sleep(5000);
        System.out.println(buffers.size());
    }
}
