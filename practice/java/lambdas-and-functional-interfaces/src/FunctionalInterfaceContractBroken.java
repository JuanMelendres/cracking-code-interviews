public class FunctionalInterfaceContractBroken {

    @FunctionalInterface
    interface TwoAbstractMethods {
        void first();
        void second(); // ILLEGAL: a second abstract method violates the SAM contract
    }

    public static void main(String[] args) {
        System.out.println("unreachable if this compiles");
    }
}
