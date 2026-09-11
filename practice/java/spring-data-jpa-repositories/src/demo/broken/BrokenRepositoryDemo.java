package demo.broken;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BrokenRepositoryDemo {

    public static void main(String[] args) {
        System.out.println("Booting a Spring context with a repository method referencing a");
        System.out.println("property that does not exist on Order (findByTotlAmountGreaterThan)...");
        System.out.println();
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BrokenAppConfig.class)) {
            System.out.println("UNEXPECTED: context started with no error.");
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            System.out.println();
            System.out.println("=== Context refresh failed, as expected ===");
            System.out.println("Root cause class:   " + root.getClass().getName());
            System.out.println("Root cause message: " + root.getMessage());
            System.out.println();
            System.out.println("This proves the property path in a derived query method is resolved");
            System.out.println("against the real JPA metamodel eagerly, at ApplicationContext startup --");
            System.out.println("not lazily on first invocation of the repository method.");
        }
    }
}
