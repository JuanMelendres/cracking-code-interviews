public class ContainerErgonomicsDemo {
    public static void main(String[] args) {
        Runtime rt = Runtime.getRuntime();
        System.out.println("availableProcessors: " + rt.availableProcessors());
        System.out.println("maxMemory (MB): " + rt.maxMemory() / (1024*1024));
    }
}
