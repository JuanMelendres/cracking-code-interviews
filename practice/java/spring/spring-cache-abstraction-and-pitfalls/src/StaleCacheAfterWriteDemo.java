import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Real proof of the classic cache-invalidation pitfall: a real underlying write
 * that has no corresponding real cache eviction leaves every subsequent cached read
 * silently stale, with no error and no warning -- the wrong value is simply served,
 * indefinitely, until the cache entry happens to expire on its own (this demo's
 * cache has no TTL configured at all, so it never would).
 */
public final class StaleCacheAfterWriteDemo {

    public static void main(String[] args) {
        System.out.println("=== Scenario A: UNSAFE -- update path has no @CacheEvict ===");
        runScenario(false);

        System.out.println();
        System.out.println("=== Scenario B: SAFE -- update path has a real @CacheEvict ===");
        runScenario(true);
    }

    private static void runScenario(boolean useEviction) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ProductService service = ctx.getBean(ProductService.class);

            System.out.println("Real initial read: " + service.findById("sku-1"));

            if (useEviction) {
                service.updateStockWithEviction("sku-1", 5);
            } else {
                service.updateStockWithoutEviction("sku-1", 5);
            }
            System.out.println("Real underlying stock updated to 5.");

            System.out.println("Real read after the update: " + service.findById("sku-1"));
        }
    }
}
