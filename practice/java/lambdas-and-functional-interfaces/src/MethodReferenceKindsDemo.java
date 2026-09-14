import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/** Real, executed proof of all four method-reference kinds and their lambda equivalents. */
public class MethodReferenceKindsDemo {

    public static void main(String[] args) {
        // 1. Static method reference: ClassName::staticMethod
        Function<String, Integer> staticRef = Integer::parseInt;
        Function<String, Integer> staticLambda = s -> Integer.parseInt(s);
        System.out.println("1. Static:            Integer::parseInt(\"42\") = " + staticRef.apply("42")
                + "  (lambda equivalent: " + staticLambda.apply("42") + ")");

        // 2. Bound instance method reference: particularInstance::instanceMethod
        String greeting = "hello world";
        Supplier<Integer> boundRef = greeting::length;
        Supplier<Integer> boundLambda = () -> greeting.length();
        System.out.println("2. Bound instance:    greeting::length() = " + boundRef.get()
                + "  (lambda equivalent: " + boundLambda.get() + ")");

        // 3. Unbound instance method reference: ClassName::instanceMethod
        //    (the instance becomes the first parameter)
        Function<String, Integer> unboundRef = String::length;
        Function<String, Integer> unboundLambda = s -> s.length();
        System.out.println("3. Unbound instance:  String::length(\"unbound\") = " + unboundRef.apply("unbound")
                + "  (lambda equivalent: " + unboundLambda.apply("unbound") + ")");

        // 4. Constructor reference: ClassName::new
        Function<String, StringBuilder> ctorRef = StringBuilder::new;
        Function<String, StringBuilder> ctorLambda = s -> new StringBuilder(s);
        System.out.println("4. Constructor:       StringBuilder::new(\"built\") = \"" + ctorRef.apply("built")
                + "\"  (lambda equivalent: \"" + ctorLambda.apply("built") + "\")");

        // A BiFunction using an unbound instance reference, for a two-arg case.
        BiFunction<String, String, Boolean> unboundTwoArg = String::equals;
        System.out.println("Unbound, two-arg:     \"a\".equals(\"a\") via String::equals = " + unboundTwoArg.apply("a", "a"));

        List<String> words = List.of("charlie", "alpha", "bravo");
        System.out.println("Sorted via method reference (String::compareTo): "
                + words.stream().sorted(String::compareTo).toList());
    }
}
