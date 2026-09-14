import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * A real, minimal in-memory publish/subscribe bus: subscribers register by event
 * class and are invoked asynchronously on a shared executor -- close enough to a real
 * message broker's decoupled dispatch to demonstrate the actual property choreography
 * depends on: a publisher never calls a subscriber directly, so no call stack connects
 * "the event was published" to "a subscriber handled it."
 */
final class EventBus {

    private final Map<Class<?>, List<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    EventBus(ExecutorService executor) {
        this.executor = executor;
    }

    <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(event -> handler.accept(eventType.cast(event)));
    }

    void publish(Object event) {
        List<Consumer<Object>> handlers = subscribers.get(event.getClass());
        if (handlers == null) return;
        for (Consumer<Object> handler : handlers) {
            executor.submit(() -> handler.accept(event));
        }
    }
}
