import java.io.*;

/**
 * Real, executed proof of the JDK's own current, standard mitigation for
 * deserialization attacks: java.io.ObjectInputFilter (JEP 290, standard
 * since Java 9). A filter genuinely rejects deserializing a disallowed
 * class BEFORE the object graph is even reconstructed -- a real,
 * effective defense against the broader class of Java deserialization
 * vulnerabilities (gadget-chain attacks), not merely the two narrower
 * hazards (constructor bypass, singleton break) shown in this directory's
 * other demos.
 */
public class ObjectInputFilterDemo {

    static class Allowed implements Serializable {
        String value = "safe";
    }

    static class Disallowed implements Serializable {
        String value = "should be rejected";
    }

    public static void main(String[] args) throws Exception {
        byte[] allowedBytes = serialize(new Allowed());
        byte[] disallowedBytes = serialize(new Disallowed());

        System.out.println("== Real ObjectInputFilter: an explicit allow-list, real rejection of anything else ==");
        ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
                Allowed.class.getName() + ";!*"); // allow exactly Allowed, reject everything else

        Allowed allowed = deserializeWithFilter(allowedBytes, filter, Allowed.class);
        System.out.println("Deserializing an ALLOWED class succeeded: " + allowed.value);

        try {
            deserializeWithFilter(disallowedBytes, filter, Disallowed.class);
            System.out.println("Deserializing a DISALLOWED class succeeded (unexpected)");
        } catch (InvalidClassException e) {
            System.out.println("Deserializing a DISALLOWED class threw real InvalidClassException: " + e.getMessage()
                    + "  <-- REAL: the object graph was never even reconstructed; the filter rejected it up front");
        }

        System.out.println("\n== Real, process-wide default filter (JEP 415, Java 17+) ==");
        System.out.println("ObjectInputFilter.Config.getSerialFilter() = " + ObjectInputFilter.Config.getSerialFilter()
                + "  (this repo's JVM sets no process-wide default; production systems accepting untrusted"
                + " serialized data should configure one via -Djdk.serialFilter or ObjectInputFilter.Config.setSerialFilter,"
                + " so EVERY ObjectInputStream in the process is protected, not just ones that opt in individually)");
    }

    static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    static <T> T deserializeWithFilter(byte[] bytes, ObjectInputFilter filter, Class<T> type)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            ois.setObjectInputFilter(filter);
            return (T) ois.readObject();
        }
    }
}
