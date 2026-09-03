# Spring Cache abstraction and pitfalls (T-514) — runnable verification

Real, executed output backing
[`syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md`](../../../../syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md)
(T-514). Real Spring Framework 6.1.14, a real CGLIB proxy, real `@Cacheable`/`@CacheEvict`
behavior, and two real, honestly-disclosed bugs hit while building these demos.

## Files

- `AppConfig.java` — `@EnableCaching` plus a real `ConcurrentMapCacheManager`.
- `Product.java`, `ProductService.java` — the cached bean, with a real underlying
  lookup counter proving whether the cache actually fired.
- `SelfInvocationBypassDemo.java` — the identical proxy-based-AOP root cause already
  proven for `@Transactional`, here for `@Cacheable`.
- `CachePoisoningViaMutationDemo.java` — a real, silent cache-corruption bug from a
  mutable cached return value.
- `StaleCacheAfterWriteDemo.java` — a real, silent stale-read bug from a missing
  `@CacheEvict`.

## Run

```bash
cd practice/java/spring/spring-cache-abstraction-and-pitfalls
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/*.java
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" SelfInvocationBypassDemo
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" CachePoisoningViaMutationDemo
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" StaleCacheAfterWriteDemo
```

**The `-parameters` flag is not optional here** — see the honest discovery below.

## Real observed output (last full run, Spring Framework 6.1.14)

### 1. Self-invocation bypasses `@Cacheable` — exactly like `@Transactional`

```
=== Calling findById() externally, through the real Spring proxy, 3 times ===
Real underlying lookup count: 1 (expected 1 -- cached after the first real call)

=== Calling the SAME logic via self-invocation (this.findById(...)), 3 times ===
Real underlying lookup count: 4 (expected 4 = 1 + 3 -- self-invocation never went through the proxy, so @Cacheable never ran)
```

### 2. Cache poisoning via a mutable cached value

```
First real call, real lookup count=1, tags=[electronics, bestseller]

=== Caller mutates the list it got back -- a completely ordinary, easy-to-miss thing to do ===
Second call (should be served from cache, real lookup count should stay 1): 1
Tags returned to a COMPLETELY UNRELATED caller: [electronics, bestseller, CORRUPTED-BY-CALLER]
Same object reference as the first call: true
```

Spring's cache abstraction stores the exact object reference returned — it never
defensively copies it. One caller's mutation is now permanently visible to every
other caller that hits the same cache entry.

### 3. Stale cache after a write with no eviction

```
=== Scenario A: UNSAFE -- update path has no @CacheEvict ===
Real initial read: Product{sku-1, Widget, stock=100}
Real underlying stock updated to 5.
Real read after the update: Product{sku-1, Widget, stock=100}

=== Scenario B: SAFE -- update path has a real @CacheEvict ===
Real initial read: Product{sku-1, Widget, stock=100}
Real underlying stock updated to 5.
Real read after the update: Product{sku-1, Widget, stock=5}
```

## Two real, honest discoveries made while building this pack

1. **Reading a field directly on a CGLIB-proxied bean reads the proxy's own
   uninitialized shell, not the real target.** Spring's CGLIB proxy for a
   no-interface class is a genuinely separate object, instantiated via Objenesis
   (bypassing the real constructor). The first version of `ProductService` exposed
   its lookup counter as a public field; reading it through the proxy reference threw
   a real `NullPointerException`. The fix was a real accessor method — `getRealLookupCount()`
   — which works correctly because method calls, unlike field reads, are actually
   intercepted and delegated to the real target.
2. **`@CacheEvict(key = "#id")` throws a real `IllegalArgumentException` without the
   `-parameters` compiler flag.** SpEL resolves `#id` by looking up the parameter's
   *name* in the compiled bytecode, which `javac` only preserves with `-parameters`.
   Without it, Spring cannot tell which parameter `#id` refers to, and fails loudly
   rather than guessing.

Both are disclosed here rather than smoothed over — they're real, common gotchas a
team adopting Spring's cache abstraction runs into on day one, not edge cases
invented for this demo.

## What this does and does not prove

This is real Spring Framework behavior against an in-memory `ConcurrentMapCacheManager`
— no real distributed cache (Redis, Hazelcast) with its own network-latency and
serialization concerns is exercised here. What transfers directly to any real cache
backend is the underlying mechanism this demo measures: proxy-based AOP's
self-invocation limitation, the cache storing whatever reference it's given with no
defensive copy, and the requirement that a write path explicitly evict what it
invalidates — none of which are backend-specific.
