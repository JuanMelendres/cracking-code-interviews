package demeter.v2;

public class TrainWreckRefundService {
    public String describeRefundTarget(Customer customer) {
        // Same violation, a second, independent call site.
        return "Refunding to card ending " + customer.getWallet().getCard().getLast4Digits();
    }
}
