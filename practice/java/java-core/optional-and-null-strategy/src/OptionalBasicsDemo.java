import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Real, executed proof of Optional's actual construction and access
 * contracts -- not the informal "it's a null wrapper" summary, but the
 * precise, real exceptions each method throws or doesn't.
 */
public class OptionalBasicsDemo {

    public static void main(String[] args) {
        System.out.println("== Optional.of(null) vs Optional.ofNullable(null) ==");
        try {
            Optional.of(null);
            System.out.println("Optional.of(null): did NOT throw (unexpected)");
        } catch (NullPointerException e) {
            System.out.println("Optional.of(null): threw real NullPointerException, immediately at construction");
        }
        Optional<String> empty = Optional.ofNullable(null);
        System.out.println("Optional.ofNullable(null): " + empty + " (no exception, isPresent=" + empty.isPresent() + ")");

        System.out.println("\n== Optional.get() on an empty Optional ==");
        Optional<String> emptyOpt = Optional.empty();
        try {
            emptyOpt.get();
            System.out.println("get() on empty: did NOT throw (unexpected)");
        } catch (NoSuchElementException e) {
            System.out.println("get() on empty: threw real NoSuchElementException: " + e.getMessage());
        }

        System.out.println("\n== Real, correct alternatives to unchecked get() ==");
        System.out.println("orElse(\"default\"):        " + emptyOpt.orElse("default"));
        System.out.println("orElseGet(() -> \"lazy\"):  " + emptyOpt.orElseGet(() -> "lazy"));
        try {
            emptyOpt.orElseThrow(() -> new IllegalStateException("real, custom exception"));
        } catch (IllegalStateException e) {
            System.out.println("orElseThrow(customSupplier): threw the real, custom exception: " + e.getMessage());
        }

        Optional<String> present = Optional.of("real-value");
        System.out.println("\n== map/flatMap chaining on a present Optional, real output ==");
        Optional<Integer> length = present.map(String::length);
        System.out.println("present.map(String::length) = " + length);
        Optional<Integer> emptyLength = emptyOpt.map(String::length);
        System.out.println("emptyOpt.map(String::length) = " + emptyLength + " (map on empty short-circuits, no NPE)");
    }
}
