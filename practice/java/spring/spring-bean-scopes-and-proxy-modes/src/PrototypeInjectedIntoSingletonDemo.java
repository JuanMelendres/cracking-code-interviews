import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PrototypeInjectedIntoSingletonDemo {

    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            SingletonHolder buggy = ctx.getBean(SingletonHolder.class);
            ScopedProxyHolder fixedByProxy = ctx.getBean(ScopedProxyHolder.class);
            ObjectProviderHolder fixedByProvider = ctx.getBean(ObjectProviderHolder.class);

            System.out.println("=== BUGGY: prototype Greeter injected directly (by reference) into a singleton ===");
            for (int i = 1; i <= 3; i++) {
                System.out.println("call " + i + ": " + buggy.greet());
            }

            System.out.println();
            System.out.println("=== FIXED (scoped proxy, proxyMode = TARGET_CLASS): fresh prototype instance per call ===");
            for (int i = 1; i <= 3; i++) {
                System.out.println("call " + i + ": " + fixedByProxy.greet());
            }

            System.out.println();
            System.out.println("=== FIXED (ObjectProvider<Greeter>.getObject()): fresh prototype instance per call ===");
            for (int i = 1; i <= 3; i++) {
                System.out.println("call " + i + ": " + fixedByProvider.greet());
            }
        }
    }
}
