import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Deliberately identical to {@link Account} except for the missing @Version field --
 * used only for the baseline lost-update demo. Real, first-hand finding while
 * building this pack: once an entity DOES have @Version, Hibernate enforces
 * optimistic checking on every UPDATE unconditionally -- there is no way to write to
 * a versioned entity "without locking" to reproduce the baseline problem. A separate,
 * genuinely unversioned entity is required to show what @Version is actually solving.
 */
@Entity
public class UnversionedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;

    private int balance;

    protected UnversionedAccount() {
    }

    public UnversionedAccount(String owner, int balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
