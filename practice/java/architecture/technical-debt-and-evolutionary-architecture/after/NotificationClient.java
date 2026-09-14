package after;

public final class NotificationClient {
    public void notifyCustomer(String orderId) {
        System.out.println("[Notification] notified customer for " + orderId);
    }
}
