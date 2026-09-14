import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

// Real demo of envelope encryption + key rotation: ciphertext is tagged with
// the key VERSION that encrypted it, so old ciphertexts stay decryptable
// after rotation without a mass re-encryption pass, and rotating a key does
// not require anyone to know which records used which version in advance.
public class KeyRotationDemo {

    record Ciphertext(int keyVersion, byte[] iv, byte[] bytes) {}

    static final Map<Integer, SecretKey> KEY_RING = new HashMap<>();
    static int currentVersion = 1;

    static SecretKey generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        return kg.generateKey();
    }

    static void rotateKey() throws Exception {
        currentVersion++;
        KEY_RING.put(currentVersion, generateKey());
    }

    static Ciphertext encrypt(byte[] plaintext) throws Exception {
        SecretKey key = KEY_RING.get(currentVersion);
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return new Ciphertext(currentVersion, iv, c.doFinal(plaintext));
    }

    static byte[] decrypt(Ciphertext ct) throws Exception {
        SecretKey key = KEY_RING.get(ct.keyVersion());
        if (key == null) throw new IllegalStateException("no key for version " + ct.keyVersion());
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, ct.iv()));
        return c.doFinal(ct.bytes());
    }

    public static void main(String[] args) throws Exception {
        KEY_RING.put(1, generateKey());

        System.out.println("=== key v1 active: encrypt a record ===");
        Ciphertext ctV1 = encrypt("ssn:123-45-6789".getBytes());
        System.out.println("keyVersion=" + ctV1.keyVersion()
                + " ciphertext=" + HexFormat.of().formatHex(ctV1.bytes()));

        System.out.println();
        System.out.println("=== rotate to key v2 (scheduled rotation, no downtime) ===");
        rotateKey();
        System.out.println("currentVersion now = " + currentVersion);

        Ciphertext ctV2 = encrypt("ssn:987-65-4321".getBytes());
        System.out.println("new record encrypted under keyVersion=" + ctV2.keyVersion());

        System.out.println();
        System.out.println("=== decrypt BOTH old (v1) and new (v2) records after rotation ===");
        System.out.println("v1 record decrypts to: " + new String(decrypt(ctV1)));
        System.out.println("v2 record decrypts to: " + new String(decrypt(ctV2)));

        System.out.println();
        System.out.println("=== simulate v1 key retirement (removed from ring after re-encryption sweep) ===");
        KEY_RING.remove(1);
        try {
            decrypt(ctV1);
            System.out.println("decrypted (BUG -- retired key should not still work)");
        } catch (IllegalStateException e) {
            System.out.println("v1 record now fails: " + e.getMessage()
                    + "  (this is why rotation runbooks re-encrypt-and-verify BEFORE retiring the old key)");
        }
    }
}
