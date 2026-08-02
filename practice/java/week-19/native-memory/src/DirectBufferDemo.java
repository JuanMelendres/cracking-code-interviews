import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

// Real demo: direct (off-heap) ByteBuffers are not counted against -Xmx at
// all -- they're governed by a separate limit, -XX:MaxDirectMemorySize.
// This program allocates far more direct memory than the tiny heap allows,
// proving the two are genuinely separate budgets, then keeps allocating
// until it hits the SEPARATE direct-memory limit and gets a real, distinct
// OutOfMemoryError.
public class DirectBufferDemo {
    public static void main(String[] args) {
        List<ByteBuffer> buffers = new ArrayList<>();
        long totalMB = 0;
        int chunkMB = 8;
        try {
            while (true) {
                buffers.add(ByteBuffer.allocateDirect(chunkMB * 1024 * 1024));
                totalMB += chunkMB;
                if (totalMB % 32 == 0) {
                    System.out.println("allocated " + totalMB + "MB of DIRECT memory so far ("
                            + "heap -Xmx is only 32MB, so this is already impossible on-heap)");
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println();
            System.out.println("CAUGHT OutOfMemoryError after allocating ~" + totalMB + "MB direct memory");
            System.out.println("message: " + e.getMessage());
        }
    }
}
