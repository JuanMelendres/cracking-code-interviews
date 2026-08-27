import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Real proof that {@code @Cacheable} is bypassed on self-invocation -- the identical
 * proxy-based-AOP root cause already proven for {@code @Transactional} in
 * transactional-proxy-mechanics-and-propagation.md, here reproduced for the cache
 * abstraction specifically. Calling a cached method externally (through Spring's
 * real proxy) caches it; calling the exact same method from inside the bean
 * (self-invocation) never does, no matter how many times it's called.
 */
public final class SelfInvocationBypassDemo {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ProductService service = ctx.getBean(ProductService.class);

            System.out.println("=== Calling findById() externally, through the real Spring proxy, 3 times ===");
            for (int i = 0; i < 3; i++) {
                service.findById("sku-1");
            }
            System.out.println("Real underlying lookup count: " + service.getRealLookupCount()
                    + " (expected 1 -- cached after the first real call)");

            System.out.println();
            System.out.println("=== Calling the SAME logic via self-invocation (this.findById(...)), 3 times ===");
            for (int i = 0; i < 3; i++) {
                service.findByIdViaSelfInvocation("sku-1");
            }
            System.out.println("Real underlying lookup count: " + service.getRealLookupCount()
                    + " (expected 4 = 1 + 3 -- self-invocation never went through the proxy, so @Cacheable never ran)");
        }
    }
}
