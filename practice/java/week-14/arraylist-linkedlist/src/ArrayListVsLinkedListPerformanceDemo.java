import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArrayListVsLinkedListPerformanceDemo {
    static final int N = 50_000;
    static final int RANDOM_ACCESS_READS = 20_000;

    public static void main(String[] args) {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < N; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }

        System.out.println("== Random-access get(index): ArrayList O(1) vs LinkedList O(n) traversal ==");
        java.util.Random rnd = new java.util.Random(42);
        int[] indices = new int[RANDOM_ACCESS_READS];
        for (int i = 0; i < indices.length; i++) indices[i] = rnd.nextInt(N);

        long arrayStart = System.nanoTime();
        long arraySum = 0;
        for (int idx : indices) arraySum += arrayList.get(idx);
        long arrayNanos = System.nanoTime() - arrayStart;

        long linkedStart = System.nanoTime();
        long linkedSum = 0;
        for (int idx : indices) linkedSum += linkedList.get(idx);
        long linkedNanos = System.nanoTime() - linkedStart;

        System.out.printf("ArrayList:  %,d random get() calls in %,d ns (checksum=%d)%n", RANDOM_ACCESS_READS, arrayNanos, arraySum);
        System.out.printf("LinkedList: %,d random get() calls in %,d ns (checksum=%d)%n", RANDOM_ACCESS_READS, linkedNanos, linkedSum);
        System.out.printf("LinkedList is %.1fx slower for random access on a %,d-element list%n",
                (double) linkedNanos / arrayNanos, N);
        System.out.println("(LinkedList.get(i) must walk the list node-by-node from whichever end is closer --");
        System.out.println(" O(n) per call -- while ArrayList.get(i) is a direct array index, O(1) per call)");

        System.out.println();
        System.out.println("== Insertion at the FRONT: ArrayList O(n) shift vs LinkedList O(1) ==");
        int insertions = 20_000;

        List<Integer> arrayListFront = new ArrayList<>(arrayList);
        long arrayFrontStart = System.nanoTime();
        for (int i = 0; i < insertions; i++) arrayListFront.add(0, i);
        long arrayFrontNanos = System.nanoTime() - arrayFrontStart;

        List<Integer> linkedListFront = new LinkedList<>(linkedList);
        long linkedFrontStart = System.nanoTime();
        for (int i = 0; i < insertions; i++) ((LinkedList<Integer>) linkedListFront).addFirst(i);
        long linkedFrontNanos = System.nanoTime() - linkedFrontStart;

        System.out.printf("ArrayList.add(0, x):        %,d insertions in %,d ns%n", insertions, arrayFrontNanos);
        System.out.printf("LinkedList.addFirst(x):     %,d insertions in %,d ns%n", insertions, linkedFrontNanos);
        System.out.printf("ArrayList front-insertion is %.1fx slower than LinkedList here%n",
                (double) arrayFrontNanos / linkedFrontNanos);
        System.out.println("(every ArrayList.add(0, x) must shift every existing element right by one slot --");
        System.out.println(" O(n) per call -- while LinkedList.addFirst() only relinks two pointers, O(1) per call)");
    }
}
