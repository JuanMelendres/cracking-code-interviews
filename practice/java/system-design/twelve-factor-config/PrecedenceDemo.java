public class PrecedenceDemo {

    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfig.load("config.properties", args);

        System.out.println("=== Real resolved config, with the real source that won for each key ===");
        for (String key : new String[]{"timeout.ms", "max.retries", "service.name"}) {
            System.out.printf("  %-15s = %-25s  (from %s)%n", key, config.get(key), config.sourceOf(key));
        }
    }
}
