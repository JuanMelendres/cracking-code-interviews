package demeter.v2;

public class DemeterRefundService {
    public String describeRefundTarget(Customer customer) {
        return "Refunding to card ending " + customer.getCardLast4Digits();
    }
}
