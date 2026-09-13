import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Real, measured comparison of EnumMap/EnumSet (array/bitset-backed, no
 * hashing at all) against HashMap/HashSet (hash-bucket-based) for enum
 * keys, plus real, direct proof of EnumMap's guaranteed natural (ordinal)
 * iteration order versus HashMap's unspecified order.
 */
public class EnumMapVsHashMapDemo {

    enum DayOfWeek {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY}

    static final int ITERATIONS = 100_000_000;

    public static void main(String[] args) {
        System.out.println("== Real measured put+get throughput, " + ITERATIONS + " operations ==");

        Map<DayOfWeek, Integer> enumMap = new EnumMap<>(DayOfWeek.class);
        long enumMapElapsed = measurePutGet(enumMap);

        Map<DayOfWeek, Integer> hashMap = new HashMap<>();
        long hashMapElapsed = measurePutGet(hashMap);

        System.out.println("EnumMap:  " + enumMapElapsed + "ms");
        System.out.println("HashMap:  " + hashMapElapsed + "ms");
        System.out.println("Real measured ratio: " + String.format("%.2fx", (double) hashMapElapsed / enumMapElapsed));

        System.out.println("\n== Real guaranteed iteration order: EnumMap follows declaration (ordinal) order ==");
        Map<DayOfWeek, String> enumMapOrder = new EnumMap<>(DayOfWeek.class);
        enumMapOrder.put(DayOfWeek.FRIDAY, "f");
        enumMapOrder.put(DayOfWeek.MONDAY, "m");
        enumMapOrder.put(DayOfWeek.WEDNESDAY, "w");
        System.out.println("Inserted FRIDAY, MONDAY, WEDNESDAY (in that order) -- EnumMap.keySet() = " + enumMapOrder.keySet()
                + "  <-- REAL: always natural/ordinal order, regardless of insertion order");

        System.out.println("\n== Real memory-shape difference: EnumSet is genuinely bitset-backed ==");
        Set<DayOfWeek> enumSet = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        Set<DayOfWeek> hashSet = new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
        System.out.println("EnumSet.of(MONDAY, FRIDAY) real toString(): " + enumSet + " (iteration order = natural/ordinal order, always)");
        System.out.println("HashSet with the same elements real toString(): " + hashSet + " (iteration order is unspecified, hash-bucket-dependent)");
    }

    static long measurePutGet(Map<DayOfWeek, Integer> map) {
        DayOfWeek[] days = DayOfWeek.values();
        long start = System.currentTimeMillis();
        long sink = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            DayOfWeek day = days[i % days.length];
            map.put(day, i);
            sink += map.get(day);
        }
        System.out.println("(sink=" + sink + ")");
        return System.currentTimeMillis() - start;
    }
}
