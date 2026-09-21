package demeter.v2;

public class Customer {
    private final String name;
    private final Wallet wallet;
    public Customer(String name, Wallet wallet) { this.name = name; this.wallet = wallet; }
    public String getName() { return name; }
    public Wallet getWallet() { return wallet; }

    // Public signature UNCHANGED from v1 -- only its internal implementation
    // adapts to Wallet's new multi-card API. This is the entire point.
    public String getCardLast4Digits() {
        return wallet.getPrimaryCard().map(Card::getLast4Digits).orElse("(none)");
    }
}
