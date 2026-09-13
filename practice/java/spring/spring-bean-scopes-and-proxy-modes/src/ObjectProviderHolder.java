import org.springframework.beans.factory.ObjectProvider;

public class ObjectProviderHolder {

    // No proxy involved -- ObjectProvider defers resolution to call time, so
    // getObject() fetches a fresh prototype instance from the container on every
    // single call, without needing a scoped proxy at all.
    private final ObjectProvider<Greeter> greeterProvider;

    public ObjectProviderHolder(ObjectProvider<Greeter> greeterProvider) {
        this.greeterProvider = greeterProvider;
    }

    public String greet() {
        return greeterProvider.getObject().greet();
    }
}
