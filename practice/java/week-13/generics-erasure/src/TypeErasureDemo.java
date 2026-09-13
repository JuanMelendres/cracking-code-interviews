import java.util.ArrayList;
import java.util.List;

public class TypeErasureDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("== Generic type information does not exist at runtime ==");
        List<String> strings = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();
        System.out.println("strings.getClass() = " + strings.getClass());
        System.out.println("integers.getClass() = " + integers.getClass());
        System.out.println("strings.getClass() == integers.getClass(): "
                + (strings.getClass() == integers.getClass())
                + "  (both are just raw java.util.ArrayList at runtime -- <String> and <Integer> are erased)");

        System.out.println();
        System.out.println("== Erasure means you can defeat generics via reflection, unsafely ==");
        @SuppressWarnings("unchecked")
        List<Object> unsafeView = (List<Object>) (List<?>) strings;
        unsafeView.add(42); // compiles because of the raw/unchecked cast; heap pollution
        try {
            String first = strings.get(0); // ClassCastException happens HERE, not at insertion
            System.out.println("no exception (unexpected): " + first);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException at READ time, not insert time: " + e.getMessage());
            System.out.println("(the Integer 42 was inserted successfully -- generics are a compile-time-only "
                    + "check; the failure surfaces later, at the point strings.get(0) tries an implicit cast to String)");
        }

        System.out.println();
        System.out.println("== A bridge method: proof the compiler generates an extra method for erasure's sake ==");
        for (var m : Wrapper.class.getDeclaredMethods()) {
            System.out.println(m.isBridge() ? "BRIDGE METHOD: " + m : "real method:   " + m);
        }
    }

    interface Box<T> {
        void set(T value);
    }

    // Because Box<T>.set(T) erases to set(Object), the compiler generates a
    // synthetic bridge method set(Object) on Wrapper that casts and delegates
    // to the real set(String) -- visible via reflection as isBridge()==true.
    static class Wrapper implements Box<String> {
        @Override
        public void set(String value) { }
    }
}
