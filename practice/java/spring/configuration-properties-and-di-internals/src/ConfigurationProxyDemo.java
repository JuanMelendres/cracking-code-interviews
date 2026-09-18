import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Real proof of why calling one @Bean method from inside another, on the
 * SAME @Configuration class, does not create a second object -- and why it
 * genuinely does when proxyBeanMethods is turned off.
 */
public class ConfigurationProxyDemo {

    static class Engine {
        private static int instancesCreated = 0;
        final int id;
        Engine() { id = ++instancesCreated; }
    }

    static class Car {
        final Engine engine;
        Car(Engine engine) { this.engine = engine; }
    }

    @Configuration // proxyBeanMethods=true is the default
    static class ProxiedConfig {
        @Bean
        Engine engine() { return new Engine(); }

        @Bean
        Car car() {
            // Calling engine() directly, as a plain Java method call --
            // looks like it should build a brand-new Engine every time.
            return new Car(engine());
        }

        @Bean
        Car secondCarCallingEngineAgain() {
            return new Car(engine());
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class PlainConfig {
        @Bean
        Engine engine() { return new Engine(); }

        @Bean
        Car car() {
            return new Car(engine());
        }

        @Bean
        Car secondCarCallingEngineAgain() {
            return new Car(engine());
        }
    }

    public static void main(String[] args) {
        System.out.println("== Default @Configuration (proxyBeanMethods=true): CGLIB-proxied ==");
        Engine.instancesCreated = 0;
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ProxiedConfig.class)) {
            Engine directEngineBean = ctx.getBean(Engine.class);
            Car car1 = ctx.getBean("car", Car.class);
            Car car2 = ctx.getBean("secondCarCallingEngineAgain", Car.class);
            System.out.println("@Configuration class itself is a real CGLIB proxy: "
                    + (ProxiedConfig.class != ctx.getBean(ProxiedConfig.class).getClass()));
            System.out.println("Total real Engine instances actually constructed: " + Engine.instancesCreated
                    + "  (expected 1 -- every engine() call, even a plain Java call, returns the SAME singleton)");
            System.out.println("car1.engine == car2.engine == the real 'engine' bean? "
                    + (car1.engine == directEngineBean && car2.engine == directEngineBean));
        }

        System.out.println();
        System.out.println("== @Configuration(proxyBeanMethods = false): a real, different outcome ==");
        Engine.instancesCreated = 0;
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PlainConfig.class)) {
            Engine directEngineBean = ctx.getBean(Engine.class);
            Car car1 = ctx.getBean("car", Car.class);
            Car car2 = ctx.getBean("secondCarCallingEngineAgain", Car.class);
            System.out.println("Total real Engine instances actually constructed: " + Engine.instancesCreated
                    + "  (expected 3 -- engine() is now a PLAIN Java method call each time, no interception at all)");
            System.out.println("car1.engine == directEngineBean? " + (car1.engine == directEngineBean)
                    + "  (false -- car1 holds its OWN Engine, not the container's 'engine' singleton)");
            System.out.println("car1.engine == car2.engine? " + (car1.engine == car2.engine)
                    + "  (false -- two separate, uncoordinated Engine instances)");
        }
    }
}
