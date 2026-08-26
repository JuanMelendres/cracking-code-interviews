package after;

public final class PaymentClient {
    public void charge(String orderId) {
        System.out.println("[Payment] charged " + orderId);
    }
}
