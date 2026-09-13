import java.util.List;

/** A real aggregate whose current state is derived entirely by folding over events. */
final class Account {
    private String owner;
    private long balance;

    static Account replay(List<Event> events) {
        Account account = new Account();
        for (Event event : events) {
            account.apply(event);
        }
        return account;
    }

    /** Replay starting from an already-known state (post-snapshot). */
    static Account replayFrom(Account snapshot, List<Event> tailEvents) {
        Account account = new Account();
        account.owner = snapshot.owner;
        account.balance = snapshot.balance;
        for (Event event : tailEvents) {
            account.apply(event);
        }
        return account;
    }

    private void apply(Event event) {
        switch (event) {
            case AccountOpened(String o) -> this.owner = o;
            case MoneyDeposited(int amount) -> this.balance += amount;
            case MoneyWithdrawn(int amount) -> this.balance -= amount;
        }
    }

    long getBalance() {
        return balance;
    }

    String getOwner() {
        return owner;
    }
}
