package demo;

/** The naive, real-world-common shape: any client that believes it holds
 * the lock can write, with no check at all against the lock service's
 * own current state. This is deliberately the bug, not a strawman --
 * this is exactly what "check the lock, then write" application code
 * looks like without fencing tokens. */
public class UnsafeStorage {
    private volatile String value;

    public void write(String newValue, String writerLabel) {
        value = newValue;
        System.out.println("  [UnsafeStorage] " + writerLabel + " wrote: " + newValue);
    }

    public String read() {
        return value;
    }
}
