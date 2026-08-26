import jakarta.persistence.OptimisticLockException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * The identical scenario as {@link LostUpdateWithoutLockingDemo}, against the
 * @Version-bearing {@link Account} entity instead. This chapter's central, testable
 * distinction: optimistic locking does not PREVENT the second session from reading
 * stale data or attempting its update -- it DETECTS the conflict at commit time and
 * throws, real and unhandled here on purpose, rather than silently losing the first
 * write. The misconception this demo disproves directly: that optimistic locking
 * stops the conflict from happening. It doesn't; it makes the conflict loud instead
 * of silent.
 */
public final class OptimisticLockingDetectionDemo {

    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateSupport.buildSessionFactory("optimistic_detect_demo", false);
        Long accountId;

        try (Session setup = sessionFactory.openSession()) {
            setup.beginTransaction();
            Account account = new Account("shared-account", 100);
            setup.persist(account);
            setup.getTransaction().commit();
            accountId = account.getId();
            System.out.println("Created account with balance $100, version " + account.getVersion());
        }

        Session sessionA = sessionFactory.openSession();
        Session sessionB = sessionFactory.openSession();

        sessionA.beginTransaction();
        sessionB.beginTransaction();

        Account accountA = sessionA.find(Account.class, accountId);
        Account accountB = sessionB.find(Account.class, accountId);
        System.out.println("Session A read balance=$" + accountA.getBalance() + " version=" + accountA.getVersion());
        System.out.println("Session B read balance=$" + accountB.getBalance() + " version=" + accountB.getVersion()
                + " (same version -- both read before either wrote)");

        accountA.setBalance(accountA.getBalance() + 50);
        sessionA.getTransaction().commit();
        System.out.println("Session A committed -> balance=$" + accountA.getBalance()
                + " version incremented to " + accountA.getVersion());

        System.out.println();
        System.out.println("Session B now attempts to commit its deposit, still holding version="
                + accountB.getVersion() + " (stale -- the real row is already at a higher version):");
        try {
            accountB.setBalance(accountB.getBalance() + 50);
            sessionB.getTransaction().commit();
            System.out.println("Session B committed without error -- THIS WOULD BE A BUG in this demo.");
        } catch (OptimisticLockException e) {
            System.out.println("REAL " + e.getClass().getName() + " thrown at commit time.");
            System.out.println("Detected, not prevented: Session B was allowed to read stale data and compute");
            System.out.println("its update against it -- the version check only fires at the UPDATE statement,");
            System.out.println("comparing the row's real current version against the version last read.");
        } finally {
            sessionB.getTransaction().rollback();
        }

        sessionA.close();
        sessionB.close();

        try (Session verify = sessionFactory.openSession()) {
            Account finalAccount = verify.find(Account.class, accountId);
            System.out.println();
            System.out.println("=== Real final balance: $" + finalAccount.getBalance()
                    + ", version " + finalAccount.getVersion() + " ===");
            System.out.println("Session A's deposit is intact. Session B's conflicting deposit was rejected,");
            System.out.println("not silently lost -- the application now knows it must decide what to do next.");
        }

        sessionFactory.close();
    }
}
