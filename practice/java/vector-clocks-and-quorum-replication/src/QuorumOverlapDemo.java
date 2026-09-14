import java.util.ArrayList;
import java.util.List;

public class QuorumOverlapDemo {

    static List<List<Integer>> combinations(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        combine(result, new ArrayList<>(), 1, n, k);
        return result;
    }

    static void combine(List<List<Integer>> result, List<Integer> current, int start, int n, int k) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i <= n; i++) {
            current.add(i);
            combine(result, current, i + 1, n, k);
            current.remove(current.size() - 1);
        }
    }

    static boolean intersects(List<Integer> a, List<Integer> b) {
        for (int x : a) if (b.contains(x)) return true;
        return false;
    }

    static void check(int n, int w, int r) {
        List<List<Integer>> writeQuorums = combinations(n, w);
        List<List<Integer>> readQuorums = combinations(n, r);
        List<Integer> counterexampleWrite = null;
        List<Integer> counterexampleRead = null;
        long pairsChecked = 0;
        for (List<Integer> wq : writeQuorums) {
            for (List<Integer> rq : readQuorums) {
                pairsChecked++;
                if (!intersects(wq, rq)) {
                    counterexampleWrite = wq;
                    counterexampleRead = rq;
                }
            }
        }
        boolean guaranteed = (counterexampleWrite == null);
        System.out.printf("N=%d W=%d R=%d (W+R=%d, %s N): %d write-quorums x %d read-quorums = %d pairs checked -> %s%n",
                n, w, r, w + r, (w + r > n ? ">" : (w + r == n ? "=" : "<")),
                writeQuorums.size(), readQuorums.size(), pairsChecked,
                guaranteed ? "EVERY pair overlaps (read always sees latest write)" : "COUNTEREXAMPLE FOUND (stale read possible)");
        if (!guaranteed) {
            System.out.println("    e.g. write quorum " + counterexampleWrite + " and read quorum " + counterexampleRead + " share NO common replica");
        }
    }

    public static void main(String[] args) {
        int n = 5;
        check(n, 3, 3); // W+R=6 > N=5
        check(n, 3, 2); // W+R=5 = N=5
        check(n, 2, 2); // W+R=4 < N=5
        check(n, 1, 1); // W+R=2 < N=5 (both eventual-consistency style)
    }
}
