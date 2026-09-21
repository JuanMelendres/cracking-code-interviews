package demeter.v1;

public class Customer {
    private final String name;
    private final Wallet wallet;
    public Customer(String name, Wallet wallet) { this.name = name; this.wallet = wallet; }
    public String getName() { return name; }
    public Wallet getWallet() { return wallet; }

    // Demeter-compliant: Customer delegates, so callers never touch Wallet or Card directly.
    public String getCardLast4Digits() {
        return wallet.getCard().getLast4Digits();
    }
}
