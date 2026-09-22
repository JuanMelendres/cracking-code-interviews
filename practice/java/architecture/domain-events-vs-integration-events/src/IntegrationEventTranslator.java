import java.math.RoundingMode;

/**
 * The translation boundary itself: the one place internal domain-event
 * shape changes get absorbed, so the public integration-event contract
 * never has to change just because the write side's internal
 * representation did.
 */
public final class IntegrationEventTranslator {

    private IntegrationEventTranslator() {
    }

    public static OrderCompletedIntegrationEvent translate(OrderCompletedDomainEventV1 domainEvent) {
        String formattedTotal = String.format("%.2f", domainEvent.total);
        return new OrderCompletedIntegrationEvent(domainEvent.orderId, formattedTotal, domainEvent.customerId);
    }

    public static OrderCompletedIntegrationEvent translate(OrderCompletedDomainEventV2 domainEvent) {
        String formattedTotal = domainEvent.grandTotal.setScale(2, RoundingMode.HALF_UP).toString();
        // loyaltyPointsEarned is deliberately NOT carried onto the public
        // contract -- it's internal-only, exactly the kind of detail this
        // boundary exists to keep from leaking.
        return new OrderCompletedIntegrationEvent(domainEvent.orderId, formattedTotal, domainEvent.customerId);
    }
}
