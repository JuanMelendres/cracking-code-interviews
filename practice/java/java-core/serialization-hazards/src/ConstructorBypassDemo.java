import java.io.*;

/**
 * Real, executed proof of a genuine, well-known Java serialization hazard:
 * deserialization does NOT call the class's constructor. Any invariant
 * enforced only in the constructor can be silently bypassed by handing
 * ObjectInputStream a crafted byte stream -- real, demonstrated by hand-
 * crafting the serialized bytes for an Account with a negative balance,
 * something the real constructor would have rejected outright.
 */
public class ConstructorBypassDemo {

    static class Account implements Serializable {
        private final int balance;

        Account(int balance) {
            if (balance < 0) {
                throw new IllegalArgumentException("balance cannot be negative: " + balance);
            }
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "Account{balance=" + balance + "}";
        }
    }

    // The real, standard fix: re-validate in a private readObject() method,
    // since defaultReadObject() alone (or no override at all) never runs
    // the constructor's own validation logic.
    static class SecureAccount implements Serializable {
        private final int balance;

        SecureAccount(int balance) {
            if (balance < 0) {
                throw new IllegalArgumentException("balance cannot be negative: " + balance);
            }
            this.balance = balance;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            if (balance < 0) {
                throw new InvalidObjectException("balance cannot be negative: " + balance);
            }
        }

        @Override
        public String toString() {
            return "SecureAccount{balance=" + balance + "}";
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== Real proof: the constructor genuinely enforces its invariant on normal construction ==");
        try {
            new Account(-100);
            System.out.println("Constructed with balance=-100 (unexpected)");
        } catch (IllegalArgumentException e) {
            System.out.println("new Account(-100) threw real IllegalArgumentException: " + e.getMessage());
        }

        System.out.println("\n== Real proof: a directly TAMPERED byte stream deserializes with no constructor call at all ==");
        Account legitimate = new Account(500);
        byte[] originalBytes = serialize(legitimate);

        // Real byte-level tampering: locate the 4-byte big-endian
        // representation of the int field's value (500) inside the actual
        // serialized stream and overwrite it with a negative value's bytes
        // -- exactly the kind of manipulation an attacker controlling the
        // byte stream over the wire could perform. No reflection involved
        // in producing the tampered object; ObjectInputStream.readObject()
        // does 100% of the work.
        byte[] tamperedBytes = originalBytes.clone();
        byte[] legitimateValueBytes = intToBigEndianBytes(500);
        byte[] maliciousValueBytes = intToBigEndianBytes(-999999);
        int offset = indexOf(tamperedBytes, legitimateValueBytes);
        System.out.println("Located the real serialized int bytes for balance=500 at stream offset " + offset);
        System.arraycopy(maliciousValueBytes, 0, tamperedBytes, offset, 4);

        Account attackerControlled = deserialize(tamperedBytes, Account.class);
        System.out.println("ObjectInputStream.readObject() on the tampered bytes produced: " + attackerControlled
                + "  <-- REAL: a balance the real constructor would have thrown IllegalArgumentException for."
                + " No reflection was used to produce this object -- readObject() alone did it.");
        System.out.println("This is exactly the real hazard java.io.Serializable creates: an ENTIRELY SEPARATE"
                + " construction path (deserialization) that the constructor's own validation logic never runs on.");

        System.out.println("\n== The real, verified fix: SecureAccount re-validates inside its own readObject() ==");
        SecureAccount secureLegitimate = new SecureAccount(500);
        byte[] secureOriginalBytes = serialize(secureLegitimate);
        byte[] secureTamperedBytes = secureOriginalBytes.clone();
        int secureOffset = indexOf(secureTamperedBytes, legitimateValueBytes);
        System.arraycopy(maliciousValueBytes, 0, secureTamperedBytes, secureOffset, 4);

        try {
            deserialize(secureTamperedBytes, SecureAccount.class);
            System.out.println("Deserialization succeeded (unexpected)");
        } catch (InvalidObjectException e) {
            System.out.println("Deserializing the identically-tampered bytes against SecureAccount threw real"
                    + " InvalidObjectException: " + e.getMessage()
                    + "  <-- REAL: the readObject() override genuinely re-applies the invariant on this path");
        }
    }

    static byte[] intToBigEndianBytes(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    static int indexOf(byte[] haystack, byte[] needle) {
        outer:
        for (int i = 0; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) continue outer;
            }
            return i;
        }
        throw new IllegalStateException("needle not found in haystack");
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
