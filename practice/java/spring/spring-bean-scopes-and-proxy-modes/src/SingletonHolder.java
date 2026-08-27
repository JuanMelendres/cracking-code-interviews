public class SingletonHolder {

    // Injected ONCE, at construction time -- this is the bug. Whatever Greeter
    // instance the container hands over here is held forever, regardless of
    // Greeter's own declared scope.
    private final Greeter greeter;

    public SingletonHolder(Greeter greeter) {
        this.greeter = greeter;
    }

    public String greet() {
        return greeter.greet();
    }
}
