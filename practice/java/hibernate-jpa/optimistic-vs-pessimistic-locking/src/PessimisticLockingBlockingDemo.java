import jakarta.persistence.LockModeType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.concurrent.CountDownLatch;

/**
 * The opposite strategy for the identical conflict: PESSIMISTIC_WRITE really acquires
 * a real database row lock (H2's own locking, via SELECT ... FOR UPDATE under the
 * hood) at read time, so a second real thread's attempt to acquire the same lock
 * really BLOCKS -- measured here with real wall-clock timing -- until the first
 * transaction commits. No conflict is ever detected after the fact, because none is
 * ever allowed to happen concurrently in the first place.
 */
public final class PessimisticLockingBlockingDemo {

    public static void main(String[] args) throws Exception {
        SessionFactory sessionFactory = HibernateSupport.buildSessionFactory("pessimistic_demo", false);
        Long accountId;

        try (Session setup = sessionFactory.openSession()) {
            setup.beginTransaction();
            Account account = new Account("shared-account", 100);
            setup.persist(account);
            setup.getTransaction().commit();
            accountId = account.getId();
        }

        CountDownLatch threadAHasLock = new CountDownLatch(1);
        CountDownLatch releaseThreadA = new CountDownLatch(1);
        long[] threadBWaitStartNanos = new long[1];
        long[] threadBAcquiredNanos = new long[1];

        Thread threadA = new Thread(() -> {
            try (Session sessionA = sessionFactory.openSession()) {
                sessionA.beginTransaction();
                Account accountA = sessionA.find(Account.class, accountId, LockModeType.PESSIMISTIC_WRITE);
                System.out.println("Thread A acquired PESSIMISTIC_WRITE lock, balance=$" + accountA.getBalance());
                threadAHasLock.countDown();
                releaseThreadA.await();
                accountA.setBalance(accountA.getBalance() + 50);
                sessionA.getTransaction().commit();
                System.out.println("Thread A committed its deposit and released the lock -> balance=$"
                        + accountA.getBalance());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                threadAHasLock.await();
                try (Session sessionB = sessionFactory.openSession()) {
                    sessionB.beginTransaction();
                    System.out.println("Thread B now requests the same PESSIMISTIC_WRITE lock (will really block)...");
                    threadBWaitStartNanos[0] = System.nanoTime();
                    Account accountB = sessionB.find(Account.class, accountId, LockModeType.PESSIMISTIC_WRITE);
                    threadBAcquiredNanos[0] = System.nanoTime();
                    System.out.println("Thread B acquired the lock after Thread A released it, balance=$"
                            + accountB.getBalance());
                    accountB.setBalance(accountB.getBalance() + 50);
                    sessionB.getTransaction().commit();
                    System.out.println("Thread B committed its deposit -> balance=$" + accountB.getBalance());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        threadA.start();
        threadB.start();
        threadAHasLock.await();

        System.out.println("Main: Thread A is holding the lock; sleeping 1500ms before releasing it...");
        Thread.sleep(1500);
        releaseThreadA.countDown();

        threadA.join();
        threadB.join();

        double realBlockedMillis = (threadBAcquiredNanos[0] - threadBWaitStartNanos[0]) / 1_000_000.0;
        System.out.println();
        System.out.println("=== Real measured block time for Thread B: " + String.format("%.0f", realBlockedMillis)
                + "ms (expected ~1500ms, matching Thread A's held-lock duration) ===");

        try (Session verify = sessionFactory.openSession()) {
            Account finalAccount = verify.find(Account.class, accountId);
            System.out.println("Real final balance: $" + finalAccount.getBalance()
                    + " (expected $200 -- both deposits honored, no conflict was ever possible)");
        }

        sessionFactory.close();
    }
}
