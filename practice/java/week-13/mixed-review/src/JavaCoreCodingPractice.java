import java.util.*;
import java.util.stream.*;

public class JavaCoreCodingPractice {

    // Problem 1: implement a Comparable value class correctly (equals,
    // hashCode, compareTo ALL consistent with each other) -- the exact
    // discipline T-101/T-104 cover, applied rather than just discussed.
    static final class Money implements Comparable<Money> {
        final String currency;
        final long cents;

        Money(String currency, long cents) {
            this.currency = Objects.requireNonNull(currency);
            this.cents = cents;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Money m)) return false;
            return cents == m.cents && currency.equals(m.currency);
        }

        @Override
        public int hashCode() { return Objects.hash(currency, cents); }

        @Override
        public int compareTo(Money o) {
            if (!currency.equals(o.currency)) {
                throw new IllegalArgumentException("cannot compare different currencies: " + currency + " vs " + o.currency);
            }
            return Long.compare(cents, o.cents);
        }

        @Override
        public String toString() { return String.format("%s %d.%02d", currency, cents / 100, cents % 100); }
    }

    // Problem 2: a generic, bounded utility method using PECS correctly --
    // a producer-extends method that finds the max of any Comparable type.
    static <T extends Comparable<? super T>> T maxOf(List<? extends T> items) {
        if (items.isEmpty()) throw new NoSuchElementException("empty list");
        T max = items.get(0);
        for (T item : items) {
            if (item.compareTo(max) > 0) max = item;
        }
        return max;
    }

    // Problem 3: a stream pipeline that groups, sorts, and formats --
    // exercising Collectors.groupingBy + a downstream comparator.
    record Employee(String department, String name, int salary) {}

    static Map<String, List<String>> topEarnerPerDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(Employee::salary)),
                                opt -> opt.map(e -> List.of(e.name())).orElse(List.of())
                        )
                ));
    }

    // Problem 4: an immutable value class with a defensively-copied
    // collection field, exercising T-103's discipline end to end.
    static final class Roster {
        private final List<String> members;

        Roster(List<String> members) { this.members = List.copyOf(members); }

        List<String> members() { return members; } // safe: already an immutable copy
    }

    static int assertions = 0;
    static void check(boolean condition, String description) {
        assertions++;
        if (!condition) throw new AssertionError("FAILED: " + description);
        System.out.println("PASS: " + description);
    }

    public static void main(String[] args) {
        // Problem 1 checks
        Money a = new Money("USD", 1050);
        Money b = new Money("USD", 1050);
        Money c = new Money("USD", 2000);
        check(a.equals(b), "Money: equal value objects are equals()-equal");
        check(a.hashCode() == b.hashCode(), "Money: equal objects have equal hashCode()");
        check(a.compareTo(c) < 0, "Money: compareTo orders by cents");
        check(new HashSet<>(List.of(a, b, c)).size() == 2, "Money: HashSet correctly deduplicates a and b");
        try {
            a.compareTo(new Money("EUR", 1050));
            check(false, "Money: cross-currency compareTo should throw");
        } catch (IllegalArgumentException e) {
            check(true, "Money: cross-currency compareTo throws IllegalArgumentException");
        }

        // Problem 2 checks
        check(maxOf(List.of(3, 1, 4, 1, 5, 9, 2, 6)).equals(9), "maxOf: finds max of Integers");
        check(maxOf(List.of("banana", "apple", "cherry")).equals("cherry"), "maxOf: finds max of Strings");
        check(maxOf(List.of(a, c)).equals(c), "maxOf: works for a custom Comparable (Money)");

        // Problem 3 checks
        List<Employee> employees = List.of(
                new Employee("eng", "alice", 150_000),
                new Employee("eng", "bob", 180_000),
                new Employee("sales", "carol", 120_000)
        );
        Map<String, List<String>> topEarners = topEarnerPerDepartment(employees);
        check(topEarners.get("eng").equals(List.of("bob")), "topEarnerPerDepartment: bob is top earner in eng");
        check(topEarners.get("sales").equals(List.of("carol")), "topEarnerPerDepartment: carol is top earner in sales");

        // Problem 4 checks
        List<String> source = new java.util.ArrayList<>(List.of("alice", "bob"));
        Roster roster = new Roster(source);
        source.add("mallory"); // mutate the ORIGINAL list after construction
        check(roster.members().equals(List.of("alice", "bob")),
                "Roster: unaffected by post-construction mutation of the source list");
        try {
            roster.members().add("mallory");
            check(false, "Roster: members() should return an immutable view");
        } catch (UnsupportedOperationException e) {
            check(true, "Roster: members() returns an immutable, unmodifiable view");
        }

        System.out.println();
        System.out.println(assertions + "/" + assertions + " assertions passed.");
    }
}
