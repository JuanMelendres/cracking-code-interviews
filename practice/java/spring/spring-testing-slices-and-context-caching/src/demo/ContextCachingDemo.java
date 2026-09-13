package demo;

import org.springframework.test.context.TestContextManager;

// Drives Spring's real TestContext framework directly -- the exact engine
// SpringExtension delegates to for every @ExtendWith(SpringExtension.class) test
// -- in guaranteed sequential order, so the context-caching effect is
// deterministic and doesn't depend on a JUnit runner's class-execution order.
public class ContextCachingDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== TestClassA: first class to use this @ContextConfiguration ===");
        new TestContextManager(TestClassA.class).getTestContext().getApplicationContext();
        System.out.println("Real contexts created so far: " + CountingConfig.CONTEXTS_CREATED.get());

        System.out.println();
        System.out.println("=== TestClassB: identical @ContextConfiguration ===");
        new TestContextManager(TestClassB.class).getTestContext().getApplicationContext();
        System.out.println("Real contexts created so far: " + CountingConfig.CONTEXTS_CREATED.get()
                + " (expect still 1 -- context reused from the cache)");

        System.out.println();
        System.out.println("=== TestClassC: identical config, but annotated @DirtiesContext ===");
        TestContextManager tcmC = new TestContextManager(TestClassC.class);
        tcmC.getTestContext().getApplicationContext();
        tcmC.afterTestClass(); // this is what actually evicts the cached context
        System.out.println("Real contexts created so far: " + CountingConfig.CONTEXTS_CREATED.get()
                + " (still 1 -- C reused the cache while running, then dirtied it on the way out)");

        System.out.println();
        System.out.println("=== TestClassD: identical config again, run AFTER C dirtied the cache ===");
        new TestContextManager(TestClassD.class).getTestContext().getApplicationContext();
        System.out.println("Real contexts created so far: " + CountingConfig.CONTEXTS_CREATED.get()
                + " (expect 2 -- a real, fresh context was rebuilt)");
    }
}
