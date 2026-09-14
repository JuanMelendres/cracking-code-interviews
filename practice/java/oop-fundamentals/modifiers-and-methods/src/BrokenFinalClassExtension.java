public class BrokenFinalClassExtension {
    static final class Sealed {
        void hello() {
            System.out.println("hello from Sealed");
        }
    }

    static class Attempt extends Sealed { // ERROR: cannot inherit from final Sealed
    }

    public static void main(String[] args) {
        new Attempt().hello();
    }
}
