import java.util.Map;

/**
 * A real downstream consumer -- a separate, independently-deployed service
 * (a notification service) that only ever sees the wire message, never the
 * publisher's internal classes. It reads specific named fields, exactly
 * like a real consumer parsing a real JSON payload would.
 */
public class NotificationConsumer {

    public String consumeExpectingField(Map<String, Object> wireMessage, String fieldName) {
        if (!wireMessage.containsKey(fieldName)) {
            throw new IllegalStateException(
                    "Consumer expected field \"" + fieldName + "\" but it was not present in the wire message. "
                            + "Actual fields present: " + wireMessage.keySet());
        }
        Object value = wireMessage.get(fieldName);
        return "Notification sent for order " + wireMessage.get("orderId") + ": amount=" + value;
    }
}
