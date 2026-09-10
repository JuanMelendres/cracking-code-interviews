import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tiny, deterministic .class byte-patcher used only to reproduce two real
 * JVM failures for this chapter's demo: an UnsupportedClassVersionError
 * (patch the major-version bytes) and a VerifyError (patch one opcode byte
 * inside the known SumLoop.sumTo bytecode). Pure JDK, no dependencies.
 */
public class ClassBytePatcher {
    public static void main(String[] args) throws IOException {
        String mode = args[0];
        Path path = Path.of(args[1]);
        byte[] data = Files.readAllBytes(path);

        switch (mode) {
            case "version" -> {
                int newMajor = Integer.parseInt(args[2]);
                data[6] = (byte) ((newMajor >> 8) & 0xFF);
                data[7] = (byte) (newMajor & 0xFF);
                System.out.println("patched major version bytes to " + newMajor);
            }
            case "opcode" -> {
                int oldOp = Integer.decode(args[2]);
                int newOp = Integer.decode(args[3]);
                int idx = indexOf(data, (byte) oldOp);
                if (idx < 0) {
                    throw new IllegalStateException("opcode 0x" + Integer.toHexString(oldOp) + " not found");
                }
                data[idx] = (byte) newOp;
                System.out.println("patched first 0x" + Integer.toHexString(oldOp & 0xFF)
                        + " byte at file offset " + idx + " to 0x" + Integer.toHexString(newOp & 0xFF));
            }
            default -> throw new IllegalArgumentException("unknown mode: " + mode);
        }
        Files.write(path, data);
    }

    private static int indexOf(byte[] data, byte target) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == target) {
                return i;
            }
        }
        return -1;
    }
}
