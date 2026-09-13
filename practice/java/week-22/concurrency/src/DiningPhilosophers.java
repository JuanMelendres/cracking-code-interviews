import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** LC 1226: The Dining Philosophers. Classic deadlock-avoidance exercise: 5 philosophers,
 * 5 forks, each needs both neighboring forks to eat. Naive "always pick up left fork first"
 * can deadlock if all 5 grab their left fork simultaneously; this breaks the cycle by having
 * the highest-indexed philosopher pick up their RIGHT fork first (a resource-ordering fix). */
public class DiningPhilosophers {
    private final Lock[] forks = new Lock[5];

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) forks[i] = new ReentrantLock();
    }

    public void wantsToEat(int philosopher, Runnable pickLeftFork, Runnable pickRightFork,
                           Runnable eat, Runnable putLeftFork, Runnable putRightFork) throws InterruptedException {
        int left = philosopher;
        int right = (philosopher + 4) % 5;

        Lock firstFork, secondFork;
        Runnable pickFirst, pickSecond, putFirst, putSecond;
        if (philosopher == 4) {
            // break the circular wait: philosopher 4 reaches for its right fork first
            firstFork = forks[right]; secondFork = forks[left];
            pickFirst = pickRightFork; pickSecond = pickLeftFork;
            putFirst = putRightFork; putSecond = putLeftFork;
        } else {
            firstFork = forks[left]; secondFork = forks[right];
            pickFirst = pickLeftFork; pickSecond = pickRightFork;
            putFirst = putLeftFork; putSecond = putRightFork;
        }

        firstFork.lock();
        try {
            secondFork.lock();
            try {
                pickFirst.run();
                pickSecond.run();
                eat.run();
                putFirst.run();
                putSecond.run();
            } finally {
                secondFork.unlock();
            }
        } finally {
            firstFork.unlock();
        }
    }
}
