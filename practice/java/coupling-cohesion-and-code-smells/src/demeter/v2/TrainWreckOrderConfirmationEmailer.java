package demeter.v2;

public class TrainWreckOrderConfirmationEmailer {
    public String buildEmailBody(Customer customer) {
        // Same violation, a third, independent call site.
        return "Hi " + customer.getName() + ", your order was charged to card ending "
                + customer.getWallet().getCard().getLast4Digits();
    }
}
