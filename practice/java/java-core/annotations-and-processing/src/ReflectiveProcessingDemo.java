import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Real, executed proof of the actual mechanism behind annotation-driven
 * frameworks (JPA's @Column, Jackson's @JsonProperty): reflection scans a
 * class's fields for a custom annotation and real, dynamically builds
 * behavior (here, a column-name mapping and a real SQL INSERT statement)
 * purely from what it finds at runtime -- no generated code, no
 * annotation processor, just reflection + annotations combined.
 */
public class ReflectiveProcessingDemo {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Column {
        String value();
    }

    static class User {
        @Column("user_id")
        int id;

        @Column("full_name")
        String name;

        // Deliberately NOT annotated -- should be excluded from the real generated mapping.
        String internalCache;

        User(int id, String name, String internalCache) {
            this.id = id;
            this.name = name;
            this.internalCache = internalCache;
        }
    }

    public static void main(String[] args) throws Exception {
        User user = new User(42, "Ada Lovelace", "should-be-ignored");

        System.out.println("== Real field-by-field reflective scan for @Column ==");
        Map<String, Object> columnValues = new LinkedHashMap<>();
        for (Field field : User.class.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                System.out.println("field \"" + field.getName() + "\": no @Column, real EXCLUDED from mapping");
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(user);
            columnValues.put(column.value(), value);
            System.out.println("field \"" + field.getName() + "\" -> real @Column(\"" + column.value() + "\") = " + value);
        }

        System.out.println("\n== Real, dynamically-generated SQL, built purely from what reflection found ==");
        String columns = String.join(", ", columnValues.keySet());
        String placeholders = String.join(", ", columnValues.values().stream().map(v -> "?").toArray(String[]::new));
        String sql = "INSERT INTO users (" + columns + ") VALUES (" + placeholders + ")";
        System.out.println("Generated SQL: " + sql);
        System.out.println("Real bound values, in order: " + columnValues.values());
    }
}
