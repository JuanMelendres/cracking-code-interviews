import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * Real proof of cache poisoning via a mutable cached value. Spring's cache
 * abstraction stores whatever object reference a {@code @Cacheable} method returns
 * -- it does not defensively copy it. If a caller mutates that returned object,
 * every future call that hits the cache gets back the SAME, now-corrupted object,
 * because there was only ever one real instance shared between every caller.
 */
public final class CachePoisoningViaMutationDemo {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ProductService service = ctx.getBean(ProductService.class);

            List<String> tagsCall1 = service.findTagsById("sku-1");
            System.out.println("First real call, real lookup count=" + service.getRealLookupCount()
                    + ", tags=" + tagsCall1);

            System.out.println();
            System.out.println("=== Caller mutates the list it got back -- a completely ordinary, easy-to-miss thing to do ===");
            tagsCall1.add("CORRUPTED-BY-CALLER");

            List<String> tagsCall2 = service.findTagsById("sku-1");
            System.out.println("Second call (should be served from cache, real lookup count should stay 1): "
                    + service.getRealLookupCount());
            System.out.println("Tags returned to a COMPLETELY UNRELATED caller: " + tagsCall2);
            System.out.println("Same object reference as the first call: " + (tagsCall1 == tagsCall2));
        }
    }
}
