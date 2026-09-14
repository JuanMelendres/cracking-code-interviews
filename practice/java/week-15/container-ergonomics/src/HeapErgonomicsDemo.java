public class HeapErgonomicsDemo {
    public static void main(String[] args) {
        long maxHeapBytes = Runtime.getRuntime().maxMemory();
        int cpus = Runtime.getRuntime().availableProcessors();
        System.out.println("Runtime.getRuntime().maxMemory() = " + maxHeapBytes
                + " bytes (" + (maxHeapBytes / (1024 * 1024)) + " MiB)");
        System.out.println("Runtime.getRuntime().availableProcessors() = " + cpus);
    }
}
