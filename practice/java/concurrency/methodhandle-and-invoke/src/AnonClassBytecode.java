import java.util.function.IntUnaryOperator;

public class AnonClassBytecode {
    public static void main(String[] args) {
        IntUnaryOperator doubler = new IntUnaryOperator() {
            @Override
            public int applyAsInt(int x) { return x * 2; }
        };
        System.out.println("doubler.applyAsInt(21) = " + doubler.applyAsInt(21));
    }
}
