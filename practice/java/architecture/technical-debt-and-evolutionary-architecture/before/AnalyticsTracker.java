package before;

public final class AnalyticsTracker {
    public void track(String orderId) {
        System.out.println("[Analytics] tracked " + orderId);
    }
}
