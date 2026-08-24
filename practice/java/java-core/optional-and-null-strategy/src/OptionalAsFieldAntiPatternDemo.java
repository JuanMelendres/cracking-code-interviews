import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;

/**
 * Real, executed proof of why "Optional as a field type" is a documented
 * anti-pattern (Brian Goetz's own stated design intent: Optional was built
 * as a RETURN type for methods that might not have a result, not as a
 * general-purpose "nullable field" replacement). Optional itself doesn't
 * implement Serializable -- a class holding one as a field genuinely
 * cannot be serialized, a real, concrete, non-hypothetical consequence.
 */
public class OptionalAsFieldAntiPatternDemo {

    // The anti-pattern: Optional as a FIELD type.
    static class UserWithOptionalField implements Serializable {
        private final String name;
        private final Optional<String> middleName; // anti-pattern

        UserWithOptionalField(String name, Optional<String> middleName) {
            this.name = name;
            this.middleName = middleName;
        }
    }

    // The correct alternative: a plain, nullable field, with Optional used
    // only at the API boundary (a getter method), never stored directly.
    static class UserWithNullableField implements Serializable {
        private final String name;
        private final String middleName; // may genuinely be null

        UserWithNullableField(String name, String middleName) {
            this.name = name;
            this.middleName = middleName;
        }

        Optional<String> getMiddleName() {
            return Optional.ofNullable(middleName); // Optional used correctly: as a RETURN type
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== Real proof: java.util.Optional does NOT implement Serializable ==");
        System.out.println("Optional implements Serializable: "
                + java.util.Arrays.asList(Optional.class.getInterfaces()).contains(Serializable.class));

        System.out.println("\n== Real consequence: a class with an Optional FIELD genuinely cannot be serialized ==");
        UserWithOptionalField badUser = new UserWithOptionalField("Ada", Optional.of("Lovelace"));
        try {
            serialize(badUser);
            System.out.println("Serialization succeeded (unexpected)");
        } catch (NotSerializableException e) {
            System.out.println("Serialization threw real NotSerializableException: " + e.getMessage());
        }

        System.out.println("\n== The correct alternative: plain nullable field, Optional only at the API boundary ==");
        UserWithNullableField goodUser = new UserWithNullableField("Alan", null);
        byte[] serialized = serialize(goodUser);
        System.out.println("Serialization succeeded, " + serialized.length + " real bytes written");
        System.out.println("getMiddleName() still returns a real Optional at the call site: " + goodUser.getMiddleName());
    }

    static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }
}
