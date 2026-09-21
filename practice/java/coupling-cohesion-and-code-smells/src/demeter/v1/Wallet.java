package demeter.v1;

public class Wallet {
    private final Card card;
    public Wallet(Card card) { this.card = card; }
    public Card getCard() { return card; }
}
