import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Requires: java --add-opens java.base/java.util=ALL-UNNAMED ArrayListGrowthDemo
 */
public class ArrayListGrowthDemo {
    public static void main(String[] args) throws Exception {
        Field elementDataField = ArrayList.class.getDeclaredField("elementData");
        elementDataField.setAccessible(true);

        ArrayList<Integer> list = new ArrayList<>();
        System.out.println("== ArrayList's backing array grows by roughly 1.5x when full, not by doubling ==");

        int lastCapacity = -1;
        for (int i = 0; i < 60; i++) {
            list.add(i);
            Object[] backing = (Object[]) elementDataField.get(list);
            if (backing.length != lastCapacity) {
                System.out.println("size=" + list.size() + " triggers growth -> new capacity=" + backing.length
                        + (lastCapacity > 0 ? "  (grew from " + lastCapacity + ", ratio=" + String.format("%.2f", (double) backing.length / lastCapacity) + ")" : "  (initial allocation on first add())"));
                lastCapacity = backing.length;
            }
        }
    }
}
