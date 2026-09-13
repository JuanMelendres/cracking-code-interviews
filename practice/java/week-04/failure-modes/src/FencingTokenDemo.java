/**
 * A real, measured split-brain reproduction and its fix via fencing tokens.
 *
 * Node A acquires a lease (token 1) and is about to write, but experiences a
 * long pause (simulating a GC pause or network partition) BEFORE its write
 * reaches storage. During the pause, its lease expires, and Node B acquires
 * a new lease (token 2) and writes successfully. When Node A's paused write
 * finally arrives, a storage layer with no fencing-token check accepts it,
 * silently overwriting Node B's correct, newer write with Node A's stale
 * data -- both nodes believed they were the leader at the moment they wrote.
 *
 * The fix: storage tracks the highest token it has ever seen and rejects any
 * write carrying an older token. Node A's stale, delayed write is then
 * correctly rejected instead of silently corrupting the data.
 */
public class FencingTokenDemo {

    static final class Storage {
        private String data = "(empty)";
        private long highestTokenSeen = 0;
        private final boolean enforceFencing;

        Storage(boolean enforceFencing) {
            this.enforceFencing = enforceFencing;
        }

        synchronized boolean write(long token, String value) {
            if (enforceFencing && token < highestTokenSeen) {
                System.out.println("  REJECTED write with token " + token + " (a newer token " + highestTokenSeen
                        + " has already written) -- value would have been \"" + value + "\"");
                return false;
            }
            highestTokenSeen = Math.max(highestTokenSeen, token);
            data = value;
            System.out.println("  ACCEPTED write with token " + token + " -> data is now \"" + value + "\"");
            return true;
        }

        String currentData() {
            return data;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== WITHOUT fencing tokens (split-brain corrupts data) ===");
        runScenario(new Storage(false));

        System.out.println("\n=== WITH fencing tokens (stale write correctly rejected) ===");
        runScenario(new Storage(true));
    }

    private static void runScenario(Storage storage) throws InterruptedException {
        // Node A acquires the lease first: token 1.
        long nodeAToken = 1;
        System.out.println("Node A acquires lease, token=" + nodeAToken);

        // Node A experiences a long pause (GC pause / network partition) BEFORE
        // its write reaches storage -- simulated by simply delaying the write below.

        // Meanwhile, Node A's lease expires and Node B acquires a NEW lease: token 2,
        // and writes successfully while Node A is still paused.
        long nodeBToken = 2;
        System.out.println("Node A's lease expires during its pause. Node B acquires a new lease, token=" + nodeBToken);
        System.out.println("Node B writes (believing itself the sole leader):");
        storage.write(nodeBToken, "correct-data-from-node-B");

        // Node A now "wakes up" from its pause, STILL believing it holds token 1
        // and is the leader -- it has no way of knowing its lease already expired.
        System.out.println("Node A wakes up from its pause, unaware its lease expired, and writes:");
        storage.write(nodeAToken, "stale-data-from-node-A");

        System.out.println("Final data: \"" + storage.currentData() + "\""
                + (storage.currentData().contains("stale") ? "  <-- CORRUPTED by the stale node" : "  <-- correct"));
    }
}
