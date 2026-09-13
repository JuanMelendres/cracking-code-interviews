import java.util.concurrent.Semaphore;

/** LC 1114: Print in Order. Three threads call first/second/third in an unspecified
 * scheduling order; enforce first-then-second-then-third output regardless. */
public class PrintInOrder {
    private final Semaphore secondReady = new Semaphore(0);
    private final Semaphore thirdReady = new Semaphore(0);

    public void first(Runnable printFirst) throws InterruptedException {
        printFirst.run();
        secondReady.release();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        secondReady.acquire();
        printSecond.run();
        thirdReady.release();
    }

    public void third(Runnable printThird) throws InterruptedException {
        thirdReady.acquire();
        printThird.run();
    }
}
