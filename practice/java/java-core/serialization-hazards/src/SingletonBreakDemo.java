import java.io.*;

/**
 * Real, executed proof of another classic, real serialization hazard:
 * making a Singleton class Serializable genuinely breaks its "exactly one
 * instance" guarantee -- deserialization creates a real, brand-new object,
 * not the original singleton -- unless readResolve() is implemented to
 * redirect deserialization back to the canonical instance.
 */
public class SingletonBreakDemo {

    static class BrokenSingleton implements Serializable {
        static final BrokenSingleton INSTANCE = new BrokenSingleton();

        private BrokenSingleton() {
        }
    }

    static class FixedSingleton implements Serializable {
        static final FixedSingleton INSTANCE = new FixedSingleton();

        private FixedSingleton() {
        }

        // The real, standard fix: redirect deserialization to the
        // canonical instance instead of letting it produce a new one.
        private Object readResolve() {
            return INSTANCE;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== Real proof: naive Serializable singleton is genuinely broken by a round trip ==");
        byte[] brokenBytes = serialize(BrokenSingleton.INSTANCE);
        BrokenSingleton deserializedBroken = deserialize(brokenBytes, BrokenSingleton.class);
        System.out.println("BrokenSingleton.INSTANCE == deserialize(serialize(INSTANCE)): "
                + (BrokenSingleton.INSTANCE == deserializedBroken)
                + "  <-- REAL: false. Deserialization created a genuinely SECOND, distinct instance,"
                + " breaking the singleton guarantee, despite the private constructor.");

        System.out.println("\n== The real, verified fix: readResolve() redirects deserialization back to INSTANCE ==");
        byte[] fixedBytes = serialize(FixedSingleton.INSTANCE);
        FixedSingleton deserializedFixed = deserialize(fixedBytes, FixedSingleton.class);
        System.out.println("FixedSingleton.INSTANCE == deserialize(serialize(INSTANCE)): "
                + (FixedSingleton.INSTANCE == deserializedFixed)
                + "  <-- REAL: true. readResolve() genuinely preserved the singleton guarantee across the round trip.");
    }

    static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    static <T> T deserialize(byte[] bytes, Class<T> type) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T) ois.readObject();
        }
    }
}
