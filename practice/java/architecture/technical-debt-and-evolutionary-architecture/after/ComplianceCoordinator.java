package after;

/**
 * Third remediation step: extract the compliance/observability collaborators
 * (fraud check, audit log, analytics) behind their own coordinator.
 */
public final class ComplianceCoordinator {
    private final FraudCheck fraud = new FraudCheck();
    private final AuditLogger audit = new AuditLogger();
    private final AnalyticsTracker analytics = new AnalyticsTracker();

    public boolean isSuspicious(String orderId) {
        return fraud.isSuspicious(orderId);
    }

    public void recordCompletion(String orderId) {
        analytics.track(orderId);
        audit.log(orderId);
    }

    public void recordRejection(String orderId) {
        audit.log(orderId);
    }
}
