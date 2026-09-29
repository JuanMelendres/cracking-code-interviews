import java.util.ArrayList;
import java.util.List;

/**
 * The collaborator under test double. Deliberately has observable side effects
 * (a call counter and an accumulating list) so the demo can prove whether real
 * code ran, instead of inferring it from a return value alone.
 */
public class AuditLog {

    private final List<String> written = new ArrayList<>();
    private int realCallCount = 0;

    public String write(String message) {
        realCallCount++;
        written.add(message);
        return "WROTE:" + message;
    }

    /**
     * Calls {@link #write} twice on {@code this}. Used to measure whether a
     * stub applied to {@code write} is honoured when the call originates inside
     * another real method of the same spied object.
     */
    public String writeTwice(String message) {
        return write(message) + "|" + write(message);
    }

    public int realCallCount() {
        return realCallCount;
    }

    public List<String> written() {
        return written;
    }

    /** Deliberately {@code final}, to test the "you cannot mock final" folklore. */
    public final String sealedWrite(String message) {
        realCallCount++;
        return "SEALED:" + message;
    }
}
