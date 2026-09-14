import java.util.List;
import java.util.stream.Stream;

public class StreamLazinessDemo {
    public static void main(String[] args) {
        System.out.println("== A stream pipeline does nothing until a terminal operation runs ==");
        Stream<Integer> pipeline = Stream.of(1, 2, 3, 4, 5)
                .peek(n -> System.out.println("peek saw: " + n))
                .filter(n -> n % 2 == 0);
        System.out.println("Pipeline built. No peek output above this line yet.");
        System.out.println("Now calling .count() (a terminal operation):");
        long count = pipeline.count();
        System.out.println("count = " + count);

        System.out.println();
        System.out.println("== Short-circuiting: findFirst() stops pulling elements once satisfied ==");
        List<Integer> source = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var result = source.stream()
                .peek(n -> System.out.println("evaluating: " + n))
                .filter(n -> n % 3 == 0)
                .findFirst();
        System.out.println("findFirst result = " + result.get());
        System.out.println("(peek should only have printed 1, 2, 3 -- not the whole 10-element source)");

        System.out.println();
        System.out.println("== A stream can only be consumed once ==");
        Stream<Integer> onceOnly = Stream.of(1, 2, 3);
        onceOnly.forEach(n -> {});
        try {
            onceOnly.count();
            System.out.println("no exception (unexpected)");
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException on reuse: " + e.getMessage());
        }
    }
}
