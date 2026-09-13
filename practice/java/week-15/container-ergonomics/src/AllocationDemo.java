import java.util.ArrayList;
import java.util.List;

public class AllocationDemo {
    public static void main(String[] args) throws Exception {
        List<byte[]> retained = new ArrayList<>();
        long totalMb = 0;
        System.out.println("maxMemory() = " + (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " MiB");
        System.out.println("Allocating 5 MiB chunks and retaining them, until something stops us...");
        while (true) {
            retained.add(new byte[5 * 1024 * 1024]);
            totalMb += 5;
            if (totalMb % 20 == 0) {
                System.out.println("retained so far: " + totalMb + " MiB");
                System.out.flush();
            }
        }
    }
}
