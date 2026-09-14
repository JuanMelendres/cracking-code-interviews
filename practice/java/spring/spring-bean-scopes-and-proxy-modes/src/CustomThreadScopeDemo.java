import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CustomThreadScopeDemo {

    public static void main(String[] args) throws InterruptedException {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("=== Same thread, two calls to a 'thread'-scoped bean ===");
            Greeter t1a = ctx.getBean("threadScopedGreeter", Greeter.class);
            Greeter t1b = ctx.getBean("threadScopedGreeter", Greeter.class);
            System.out.println("t1a == t1b: " + (t1a == t1b) + "  (" + t1a.greet() + " vs " + t1b.greet() + ")");

            System.out.println();
            System.out.println("=== A different thread, same bean name ===");
            Greeter[] fromOtherThread = new Greeter[1];
            Thread worker = new Thread(() ->
                    fromOtherThread[0] = ctx.getBean("threadScopedGreeter", Greeter.class));
            worker.start();
            worker.join();
            System.out.println("t1a == fromOtherThread: " + (t1a == fromOtherThread[0])
                    + "  (" + t1a.greet() + " vs " + fromOtherThread[0].greet() + ")");
        }
    }
}
