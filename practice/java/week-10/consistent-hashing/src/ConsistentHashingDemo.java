import java.security.MessageDigest;
import java.util.*;

/**
 * T-806 -- measures key redistribution on node removal, naive hash%N
 * versus consistent hashing with virtual nodes. 10,000 keys, 10 nodes,
 * remove 1 node, count how many keys map to a different node than before.
 */
public class ConsistentHashingDemo {
    static final int KEYS = 10_000;
    static final int NODES = 10;
    static final int VIRTUAL_NODES_PER_NODE = 150;

    public static void main(String[] args) throws Exception {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < KEYS; i++) keys.add("key-" + i);

        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < NODES; i++) nodes.add("node-" + i);

        System.out.println("== naive hash % N ==");
        naiveHashDemo(keys, nodes);

        System.out.println();
        System.out.println("== consistent hashing with " + VIRTUAL_NODES_PER_NODE + " virtual nodes per physical node ==");
        consistentHashDemo(keys, nodes);
    }

    static void naiveHashDemo(List<String> keys, List<String> nodes) {
        Map<String, String> before = new HashMap<>();
        for (String k : keys) before.put(k, nodes.get(Math.floorMod(k.hashCode(), nodes.size())));

        List<String> nodesAfterRemoval = new ArrayList<>(nodes.subList(0, nodes.size() - 1)); // remove the last node
        int moved = 0;
        for (String k : keys) {
            String newNode = nodesAfterRemoval.get(Math.floorMod(k.hashCode(), nodesAfterRemoval.size()));
            if (!newNode.equals(before.get(k))) moved++;
        }
        System.out.printf("removed 1 of %d nodes: %d of %d keys (%.1f%%) remapped to a different node%n",
                nodes.size(), moved, keys.size(), 100.0 * moved / keys.size());
        System.out.println("(theoretical worst case for hash%N on ANY node-count change: nearly ALL keys remap, "
                + "because N itself changed, and every key's slot is k.hashCode() % N)");
    }

    static void consistentHashDemo(List<String> keys, List<String> nodes) throws Exception {
        ConsistentHashRing ringBefore = new ConsistentHashRing(nodes, VIRTUAL_NODES_PER_NODE);
        Map<String, String> before = new HashMap<>();
        for (String k : keys) before.put(k, ringBefore.getNode(k));

        List<String> nodesAfterRemoval = new ArrayList<>(nodes.subList(0, nodes.size() - 1));
        ConsistentHashRing ringAfter = new ConsistentHashRing(nodesAfterRemoval, VIRTUAL_NODES_PER_NODE);
        int moved = 0;
        for (String k : keys) {
            String newNode = ringAfter.getNode(k);
            if (!newNode.equals(before.get(k))) moved++;
        }
        System.out.printf("removed 1 of %d nodes: %d of %d keys (%.1f%%) remapped to a different node%n",
                nodes.size(), moved, keys.size(), 100.0 * moved / keys.size());
        System.out.printf("(theoretical ideal for removing 1 of %d nodes: ~%.1f%% -- only that node's own keys "
                + "should move, to neighbors on the ring)%n", nodes.size(), 100.0 / nodes.size());
    }

    /** A minimal consistent-hash ring using SHA-256 for uniform key distribution. */
    static class ConsistentHashRing {
        final TreeMap<Long, String> ring = new TreeMap<>();

        ConsistentHashRing(List<String> nodes, int virtualNodesPerNode) throws Exception {
            for (String node : nodes) {
                for (int v = 0; v < virtualNodesPerNode; v++) {
                    ring.put(hash(node + "#" + v), node);
                }
            }
        }

        String getNode(String key) throws Exception {
            long h = hash(key);
            Map.Entry<Long, String> entry = ring.ceilingEntry(h);
            if (entry == null) entry = ring.firstEntry(); // wrap around the ring
            return entry.getValue();
        }

        static long hash(String s) throws Exception {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes());
            long h = 0;
            for (int i = 0; i < 8; i++) h = (h << 8) | (digest[i] & 0xFF);
            return h;
        }
    }
}
