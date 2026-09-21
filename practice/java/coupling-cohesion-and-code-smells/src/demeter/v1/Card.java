package demeter.v1;

public class Card {
    private final String last4;
    public Card(String last4) { this.last4 = last4; }
    public String getLast4Digits() { return last4; }
}
