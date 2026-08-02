// A long-running, allocation-light, computation-heavy loop -- deliberately
// NOT allocation-heavy, so GC-triggered safepoints are rare, making any
// non-GC safepoint (triggered externally via jcmd) stand out clearly in the
// safepoint log rather than being buried among GC pauses.
public class SafepointDemo {
    public static void main(String[] args) throws Exception {
        long endAt = System.currentTimeMillis() + 8000;
        long acc = 0;
        long iterations = 0;
        while (System.currentTimeMillis() < endAt) {
            acc += Math.multiplyHigh(iterations, 0x9E3779B97F4A7C15L);
            iterations++;
        }
        System.out.println("iterations=" + iterations + " acc=" + acc);
    }
}
