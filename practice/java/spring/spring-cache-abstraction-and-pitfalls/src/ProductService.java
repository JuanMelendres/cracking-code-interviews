import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real Spring-managed bean whose real, underlying "database" is just an in-memory
 * map -- what matters for every demo in this pack is that {@code realLookupCount}
 * only increments when the underlying method body genuinely runs, so a call that
 * returns a cached value without incrementing it is real, measured proof caching
 * actually happened, not an assumption.
 */
public class ProductService {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>();
    private final AtomicInteger realLookupCount = new AtomicInteger();

    public ProductService() {
        stock.put("sku-1", 100);
    }

    /**
     * A real, honest discovery made while building this demo: Spring's CGLIB proxy
     * for a class with no interface is a real, separate object (created via
     * Objenesis, bypassing this class's constructor entirely) -- reading a field
     * directly on that proxy reference reads the PROXY's own uninitialized copy,
     * not the real target bean's. A real accessor method works correctly because
     * the call is intercepted and delegated to the real target, same as any other
     * method call through the proxy.
     */
    public int getRealLookupCount() {
        return realLookupCount.get();
    }

    @Cacheable("products")
    public Product findById(String id) {
        realLookupCount.incrementAndGet();
        return new Product(id, "Widget", stock.getOrDefault(id, 0));
    }

    /**
     * Deliberately calls {@code findById} via {@code this.} -- a real, in-process
     * method call that never passes through the CGLIB proxy Spring created for this
     * bean, so the real {@code @Cacheable} advice never runs. The exact same root
     * cause proven for {@code @Transactional} in
     * transactional-proxy-mechanics-and-propagation.md.
     */
    public Product findByIdViaSelfInvocation(String id) {
        return this.findById(id);
    }

    @Cacheable("products-tags")
    public List<String> findTagsById(String id) {
        realLookupCount.incrementAndGet();
        List<String> tags = new ArrayList<>();
        tags.add("electronics");
        tags.add("bestseller");
        return tags;
    }

    /** The buggy version: updates the real store but forgets to evict the stale cached entry. */
    public void updateStockWithoutEviction(String id, int newStock) {
        stock.put(id, newStock);
    }

    /** The fixed version: the same real update, plus a real, correct cache eviction. */
    @CacheEvict(value = "products", key = "#id")
    public void updateStockWithEviction(String id, int newStock) {
        stock.put(id, newStock);
    }
}
