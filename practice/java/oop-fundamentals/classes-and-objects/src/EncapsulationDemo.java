import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates encapsulation: a class's fields are private, and every state
 * change goes through methods that enforce the class's own invariants.
 * Two versions of the same idea are compared directly: a broken,
 * public-field version that lets an invariant be violated from outside the
 * class, and a fixed, encapsulated version that makes the same violation
 * impossible to express.
 */
public class EncapsulationDemo {

    /** BROKEN: public fields, no invariant enforcement. */
    static class LeakyAccount {
        public double balance;

        LeakyAccount(double balance) {
            this.balance = balance;
        }
    }

    /** FIXED: private field, all mutation goes through validated methods. */
    static class BankAccount {
        private double balance;

        BankAccount(double openingBalance) {
            if (openingBalance < 0) {
                throw new IllegalArgumentException("Opening balance cannot be negative");
            }
            this.balance = openingBalance;
        }

        double getBalance() {
            return balance;
        }

        void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive");
            }
            balance += amount;
        }

        void withdraw(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive");
            }
            if (amount > balance) {
                throw new IllegalStateException("Insufficient funds: balance=" + balance + ", requested=" + amount);
            }
            balance -= amount;
        }
    }

    private static int assertions = 0;
    private static final List<String> failures = new ArrayList<>();

    private static void check(boolean condition, String description) {
        assertions++;
        if (!condition) {
            failures.add(description);
        }
    }

    public static void main(String[] args) {
        // --- The bug the leaky version allows ---
        LeakyAccount leaky = new LeakyAccount(100.0);
        leaky.balance = -500.0; // nothing stops this — the field is public
        check(leaky.balance == -500.0,
                "LeakyAccount's public field can be set directly to an invalid negative balance");

        // --- The same bug, made structurally impossible ---
        BankAccount account = new BankAccount(100.0);
        check(account.getBalance() == 100.0, "BankAccount starts with the requested opening balance");

        account.deposit(50.0);
        check(account.getBalance() == 150.0, "deposit() increases the balance by the deposited amount");

        boolean rejectedOverdraft = false;
        try {
            account.withdraw(1_000_000.0);
        } catch (IllegalStateException e) {
            rejectedOverdraft = true;
        }
        check(rejectedOverdraft, "withdraw() rejects an amount larger than the current balance");
        check(account.getBalance() == 150.0, "balance is unchanged after a rejected withdrawal");

        boolean rejectedNegativeDeposit = false;
        try {
            account.deposit(-10.0);
        } catch (IllegalArgumentException e) {
            rejectedNegativeDeposit = true;
        }
        check(rejectedNegativeDeposit, "deposit() rejects a negative amount");

        boolean rejectedNegativeOpening = false;
        try {
            new BankAccount(-1.0);
        } catch (IllegalArgumentException e) {
            rejectedNegativeOpening = true;
        }
        check(rejectedNegativeOpening, "the constructor itself rejects a negative opening balance");

        // There is no equivalent line to "account.balance = -500.0;" available
        // to write here at all — private fields make that a compile error,
        // not a runtime check. That's the actual mechanism encapsulation
        // provides: not "harder to misuse," but "impossible to express."

        System.out.println((failures.isEmpty() ? "PASS" : "FAIL") + " " + (assertions - failures.size()) + "/" + assertions + " assertions");
        for (String f : failures) {
            System.out.println("  FAILED: " + f);
        }
        if (!failures.isEmpty()) {
            System.exit(1);
        }
    }
}
