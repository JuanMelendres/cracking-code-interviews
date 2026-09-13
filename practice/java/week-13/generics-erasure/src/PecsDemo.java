import java.util.ArrayList;
import java.util.List;

public class PecsDemo {

    // Producer-Extends: a method that only READS from a list of some
    // unknown subtype of Number should accept List<? extends Number>.
    static double sumOf(List<? extends Number> producer) {
        double total = 0;
        for (Number n : producer) {
            total += n.doubleValue();
        }
        return total;
    }

    // Consumer-Super: a method that only WRITES Integers into a list should
    // accept List<? super Integer> -- works for List<Integer>, List<Number>,
    // List<Object>, maximizing what callers can pass.
    static void fillWithSquares(List<? super Integer> consumer, int upTo) {
        for (int i = 1; i <= upTo; i++) {
            consumer.add(i * i);
        }
    }

    public static void main(String[] args) {
        System.out.println("== Producer-extends: sumOf() accepts List<Integer>, List<Double>, List<Number> ==");
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.5, 2.5);
        System.out.println("sumOf(List<Integer>) = " + sumOf(ints));
        System.out.println("sumOf(List<Double>)  = " + sumOf(doubles));

        System.out.println();
        System.out.println("== Consumer-super: fillWithSquares() accepts List<Integer>, List<Number>, List<Object> ==");
        List<Integer> intTarget = new ArrayList<>();
        fillWithSquares(intTarget, 5);
        System.out.println("filled List<Integer>: " + intTarget);

        List<Number> numberTarget = new ArrayList<>();
        fillWithSquares(numberTarget, 5);
        System.out.println("filled List<Number>:  " + numberTarget);

        List<Object> objectTarget = new ArrayList<>();
        fillWithSquares(objectTarget, 5);
        System.out.println("filled List<Object>:  " + objectTarget);

        System.out.println();
        System.out.println("== Violating PECS: a List<? extends Number> cannot be safely written to ==");
        System.out.println("List<? extends Integer> readOnlyView = ints;  // compiles");
        List<? extends Integer> readOnlyView = ints;
        System.out.println("readOnlyView.get(0) = " + readOnlyView.get(0) + "  (reading is safe)");
        System.out.println("readOnlyView.add(99);  // DOES NOT COMPILE -- the compiler cannot prove the runtime");
        System.out.println("  type is exactly Integer (it could be List<? extends Integer> holding some other");
        System.out.println("  Integer subtype in a hypothetical future JDK), so add() is rejected at compile time.");
        System.out.println("  (This line is commented out in the source because it is a compile error, not a");
        System.out.println("  runtime one -- proving the PECS violation is caught before the program ever runs.)");
    }
}
