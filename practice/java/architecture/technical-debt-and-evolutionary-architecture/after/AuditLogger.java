package after;

public final class AuditLogger {
    public void log(String orderId) {
        System.out.println("[Audit] logged " + orderId);
    }
}
