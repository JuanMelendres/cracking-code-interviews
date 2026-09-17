import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Two real, separate proofs:
 *  1. A naive Comparator.comparing() on a nullable field throws a real NPE;
 *     Comparator.nullsFirst()/nullsLast() fixes it without touching the field.
 *  2. List.sort() (TimSort) is a real, verified STABLE sort: elements that
 *     compare equal keep their original relative order.
 */
public class NullsAndStableSortDemo {

    record Employee(String name, String department) {}

    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>(List.of(
                new Employee("Kim", "Engineering"),
                new Employee("Osei", null),
                new Employee("Patel", "Sales"),
                new Employee("Reyes", null),
                new Employee("Tanaka", "Engineering")
        ));

        System.out.println("== Attempting Comparator.comparing(Employee::department) on data containing a null ==");
        try {
            List<Employee> attempt = new ArrayList<>(employees);
            attempt.sort(Comparator.comparing(Employee::department));
            System.out.println("Sorted without error (unexpected): " + attempt);
        } catch (NullPointerException e) {
            System.out.println("Real NullPointerException thrown: " + e.getMessage());
        }

        List<Employee> fixedNullsFirst = new ArrayList<>(employees);
        fixedNullsFirst.sort(Comparator.comparing(Employee::department, Comparator.nullsFirst(Comparator.naturalOrder())));

        System.out.println();
        System.out.println("== Fixed with Comparator.nullsFirst(Comparator.naturalOrder()) ==");
        fixedNullsFirst.forEach(e -> System.out.println(e.name() + " -> " + e.department()));

        List<Employee> fixedNullsLast = new ArrayList<>(employees);
        fixedNullsLast.sort(Comparator.comparing(Employee::department, Comparator.nullsLast(Comparator.naturalOrder())));

        System.out.println();
        System.out.println("== Same data with Comparator.nullsLast(Comparator.naturalOrder()) instead ==");
        fixedNullsLast.forEach(e -> System.out.println(e.name() + " -> " + e.department()));

        System.out.println();
        System.out.println("== Stability proof: tagged elements with an EQUAL sort key ==");

        record Tagged(int sortKey, int originalIndex) {}

        List<Tagged> tagged = new ArrayList<>();
        int[] keysInInsertionOrder = {5, 3, 5, 1, 3, 5};
        for (int i = 0; i < keysInInsertionOrder.length; i++) {
            tagged.add(new Tagged(keysInInsertionOrder[i], i));
        }

        System.out.println("Original insertion order (sortKey, originalIndex): " + tagged);

        tagged.sort(Comparator.comparingInt(Tagged::sortKey));

        System.out.println("After sorting by sortKey ONLY: " + tagged);

        boolean stable = true;
        for (int i = 1; i < tagged.size(); i++) {
            Tagged prev = tagged.get(i - 1);
            Tagged curr = tagged.get(i);
            if (prev.sortKey() == curr.sortKey() && prev.originalIndex() > curr.originalIndex()) {
                stable = false;
                break;
            }
        }
        System.out.println("Elements with equal sortKey kept their original relative order? " + stable);
    }
}
