import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Every Collectors factory method a backend engineer is likely to reach for,
 * run once against one shared dataset so the outputs are directly comparable.
 * Java 21. No dependencies.
 */
public final class CollectorCatalogDemo {

    record Employee(String name, String department, int salary, int age) {}

    static final List<Employee> STAFF = List.of(
            new Employee("Ana", "Engineering", 95_000, 34),
            new Employee("Ben", "Engineering", 120_000, 41),
            new Employee("Cleo", "Design", 88_000, 29),
            new Employee("Dev", "Sales", 72_000, 45),
            new Employee("Eve", "Sales", 81_000, 38),
            new Employee("Fay", "Design", 91_000, 31));

    public static void main(String[] args) {
        line("toList()", STAFF.stream().map(Employee::name).collect(Collectors.toList()));
        line("toUnmodifiableList()", STAFF.stream().map(Employee::name).collect(Collectors.toUnmodifiableList()));
        line("toSet()", STAFF.stream().map(Employee::department).collect(Collectors.toSet()));

        line("joining()", STAFF.stream().map(Employee::name).collect(Collectors.joining()));
        line("joining(\", \")", STAFF.stream().map(Employee::name).collect(Collectors.joining(", ")));
        line("joining(\", \", \"[\", \"]\")",
                STAFF.stream().map(Employee::name).collect(Collectors.joining(", ", "[", "]")));

        line("counting()", STAFF.stream().collect(Collectors.counting()));
        line("summingInt(salary)", STAFF.stream().collect(Collectors.summingInt(Employee::salary)));
        line("averagingInt(age)", STAFF.stream().collect(Collectors.averagingInt(Employee::age)));
        line("summarizingInt(salary)", STAFF.stream().collect(Collectors.summarizingInt(Employee::salary)));

        line("minBy(salary)", STAFF.stream()
                .collect(Collectors.minBy((a, b) -> Integer.compare(a.salary(), b.salary())))
                .map(Employee::name));
        line("maxBy(salary)", STAFF.stream()
                .collect(Collectors.maxBy((a, b) -> Integer.compare(a.salary(), b.salary())))
                .map(Employee::name));

        line("partitioningBy(salary > 90k)",
                STAFF.stream().collect(Collectors.partitioningBy(e -> e.salary() > 90_000,
                        Collectors.mapping(Employee::name, Collectors.toList()))));

        line("groupingBy(department)",
                STAFF.stream().collect(Collectors.groupingBy(Employee::department,
                        Collectors.mapping(Employee::name, Collectors.toList()))));
        line("groupingBy + counting()",
                STAFF.stream().collect(Collectors.groupingBy(Employee::department, Collectors.counting())));
        line("groupingBy + averagingInt",
                STAFF.stream().collect(Collectors.groupingBy(Employee::department,
                        Collectors.averagingInt(Employee::salary))));
        line("groupingBy(TreeMap::new, ...)",
                STAFF.stream().collect(Collectors.groupingBy(Employee::department,
                        TreeMap::new, Collectors.counting())));

        line("filtering(age < 40)",
                STAFF.stream().collect(Collectors.groupingBy(Employee::department,
                        Collectors.filtering(e -> e.age() < 40,
                                Collectors.mapping(Employee::name, Collectors.toList())))));
        line("flatMapping(name chars)",
                STAFF.stream().collect(Collectors.groupingBy(Employee::department,
                        Collectors.flatMapping(e -> e.name().chars().mapToObj(c -> (char) c),
                                Collectors.toSet()))));

        line("toMap(name, salary)",
                STAFF.stream().collect(Collectors.toMap(Employee::name, Employee::salary)));
        line("toMap(dept, salary, merge)",
                STAFF.stream().collect(Collectors.toMap(Employee::department, Employee::salary, Integer::sum)));
        line("toMap(..., LinkedHashMap::new)",
                STAFF.stream().collect(Collectors.toMap(Employee::name, Employee::salary,
                        (a, b) -> a, LinkedHashMap::new)));

        line("reducing(0, salary, Integer::sum)",
                STAFF.stream().collect(Collectors.reducing(0, Employee::salary, Integer::sum)));

        line("teeing(min, max)", STAFF.stream().collect(Collectors.teeing(
                Collectors.minBy((a, b) -> Integer.compare(a.salary(), b.salary())),
                Collectors.maxBy((a, b) -> Integer.compare(a.salary(), b.salary())),
                (lowest, highest) -> lowest.map(Employee::name).orElse("?")
                        + " ... " + highest.map(Employee::name).orElse("?"))));

        line("collectingAndThen(toList, size)", STAFF.stream().map(Employee::name)
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::size)));

        System.out.println();
        System.out.println("== Mutability, checked rather than assumed ==");
        checkMutable("toList()", STAFF.stream().map(Employee::name).collect(Collectors.toList()));
        checkMutable("toUnmodifiableList()",
                STAFF.stream().map(Employee::name).collect(Collectors.toUnmodifiableList()));
        checkMutable("Stream.toList()", STAFF.stream().map(Employee::name).toList());
    }

    static void line(String label, Object value) {
        Object shown = value instanceof Optional<?> o ? o.orElse(null) : value;
        System.out.printf("%-32s %s%n", label, shown);
    }

    static void checkMutable(String label, List<String> list) {
        try {
            list.add("probe");
            System.out.printf("  %-24s MUTABLE   (add succeeded)%n", label);
        } catch (UnsupportedOperationException e) {
            System.out.printf("  %-24s IMMUTABLE (UnsupportedOperationException)%n", label);
        }
    }
}
