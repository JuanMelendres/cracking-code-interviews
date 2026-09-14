import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A real, minimal fitness function -- an automated, continuously re-runnable check on
 * one architectural characteristic (here, efferent coupling: how many distinct
 * collaborator types a class directly depends on), the mechanism evolutionary
 * architecture is built around. No third-party library, no ArchUnit, no build-tool
 * integration -- just real {@code java.lang.reflect} inspection of a class's declared
 * fields, which is enough to demonstrate the actual property: the check is automated,
 * repeatable, and gives an exact number, not a subjective code-review opinion.
 */
public final class CouplingFitnessFunction {

    static int measureEfferentCoupling(Class<?> targetClass) {
        Set<String> collaboratorTypes = new LinkedHashSet<>();
        for (Field field : targetClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            Class<?> fieldType = field.getType();
            if (fieldType.isPrimitive()) continue;
            if (fieldType.getName().startsWith("java.")) continue;
            collaboratorTypes.add(fieldType.getSimpleName());
        }
        return collaboratorTypes.size();
    }

    static boolean checkThreshold(Class<?> targetClass, int maxAllowedCoupling) {
        int actual = measureEfferentCoupling(targetClass);
        boolean passed = actual <= maxAllowedCoupling;
        System.out.printf("[Fitness Function] %s: efferent coupling = %d (threshold: <= %d) -> %s%n",
                targetClass.getName(), actual, maxAllowedCoupling, passed ? "PASS" : "FAIL");
        return passed;
    }

    public static void main(String[] args) throws Exception {
        int threshold = 5;

        System.out.println("=== Running coupling fitness function against before.OrderProcessor ===");
        Class<?> before = Class.forName("before.OrderProcessor");
        boolean beforePassed = checkThreshold(before, threshold);

        System.out.println();
        System.out.println("=== Running coupling fitness function against after.OrderProcessor ===");
        Class<?> after = Class.forName("after.OrderProcessor");
        boolean afterPassed = checkThreshold(after, threshold);

        System.out.println();
        System.out.println("Build gate result: before=" + (beforePassed ? "PASS" : "FAIL")
                + ", after=" + (afterPassed ? "PASS" : "FAIL"));

        if (!beforePassed) {
            System.out.println("A CI pipeline wiring this fitness function into the build would have"
                    + " REJECTED before.OrderProcessor at merge time, not discovered its coupling"
                    + " informally during an unrelated incident months later.");
        }
    }
}
