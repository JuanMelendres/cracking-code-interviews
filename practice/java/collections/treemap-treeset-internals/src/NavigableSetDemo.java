import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * T-203: real, executed proof of the corrected Set/Map hierarchy the Phase 1
 * audit flagged as inverted in the source material -- NavigableSet/NavigableMap
 * are INTERFACES (extending SortedSet/SortedMap), never peer implementations
 * of TreeSet/TreeMap; TreeSet/TreeMap are the concrete classes that IMPLEMENT
 * them.
 */
public class NavigableSetDemo {

    public static void main(String[] args) {
        // Compiles ONLY because NavigableSet is an interface and TreeSet is a
        // real implementing class -- this line itself is the proof; it would
        // not compile if NavigableSet were (as the audited material implied)
        // a sibling concrete class instead of TreeSet's own supertype.
        NavigableSet<Integer> set = new TreeSet<>(java.util.List.of(10, 20, 30, 40, 50));
        System.out.println("NavigableSet<Integer> set = new TreeSet<>(...) -- compiles: " + set);

        System.out.println();
        System.out.println("== Real NavigableSet methods, real output ==");
        System.out.println("floor(25)    = " + set.floor(25) + "   (greatest element <= 25)");
        System.out.println("ceiling(25)  = " + set.ceiling(25) + "   (smallest element >= 25)");
        System.out.println("lower(30)    = " + set.lower(30) + "   (greatest element < 30, strictly)");
        System.out.println("higher(30)   = " + set.higher(30) + "   (smallest element > 30, strictly)");
        System.out.println("first()      = " + set.first());
        System.out.println("last()       = " + set.last());
        System.out.println("descendingSet() = " + set.descendingSet());
        System.out.println("subSet(20, true, 40, true) = " + set.subSet(20, true, 40, true));

        Integer polled = set.pollFirst();
        System.out.println("pollFirst()  = " + polled + "   set is now: " + set);

        System.out.println();
        System.out.println("== The real interface chain (java.lang.Class.getInterfaces / getSuperclass) ==");
        printHierarchy(TreeSet.class);
        System.out.println();
        printHierarchy(TreeMap.class);

        System.out.println();
        System.out.println("== NavigableMap: the same pattern for Map ==");
        NavigableMap<Integer, String> map = new TreeMap<>();
        map.put(10, "ten");
        map.put(30, "thirty");
        map.put(50, "fifty");
        System.out.println("floorEntry(25)   = " + map.floorEntry(25));
        System.out.println("ceilingEntry(25) = " + map.ceilingEntry(25));
        System.out.println("firstKey()       = " + map.firstKey());
        System.out.println("descendingMap()  = " + map.descendingMap());
    }

    private static void printHierarchy(Class<?> cls) {
        System.out.println(cls.getSimpleName() + " implements/extends:");
        Class<?> superclass = cls.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            System.out.println("  extends " + superclass.getSimpleName());
        }
        for (Class<?> iface : cls.getInterfaces()) {
            System.out.println("  implements " + iface.getSimpleName());
        }
    }
}
