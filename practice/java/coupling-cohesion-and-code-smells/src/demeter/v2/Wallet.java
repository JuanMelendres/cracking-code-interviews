package demeter.v2;

import java.util.List;
import java.util.Optional;

/**
 * v2: a real internal-structure change -- Wallet now holds multiple cards
 * instead of one, and the old single-card accessor getCard() is gone. Any
 * caller that reached through Wallet directly for a card is now broken;
 * any caller that only ever talked to Customer is unaffected.
 */
public class Wallet {
    private final List<Card> cards;
    public Wallet(List<Card> cards) { this.cards = cards; }
    public Optional<Card> getPrimaryCard() { return cards.isEmpty() ? Optional.empty() : Optional.of(cards.get(0)); }
}
