import java.util.concurrent.atomic.AtomicInteger;

public class Greeter {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private final int id;

    public Greeter() {
        this.id = COUNTER.incrementAndGet();
    }

    public int getId() {
        return id;
    }

    public String greet() {
        return "Greeter#" + id;
    }
}
