import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

// Real demo: EC digital signature (SHA256withECDSA) proving integrity + authenticity,
// and showing that a single flipped bit in the signed message fails verification.
public class SignatureTamperDemo {

    public static void main(String[] args) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);
        KeyPair pair = kpg.generateKeyPair();

        byte[] original = "transfer:acct=1234,amount=100.00".getBytes(StandardCharsets.UTF_8);

        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(pair.getPrivate());
        signer.update(original);
        byte[] sig = signer.sign();
        System.out.println("message:   " + new String(original, StandardCharsets.UTF_8));
        System.out.println("signature: " + HexFormat.of().formatHex(sig).substring(0, 40) + "...");

        Signature verifier = Signature.getInstance("SHA256withECDSA");
        verifier.initVerify(pair.getPublic());
        verifier.update(original);
        System.out.println();
        System.out.println("verify(original message, same signature) = " + verifier.verify(sig));

        byte[] tampered = "transfer:acct=1234,amount=900.00".getBytes(StandardCharsets.UTF_8);
        Signature verifier2 = Signature.getInstance("SHA256withECDSA");
        verifier2.initVerify(pair.getPublic());
        verifier2.update(tampered);
        System.out.println("verify(tampered message '900.00', same signature) = " + verifier2.verify(sig));
    }
}
