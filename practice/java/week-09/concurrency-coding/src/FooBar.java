import java.util.concurrent.Semaphore;

/** LC 1115: Print FooBar Alternately. Two threads must interleave foo/bar/foo/bar/...
 * exactly n times each, never foo-foo or bar-bar. */
public class FooBar {
    private final int n;
    private final Semaphore fooTurn = new Semaphore(1);
    private final Semaphore barTurn = new Semaphore(0);

    public FooBar(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            fooTurn.acquire();
            printFoo.run();
            barTurn.release();
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            barTurn.acquire();
            printBar.run();
            fooTurn.release();
        }
    }
}
