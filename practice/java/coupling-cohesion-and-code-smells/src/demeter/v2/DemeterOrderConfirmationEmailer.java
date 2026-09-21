package demeter.v2;

public class DemeterOrderConfirmationEmailer {
    public String buildEmailBody(Customer customer) {
        return "Hi " + customer.getName() + ", your order was charged to card ending " + customer.getCardLast4Digits();
    }
}
