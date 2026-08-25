import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Real, executed module-boundary enforcement using ArchUnit -- not a description of
 * how it works, an actual run against actually-compiled bytecode. Two real rules,
 * checked against the identical compiled classpath: one clean module (shipping)
 * passes; one that reaches around the public API (shippinglegacy) genuinely fails,
 * with ArchUnit's own real violation report naming the exact class and dependency.
 */
public class BoundaryCheckDemo {
    public static void main(String[] args) {
        JavaClasses importedClasses = new ClassFileImporter().importPath("out");

        ArchRule shippingRule = noClasses().that().resideInAPackage("shipping")
                .should().dependOnClassesThat().resideInAnyPackage("orders.internal..")
                .because("shipping must depend on orders only through orders.api, never orders.internal");

        ArchRule shippingLegacyRule = noClasses().that().resideInAPackage("shippinglegacy")
                .should().dependOnClassesThat().resideInAnyPackage("orders.internal..")
                .because("shippinglegacy must depend on orders only through orders.api, never orders.internal");

        System.out.println("=== Checking: shipping must not depend on orders.internal ===");
        runRule(shippingRule, importedClasses);

        System.out.println();
        System.out.println("=== Checking: shippinglegacy must not depend on orders.internal ===");
        runRule(shippingLegacyRule, importedClasses);
    }

    private static void runRule(ArchRule rule, JavaClasses classes) {
        try {
            rule.check(classes);
            System.out.println("PASS");
        } catch (AssertionError e) {
            System.out.println("FAIL");
            System.out.println(e.getMessage());
        }
    }
}
