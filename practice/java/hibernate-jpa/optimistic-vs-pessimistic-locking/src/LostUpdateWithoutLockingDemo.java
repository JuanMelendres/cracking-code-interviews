import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * The baseline problem both locking strategies exist to solve, reproduced without
 * either: two real Hibernate sessions load the SAME account row (an entity with no
 * @Version field at all -- see UnversionedAccount's own javadoc for why a versioned
 * entity can't be used to reproduce this baseline), each independently applies a
 * deposit based on the balance it read, and whichever commits second silently
 * overwrites the first's change -- a real, reproducible lost update.
 */
public final class LostUpdateWithoutLockingDemo {

    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateSupport.buildSessionFactory("lost_update_demo", false);
        Long accountId;

        try (Session setup = sessionFactory.openSession()) {
            setup.beginTransaction();
            UnversionedAccount account = new UnversionedAccount("shared-account", 100);
            setup.persist(account);
            setup.getTransaction().commit();
            accountId = account.getId();
        }

        System.out.println("=== Starting balance: $100 ===");
        System.out.println("Two real, independent sessions both read the account, then both deposit $50.");
        System.out.println("Expected if both deposits are honored: $200. Actual, with no locking:");
        System.out.println();

        Session sessionA = sessionFactory.openSession();
        Session sessionB = sessionFactory.openSession();

        sessionA.beginTransaction();
        sessionB.beginTransaction();

        UnversionedAccount accountA = sessionA.find(UnversionedAccount.class, accountId);
        UnversionedAccount accountB = sessionB.find(UnversionedAccount.class, accountId);
        System.out.println("Session A read balance: $" + accountA.getBalance());
        System.out.println("Session B read balance: $" + accountB.getBalance());

        accountA.setBalance(accountA.getBalance() + 50);
        sessionA.getTransaction().commit();
        System.out.println("Session A committed its deposit -> balance now $" + accountA.getBalance());

        accountB.setBalance(accountB.getBalance() + 50);
        sessionB.getTransaction().commit();
        System.out.println("Session B committed its deposit -> balance now $" + accountB.getBalance()
                + " (computed from its OWN stale read of $100, not A's already-committed $150)");

        sessionA.close();
        sessionB.close();

        try (Session verify = sessionFactory.openSession()) {
            UnversionedAccount finalAccount = verify.find(UnversionedAccount.class, accountId);
            System.out.println();
            System.out.println("=== Real final balance in the database: $" + finalAccount.getBalance() + " ===");
            System.out.println("Expected $200 (two real $50 deposits); Session A's deposit was silently lost.");
        }

        sessionFactory.close();
    }
}
