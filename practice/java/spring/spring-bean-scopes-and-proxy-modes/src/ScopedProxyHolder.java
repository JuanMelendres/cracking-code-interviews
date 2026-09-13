public class ScopedProxyHolder {

    // What's actually injected here is a CGLIB scoped-proxy, not a real Greeter --
    // every method call is re-dispatched to a freshly resolved prototype instance
    // from the container, instead of a single instance captured at wiring time.
    private final Greeter greeter;

    public ScopedProxyHolder(Greeter greeter) {
        this.greeter = greeter;
    }

    public String greet() {
        return greeter.greet();
    }
}
