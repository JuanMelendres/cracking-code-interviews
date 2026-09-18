import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Real proof of three things about Spring's circular-dependency handling:
 *  1. Two beans requiring each other via CONSTRUCTOR injection is a real,
 *     unresolvable startup failure -- there is no valid construction order.
 *  2. The identical cycle via FIELD injection resolves silently, because
 *     Spring can construct each bean (no-arg constructor) before wiring its
 *     fields -- a real, different outcome for what looks like "the same"
 *     circular dependency.
 *  3. @Lazy on ONE side of a constructor-injected cycle breaks it: Spring
 *     injects a lazy proxy instead of the real bean, deferring the real
 *     lookup until first use, after both beans already exist.
 */
public class CircularDependencyDemo {

    static class ServiceA {
        final ServiceB b;
        ServiceA(ServiceB b) { this.b = b; }
    }

    static class ServiceB {
        final ServiceA a;
        ServiceB(ServiceA a) { this.a = a; }
    }

    @Configuration
    static class ConstructorCycleConfig {
        @Bean ServiceA serviceA(ServiceB b) { return new ServiceA(b); }
        @Bean ServiceB serviceB(ServiceA a) { return new ServiceB(a); }
    }

    static class FieldA {
        @Autowired FieldB b;
    }

    static class FieldB {
        @Autowired FieldA a;
    }

    @Configuration
    static class FieldCycleConfig {
        @Bean FieldA fieldA() { return new FieldA(); }
        @Bean FieldB fieldB() { return new FieldB(); }
    }

    static class LazyA {
        final LazyB b;
        LazyA(@Lazy LazyB b) { this.b = b; }
    }

    static class LazyB {
        final LazyA a;
        LazyB(LazyA a) { this.a = a; }
        LazyA getA() { return a; }
    }

    @Configuration
    static class LazyCycleConfig {
        @Bean LazyA lazyA(@Lazy LazyB b) { return new LazyA(b); }
        @Bean LazyB lazyB(LazyA a) { return new LazyB(a); }
    }

    public static void main(String[] args) {
        System.out.println("== Constructor-injected cycle: real, unresolvable startup failure ==");
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ConstructorCycleConfig.class)) {
            System.out.println("Started without error (unexpected)");
        } catch (BeanCreationException e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            System.out.println("Real failure: " + e.getClass().getSimpleName());
            System.out.println("Root cause: " + root.getClass().getSimpleName() + ": " + root.getMessage());
        }

        System.out.println();
        System.out.println("== IDENTICAL cycle via FIELD injection: resolves silently, no error ==");
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(FieldCycleConfig.class)) {
            FieldA a = ctx.getBean(FieldA.class);
            FieldB b = ctx.getBean(FieldB.class);
            System.out.println("Started successfully. a.b == real FieldB bean? " + (a.b == b));
            System.out.println("b.a == real FieldA bean? " + (b.a == a));
            System.out.println("Why: Spring calls each no-arg constructor FIRST (objects now exist),");
            System.out.println("     then wires @Autowired fields afterward -- no ordering problem exists.");
        }

        System.out.println();
        System.out.println("== Constructor cycle FIXED with @Lazy on one side ==");
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(LazyCycleConfig.class)) {
            LazyA a = ctx.getBean(LazyA.class);
            LazyB b = ctx.getBean(LazyB.class);
            System.out.println("Started successfully.");
            System.out.println("a.b is a real CGLIB lazy proxy, not the literal LazyB instance yet resolved: "
                    + a.b.getClass().getName().contains("SpringCGLIB"));
            System.out.println("DIRECT FIELD access through the proxy, a.b.a == a ?       " + (a.b.a == a)
                    + "  <-- proxies only intercept METHOD calls, not field reads");
            System.out.println("METHOD access through the proxy, a.b.getA() == a ?        " + (a.b.getA() == a)
                    + "  <-- correctly delegates to the real target");
        }
    }
}
