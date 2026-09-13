import java.util.function.Supplier;

public class CapturingBroken {
    public static void main(String[] args) {
        int count = 0;
        Supplier<Integer> supplier = () -> {
            count++; // ILLEGAL: captured local is not effectively final
            return count;
        };
        System.out.println(supplier.get());
    }
}
