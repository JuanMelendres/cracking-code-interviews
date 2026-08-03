import java.util.function.IntConsumer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/** LC 1195: Fizz Buzz Multithreaded. Four threads print 1..n cooperatively:
 * fizz (div by 3 only), buzz (div by 5 only), fizzbuzz (div by both), number (neither). */
public class FizzBuzzMultithreaded {
    private final int n;
    private final AtomicInteger current = new AtomicInteger(1);
    private final Semaphore fizzTurn = new Semaphore(0);
    private final Semaphore buzzTurn = new Semaphore(0);
    private final Semaphore fizzBuzzTurn = new Semaphore(0);
    private final Semaphore numberTurn = new Semaphore(1);

    public FizzBuzzMultithreaded(int n) {
        this.n = n;
    }

    private void dispatchNext() {
        int next = current.get();
        if (next > n) {
            // wake every waiter so they can observe completion and exit without blocking forever
            fizzTurn.release();
            buzzTurn.release();
            fizzBuzzTurn.release();
            numberTurn.release();
            return;
        }
        if (next % 15 == 0) fizzBuzzTurn.release();
        else if (next % 3 == 0) fizzTurn.release();
        else if (next % 5 == 0) buzzTurn.release();
        else numberTurn.release();
    }

    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            fizzTurn.acquire();
            if (current.get() > n) { dispatchNext(); return; }
            printFizz.run();
            current.incrementAndGet();
            dispatchNext();
        }
    }

    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            buzzTurn.acquire();
            if (current.get() > n) { dispatchNext(); return; }
            printBuzz.run();
            current.incrementAndGet();
            dispatchNext();
        }
    }

    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            fizzBuzzTurn.acquire();
            if (current.get() > n) { dispatchNext(); return; }
            printFizzBuzz.run();
            current.incrementAndGet();
            dispatchNext();
        }
    }

    public void number(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            numberTurn.acquire();
            int val = current.get();
            if (val > n) { dispatchNext(); return; }
            printNumber.accept(val);
            current.incrementAndGet();
            dispatchNext();
        }
    }
}
