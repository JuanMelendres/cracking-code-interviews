import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Requires: java --add-opens java.base/java.util=ALL-UNNAMED HashMapResizeDemo
 * (reflective access to HashMap's private table/threshold fields is blocked by
 * the module system otherwise -- stated explicitly, not worked around silently).
 */
public class HashMapResizeDemo {
    public static void main(String[] args) throws Exception {
        Field tableField = HashMap.class.getDeclaredField("table");
        tableField.setAccessible(true);
        Field thresholdField = HashMap.class.getDeclaredField("threshold");
        thresholdField.setAccessible(true);

        HashMap<Integer, String> map = new HashMap<>();

        System.out.println("== Lazy initialization: the backing array doesn't exist until the first put ==");
        System.out.println("table before any put(): " + tableField.get(map));

        map.put(0, "v0");
        Object[] table = (Object[]) tableField.get(map);
        System.out.println("table after first put(): length=" + table.length
                + ", threshold=" + thresholdField.get(map)
                + "  (default capacity 16, default load factor 0.75 -> threshold = 16*0.75 = 12)");

        System.out.println();
        System.out.println("== Resize doubles capacity once size exceeds threshold ==");
        for (int i = 1; i <= 11; i++) {
            map.put(i, "v" + i);
        }
        table = (Object[]) tableField.get(map);
        System.out.println("after 12 entries total (size=" + map.size() + "): table length=" + table.length
                + ", threshold=" + thresholdField.get(map) + "  (still capacity 16 -- size == threshold, not yet exceeded)");

        map.put(12, "v12");
        table = (Object[]) tableField.get(map);
        System.out.println("after 13th entry (size=" + map.size() + "): table length=" + table.length
                + ", threshold=" + thresholdField.get(map)
                + "  (RESIZED: capacity doubled 16->32 the instant size exceeded the old threshold of 12)");
    }
}
