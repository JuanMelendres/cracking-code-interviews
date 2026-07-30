import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

/** LC 1116: Print Zero Even Odd. Three threads (zero/even/odd) must together
 * print 0,1,0,2,0,3,0,4,...,0,n -- a zero before every number, alternating
 * which of the even/odd thread supplies the next number. */
public class ZeroEvenOdd {
    private final int n;
    private final Semaphore zeroTurn = new Semaphore(1);
    private final Semaphore evenTurn = new Semaphore(0);
    private final Semaphore oddTurn = new Semaphore(0);

    public ZeroEvenOdd(int n) {
        this.n = n;
    }

    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            zeroTurn.acquire();
            printNumber.accept(0);
            if (i % 2 == 1) oddTurn.release(); else evenTurn.release();
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 2; i <= n; i += 2) {
            evenTurn.acquire();
            printNumber.accept(i);
            zeroTurn.release();
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i += 2) {
            oddTurn.acquire();
            printNumber.accept(i);
            zeroTurn.release();
        }
    }
}
