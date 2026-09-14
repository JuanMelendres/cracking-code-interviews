public class FunctionalInterfaceContractFixed {

    // Real, compiling proof that default and static methods do NOT count
    // toward the single-abstract-method (SAM) constraint -- only abstract
    // methods do. This interface has ONE abstract method (first()) plus a
    // default method and a static method, and it compiles as a valid
    // functional interface.
    @FunctionalInterface
    interface OneAbstractMethodPlusExtras {
        void first();

        default void second() {
            System.out.println("default method -- does not count toward SAM");
        }

        static void third() {
            System.out.println("static method -- does not count toward SAM either");
        }
    }

    public static void main(String[] args) {
        OneAbstractMethodPlusExtras impl = () -> System.out.println("lambda satisfies the single abstract method");
        impl.first();
        impl.second();
        OneAbstractMethodPlusExtras.third();
    }
}
