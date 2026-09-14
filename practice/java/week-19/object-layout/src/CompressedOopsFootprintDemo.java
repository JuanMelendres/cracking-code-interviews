import java.util.ArrayList;
import java.util.List;

// Real, measured memory-footprint comparison: allocate a large number of
// small, reference-heavy objects (a singly-linked-list-style node, holding
// one reference field), with -XX:+UseCompressedOops (default under 32GB
// heap) versus -XX:-UseCompressedOops explicitly disabled -- same object
// graph, same count, only the flag differs.
public class CompressedOopsFootprintDemo {

    static class Node {
        Node next;
        long value;
    }

    public static void main(String[] args) {
        int count = 5_000_000;
        Runtime rt = Runtime.getRuntime();
        System.gc();
        long before = rt.totalMemory() - rt.freeMemory();

        List<Node> keepAlive = new ArrayList<>(count);
        Node prev = null;
        for (int i = 0; i < count; i++) {
            Node n = new Node();
            n.value = i;
            n.next = prev;
            prev = n;
            keepAlive.add(n); // keep every node reachable
        }

        System.gc();
        long after = rt.totalMemory() - rt.freeMemory();
        long usedBytes = after - before;

        System.out.println("nodes=" + count);
        System.out.println("heap used for " + count + " Node objects (each: 1 ref field + 1 long field): "
                + (usedBytes / (1024 * 1024)) + " MB");
        System.out.println("bytes per node (approx): " + (usedBytes / count));
        System.out.println("keepAlive.size()=" + keepAlive.size()); // prevent dead-code elimination
    }
}
