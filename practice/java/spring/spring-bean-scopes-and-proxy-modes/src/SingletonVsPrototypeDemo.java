import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SingletonVsPrototypeDemo {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("=== Singleton-scoped SingletonHolder bean, fetched twice from the container ===");
            SingletonHolder holder1 = ctx.getBean(SingletonHolder.class);
            SingletonHolder holder2 = ctx.getBean(SingletonHolder.class);
            System.out.println("holder1 == holder2: " + (holder1 == holder2));

            System.out.println();
            System.out.println("=== Prototype-scoped 'greeter' bean, fetched twice directly from the container ===");
            Greeter g1 = ctx.getBean("greeter", Greeter.class);
            Greeter g2 = ctx.getBean("greeter", Greeter.class);
            System.out.println("g1 == g2: " + (g1 == g2) + "  (" + g1.greet() + " vs " + g2.greet() + ")");
        }
    }
}
