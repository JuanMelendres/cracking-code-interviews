import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

/**
 * Real, executed cycle detection at the module ("slice") level -- the other classic
 * modular-monolith enforcement mechanic alongside layer/boundary rules. A real cycle
 * is present in this codebase: shipping depends on orders (via orders.api, its
 * normal, correct usage), and orders.internal.OrderCreatedNotifier depends back on
 * shipping -- a real, complete cycle ArchUnit's slice-cycle check genuinely detects.
 */
public class CycleCheckDemo {
    public static void main(String[] args) {
        JavaClasses importedClasses = new ClassFileImporter().importPath("out");

        System.out.println("=== Checking: top-level packages must be free of cycles ===");
        try {
            SlicesRuleDefinition.slices().matching("(*)..")
                    .should().beFreeOfCycles()
                    .check(importedClasses);
            System.out.println("PASS");
        } catch (AssertionError e) {
            System.out.println("FAIL");
            System.out.println(e.getMessage());
        }
    }
}
