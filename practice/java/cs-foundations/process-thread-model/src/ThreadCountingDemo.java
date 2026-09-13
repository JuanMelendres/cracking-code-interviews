import java.util.concurrent.CountDownLatch;

/**
 * Measures how many real, OS-level threads exist under this JVM process while
 * N Java threads are alive and blocked -- run once with "platform" and once
 * with "virtual" as the argument. The OS thread count is read directly from
 * the operating system (macOS `top -stats th`), not from any JVM API, so this
 * is real evidence of how each Thread flavor actually maps onto the OS below
 * Java's own Thread abstraction, not a claim taken from documentation.
 */
public class ThreadCountingDemo {

    public static void main(String[] args) throws Exception {
        boolean virtual = args.length > 0 && args[0].equals("virtual");
        int n = 200;
        long pid = ProcessHandle.current().pid();

        System.out.println("Mode: " + (virtual ? "virtual" : "platform") + " threads, n=" + n + ", pid=" + pid);
        printOsThreadCount(pid, "before spawning any of the " + n + " threads");

        CountDownLatch ready = new CountDownLatch(n);
        CountDownLatch release = new CountDownLatch(1);

        for (int i = 0; i < n; i++) {
            Runnable task = () -> {
                ready.countDown();
                try {
                    release.await();
                } catch (InterruptedException ignored) {
                }
            };
            if (virtual) {
                Thread.ofVirtual().start(task);
            } else {
                Thread.ofPlatform().start(task);
            }
        }

        ready.await();
        Thread.sleep(500); // let the OS scheduler settle before sampling
        printOsThreadCount(pid, "with all " + n + " " + (virtual ? "virtual" : "platform") + " threads alive and blocked");
        release.countDown();
    }

    private static void printOsThreadCount(long pid, String label) throws Exception {
        Process p = new ProcessBuilder("top", "-l", "1", "-pid", String.valueOf(pid), "-stats", "th").start();
        String output = new String(p.getInputStream().readAllBytes());
        p.waitFor();
        String lastLine = output.lines().reduce((first, second) -> second).orElse("?");
        System.out.println("  OS-level thread count (" + label + "): " + lastLine.trim());
    }
}
