package demeter.v2;

public class TrainWreckReceiptPrinter {
    public String printLine(Customer customer) {
        // Law of Demeter violation: reaches through Customer -> Wallet -> Card.
        return "Receipt for " + customer.getName() + ", card ending " + customer.getWallet().getCard().getLast4Digits();
    }
}
