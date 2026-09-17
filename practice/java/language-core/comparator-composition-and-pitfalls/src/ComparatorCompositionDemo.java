import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Real, executed proof that Comparator.comparing().thenComparing() composes
 * multi-field sorts declaratively, and that .reversed() flips a composed
 * chain without rewriting it.
 */
public class ComparatorCompositionDemo {

    record Person(String lastName, String firstName, int age) {}

    public static void main(String[] args) {
        List<Person> people = new ArrayList<>(List.of(
                new Person("Diaz", "Bruno", 41),
                new Person("Diaz", "Ana", 29),
                new Person("Ackerman", "Ana", 37),
                new Person("Diaz", "Ana", 52),
                new Person("Ackerman", "Bruno", 22)
        ));

        System.out.println("== Unsorted input ==");
        people.forEach(p -> System.out.println(p.lastName() + ", " + p.firstName() + " (" + p.age() + ")"));

        List<Person> byNameThenAge = new ArrayList<>(people);
        byNameThenAge.sort(
                Comparator.comparing(Person::lastName)
                        .thenComparing(Person::firstName)
                        .thenComparingInt(Person::age)
        );

        System.out.println();
        System.out.println("== Sorted: lastName -> firstName -> age (all ascending) ==");
        byNameThenAge.forEach(p -> System.out.println(p.lastName() + ", " + p.firstName() + " (" + p.age() + ")"));

        List<Person> byAgeDesc = new ArrayList<>(people);
        byAgeDesc.sort(
                Comparator.comparingInt(Person::age).reversed()
        );

        System.out.println();
        System.out.println("== Sorted: age descending, via .reversed() on the SAME comparator shape ==");
        byAgeDesc.forEach(p -> System.out.println(p.lastName() + ", " + p.firstName() + " (" + p.age() + ")"));

        List<Person> byLastNameDescThenFirstAsc = new ArrayList<>(people);
        byLastNameDescThenFirstAsc.sort(
                Comparator.comparing(Person::lastName, Comparator.reverseOrder())
                        .thenComparing(Person::firstName)
        );

        System.out.println();
        System.out.println("== Sorted: lastName DESCENDING, then firstName ascending (mixed directions, one chain) ==");
        byLastNameDescThenFirstAsc.forEach(p -> System.out.println(p.lastName() + ", " + p.firstName() + " (" + p.age() + ")"));
    }
}
