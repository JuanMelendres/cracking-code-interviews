import java.util.concurrent.Semaphore;

/** LC 1117: Building H2O. Exactly 2 hydrogen threads and 1 oxygen thread must
 * release per water molecule; hydrogen must never proceed more than 2-ahead of oxygen. */
public class H2O {
    private final Semaphore hydrogenSlots = new Semaphore(2); // at most 2 H allowed in per molecule
    private final Semaphore oxygenTurn = new Semaphore(0);    // released once both H have bonded
    private int hydrogenCount = 0;
    private final Object lock = new Object();

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        hydrogenSlots.acquire();
        boolean isSecond;
        synchronized (lock) {
            hydrogenCount++;
            isSecond = (hydrogenCount % 2 == 0);
        }
        releaseHydrogen.run();
        if (isSecond) {
            oxygenTurn.release(); // second H of this molecule signals oxygen
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        oxygenTurn.acquire();
        releaseOxygen.run();
        hydrogenSlots.release(2); // free both H slots for the next molecule
    }
}
