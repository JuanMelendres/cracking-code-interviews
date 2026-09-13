public class BrokenFinalMethodOverride {
    static class Parent {
        final void greet() {
            System.out.println("Parent greeting");
        }
    }

    static class Child extends Parent {
        @Override
        void greet() { // ERROR: greet() in Child cannot override greet() in Parent; overridden method is final
            System.out.println("Child greeting");
        }
    }

    public static void main(String[] args) {
        new Child().greet();
    }
}
