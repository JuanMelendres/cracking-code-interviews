package demo;

/** The real fix: the storage layer itself -- not the lock holder's own
 * good behavior -- rejects any write whose token is not higher than the
 * highest token it has already accepted. A stale lock holder's write is
 * rejected by the resource being written to, regardless of whether that
 * stale holder still (wrongly) believes it holds the lock. */
public class FencedStorage {
    private volatile String value;
    private volatile long lastAcceptedToken = -1;

    public synchronized boolean write(String newValue, long token, String writerLabel) {
        if (token <= lastAcceptedToken) {
            System.out.println("  [FencedStorage] " + writerLabel + " REJECTED: token " + token
                + " <= last accepted token " + lastAcceptedToken + " (stale writer)");
            return false;
        }
        value = newValue;
        lastAcceptedToken = token;
        System.out.println("  [FencedStorage] " + writerLabel + " ACCEPTED (token " + token + "): wrote " + newValue);
        return true;
    }

    public String read() {
        return value;
    }
}
