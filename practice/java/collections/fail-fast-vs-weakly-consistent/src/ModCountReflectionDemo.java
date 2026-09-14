import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Real, reflective proof of the actual mechanism behind fail-fast:
 * AbstractList.modCount is a real protected int field, incremented on every
 * structural modification. An Iterator captures its "expectedModCount" at
 * creation and compares it against the live modCount on every next()/
 * remove() call -- a mismatch is exactly what throws
 * ConcurrentModificationException.
 */
public class ModCountReflectionDemo {

    public static void main(String[] args) throws Exception {
        Field modCountField = AbstractList.class.getDeclaredField("modCount");
        modCountField.setAccessible(true);

        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        System.out.println("modCount after construction: " + modCountField.getInt(list));

        list.add(4);
        System.out.println("modCount after add(4):       " + modCountField.getInt(list));

        list.remove(Integer.valueOf(2));
        System.out.println("modCount after remove(2):    " + modCountField.getInt(list));

        list.set(0, 99); // NOT structural -- replaces a value in place
        System.out.println("modCount after set(0, 99):   " + modCountField.getInt(list)
                + " -- unchanged: set() is not a structural modification, so iterators remain valid through it");

        list.get(0); // a pure read
        System.out.println("modCount after get(0):       " + modCountField.getInt(list)
                + " -- unchanged: reads never touch modCount");
    }
}
