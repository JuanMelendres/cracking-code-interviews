import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observer, Command, Chain of Responsibility, and Template Method, executed --
 * including the two failure modes an interviewer follows up with: a listener
 * that throws, and a listener that unsubscribes during dispatch.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/BehavioralDemo.java
 *   java -cp out BehavioralDemo
 */
public class BehavioralDemo {

    public static void main(String[] args) {
        section("Observer: one event, many independent reactions");
        observerHappyPath();

        section("Observer failure mode 1: one throwing listener starves the rest");
        observerThrowingListener();

        section("Observer failure mode 2: unsubscribing during dispatch");
        observerConcurrentModification();

        section("Command: behaviour as an object, which makes undo possible");
        command();

        section("Chain of Responsibility: each handler decides to handle or pass along");
        chainOfResponsibility();

        section("Template Method: the skeleton is fixed, the steps are not");
        templateMethod();
    }

    // ------------------------------------------------ Observer

    interface OrderListener {
        void onOrderPlaced(String orderId);
    }

    static final class OrderPublisher {
        private final List<OrderListener> listeners = new ArrayList<>();

        void subscribe(OrderListener listener) {
            listeners.add(listener);
        }

        void unsubscribe(OrderListener listener) {
            listeners.remove(listener);
        }

        /** Naive dispatch: no isolation, no defensive copy. */
        void publish(String orderId) {
            for (OrderListener listener : listeners) {
                listener.onOrderPlaced(orderId);
            }
        }

        /** Hardened dispatch: failures are contained, iteration is over a snapshot. */
        List<String> publishIsolated(String orderId) {
            List<String> failures = new ArrayList<>();
            for (OrderListener listener : List.copyOf(listeners)) {
                try {
                    listener.onOrderPlaced(orderId);
                } catch (RuntimeException e) {
                    failures.add(listener.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
            return failures;
        }
    }

    static final class EmailListener implements OrderListener {
        @Override
        public void onOrderPlaced(String orderId) {
            System.out.println("  EmailListener: confirmation queued for " + orderId);
        }
    }

    static final class AnalyticsListener implements OrderListener {
        @Override
        public void onOrderPlaced(String orderId) {
            System.out.println("  AnalyticsListener: order_placed recorded for " + orderId);
        }
    }

    static final class BrokenListener implements OrderListener {
        @Override
        public void onOrderPlaced(String orderId) {
            throw new IllegalStateException("downstream webhook endpoint is down");
        }
    }

    private static void observerHappyPath() {
        OrderPublisher publisher = new OrderPublisher();
        publisher.subscribe(new EmailListener());
        publisher.subscribe(new AnalyticsListener());
        publisher.publish("ORD-1001");
        System.out.println("The publisher does not know what either listener does.");
    }

    private static void observerThrowingListener() {
        OrderPublisher naive = new OrderPublisher();
        naive.subscribe(new EmailListener());
        naive.subscribe(new BrokenListener());
        naive.subscribe(new AnalyticsListener());

        System.out.println("Naive publish():");
        try {
            naive.publish("ORD-1002");
        } catch (RuntimeException e) {
            System.out.println("  propagated out of publish(): " + e.getMessage());
        }
        System.out.println("  AnalyticsListener never ran -- registration order silently decided");
        System.out.println("  which subscribers survive an unrelated failure.");

        System.out.println();
        System.out.println("Hardened publishIsolated():");
        List<String> failures = naive.publishIsolated("ORD-1003");
        System.out.println("  contained failures: " + failures);
    }

    private static void observerConcurrentModification() {
        OrderPublisher publisher = new OrderPublisher();
        publisher.subscribe(new EmailListener());

        OrderListener oneShot = new OrderListener() {
            @Override
            public void onOrderPlaced(String orderId) {
                System.out.println("  one-shot listener fired for " + orderId + ", unsubscribing now");
                publisher.unsubscribe(this);
            }
        };
        publisher.subscribe(oneShot);
        publisher.subscribe(new AnalyticsListener());

        try {
            publisher.publish("ORD-1004");
            System.out.println("  publish() returned with NO exception at all.");
            System.out.println("  AnalyticsListener was third in the list and never ran: removing the");
            System.out.println("  second-of-three element left the iterator's cursor equal to the new");
            System.out.println("  size, so hasNext() returned false before the modCount check could fire.");
            System.out.println("  A silently skipped subscriber is strictly worse than the");
            System.out.println("  ConcurrentModificationException most people expect here.");
        } catch (RuntimeException e) {
            System.out.println("  " + e.getClass().getSimpleName()
                    + " thrown from the dispatch loop (message: " + e.getMessage() + ")");
        }

        System.out.println("Same scenario with CopyOnWriteArrayList as the listener store:");
        List<OrderListener> cow = new CopyOnWriteArrayList<>();
        cow.add(new EmailListener());
        cow.add(new AnalyticsListener());
        for (OrderListener l : cow) {
            l.onOrderPlaced("ORD-1005");
            cow.remove(l); // legal: iteration is over an immutable snapshot
        }
        System.out.println("  no exception; listeners remaining afterwards: " + cow.size());
    }

    // ------------------------------------------------ Command

    interface Command {
        void execute();

        void undo();

        String name();
    }

    static final class Document {
        private final StringBuilder text = new StringBuilder();

        @Override
        public String toString() {
            return "\"" + text + "\"";
        }
    }

    static final class AppendText implements Command {
        private final Document document;
        private final String fragment;

        AppendText(Document document, String fragment) {
            this.document = document;
            this.fragment = fragment;
        }

        @Override
        public void execute() {
            document.text.append(fragment);
        }

        @Override
        public void undo() {
            document.text.setLength(document.text.length() - fragment.length());
        }

        @Override
        public String name() {
            return "AppendText(\"" + fragment + "\")";
        }
    }

    static final class CommandHistory {
        private final Deque<Command> done = new ArrayDeque<>();

        void run(Command command) {
            command.execute();
            done.push(command);
        }

        Optional<String> undoLast() {
            if (done.isEmpty()) {
                return Optional.empty();
            }
            Command command = done.pop();
            command.undo();
            return Optional.of(command.name());
        }
    }

    private static void command() {
        Document document = new Document();
        CommandHistory history = new CommandHistory();

        history.run(new AppendText(document, "Hello"));
        history.run(new AppendText(document, ", world"));
        history.run(new AppendText(document, "!!!"));
        System.out.println("After three commands: " + document);

        System.out.println("undo -> " + history.undoLast().orElse("(nothing)") + "  document: " + document);
        System.out.println("undo -> " + history.undoLast().orElse("(nothing)") + "  document: " + document);
        System.out.println("undo -> " + history.undoLast().orElse("(nothing)") + "  document: " + document);
        System.out.println("undo -> " + history.undoLast().orElse("(nothing)") + "  document: " + document);
        System.out.println("Undo is possible only because each action is a first-class object that");
        System.out.println("captured everything needed to reverse itself.");
    }

    // ------------------------------------------------ Chain of Responsibility

    record Request(String path, String apiKey, int bodyBytes) {
    }

    interface Handler {
        /** Returns a rejection reason, or empty to pass the request along. */
        Optional<String> handle(Request request);
    }

    static final class AuthHandler implements Handler {
        @Override
        public Optional<String> handle(Request request) {
            return request.apiKey() == null ? Optional.of("401 missing API key") : Optional.empty();
        }
    }

    static final class SizeLimitHandler implements Handler {
        @Override
        public Optional<String> handle(Request request) {
            return request.bodyBytes() > 1_000
                    ? Optional.of("413 body too large (" + request.bodyBytes() + " bytes)")
                    : Optional.empty();
        }
    }

    static final class RouteHandler implements Handler {
        @Override
        public Optional<String> handle(Request request) {
            return request.path().startsWith("/api/")
                    ? Optional.empty()
                    : Optional.of("404 no route for " + request.path());
        }
    }

    private static String runChain(List<Handler> chain, Request request) {
        for (Handler handler : chain) {
            Optional<String> rejection = handler.handle(request);
            if (rejection.isPresent()) {
                return handler.getClass().getSimpleName() + " stopped it: " + rejection.get();
            }
        }
        return "200 handled " + request.path();
    }

    private static void chainOfResponsibility() {
        List<Handler> chain = List.of(new AuthHandler(), new SizeLimitHandler(), new RouteHandler());

        System.out.println(runChain(chain, new Request("/api/orders", "key-123", 200)));
        System.out.println(runChain(chain, new Request("/api/orders", null, 200)));
        System.out.println(runChain(chain, new Request("/api/orders", "key-123", 5_000)));
        System.out.println(runChain(chain, new Request("/admin", "key-123", 10)));
        System.out.println("Order is the whole design: auth runs before size, so an unauthenticated");
        System.out.println("oversized request is rejected as 401, never 413.");
    }

    // ------------------------------------------------ Template Method

    abstract static class ReportJob {
        /** final: subclasses customise steps, never the sequence. */
        final String run() {
            String raw = fetch();
            String transformed = transform(raw);
            return publish(transformed);
        }

        abstract String fetch();

        /** Overridable with a sensible default -- a hook, not a requirement. */
        String transform(String raw) {
            return raw.trim();
        }

        abstract String publish(String payload);
    }

    static final class DailySalesReport extends ReportJob {
        @Override
        String fetch() {
            return "  sales=1500,refunds=30  ";
        }

        @Override
        String publish(String payload) {
            return "emailed [" + payload + "]";
        }
    }

    static final class ComplianceExport extends ReportJob {
        @Override
        String fetch() {
            return "  pii=redacted,rows=42  ";
        }

        @Override
        String transform(String raw) {
            return raw.trim().toUpperCase();
        }

        @Override
        String publish(String payload) {
            return "uploaded to SFTP [" + payload + "]";
        }
    }

    private static void templateMethod() {
        System.out.println("DailySalesReport.run()  -> " + new DailySalesReport().run());
        System.out.println("ComplianceExport.run()  -> " + new ComplianceExport().run());
        System.out.println("Both ran fetch -> transform -> publish in that order, enforced by a final method.");
        System.out.println("ComplianceExport changed one step; it could not reorder or skip any.");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
