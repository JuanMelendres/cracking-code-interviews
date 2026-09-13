import java.util.function.IntUnaryOperator;

public class LambdaBytecode {
    public static void main(String[] args) {
        IntUnaryOperator doubler = x -> x * 2;
        System.out.println("doubler.applyAsInt(21) = " + doubler.applyAsInt(21));
    }
}
