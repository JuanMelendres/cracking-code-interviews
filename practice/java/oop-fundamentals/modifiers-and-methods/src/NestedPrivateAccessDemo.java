public class NestedPrivateAccessDemo {

    static class Account {
        private double balance = 100.0;
    }

    public static void main(String[] args) {
        // A real, genuine, and somewhat surprising JLS rule: `private` access is scoped
        // to the TOP-LEVEL enclosing class, not to the individual class body — so this
        // sibling nested class CAN read Account's private field directly. This is not a
        // bug or a compiler quirk; it compiles and runs cleanly, confirmed below.
        Account acc = new Account();
        System.out.println("Account.balance accessed from a sibling nested class: " + acc.balance);
        System.out.println("This compiles and runs cleanly — private is scoped to the top-level class,");
        System.out.println("not the immediate class body. Compare against BrokenPrivateAccess.java,");
        System.out.println("where the SAME field access from a genuinely different top-level class");
        System.out.println("fails to compile.");
    }
}
