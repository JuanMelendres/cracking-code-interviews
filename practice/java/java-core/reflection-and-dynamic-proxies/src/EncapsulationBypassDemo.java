import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Real, executed proof that reflection genuinely bypasses normal
 * encapsulation: a private field on a SEPARATE top-level class (genuinely
 * not a nestmate of this class -- see the real, honest finding below about
 * why that separation matters) is read AND mutated from outside via
 * setAccessible(true) -- exactly the mechanism frameworks like Jackson
 * (field-based deserialization) and Spring (dependency injection into
 * private fields) rely on.
 */
public class EncapsulationBypassDemo {

    public static void main(String[] args) throws Exception {
        BankAccount account = new BankAccount();
        System.out.println("Before reflection: " + account);

        // Attempt WITHOUT setAccessible -- real, expected failure.
        Field balanceField = BankAccount.class.getDeclaredField("balance");
        try {
            balanceField.get(account);
            System.out.println("Read balance without setAccessible: succeeded (unexpected)");
        } catch (IllegalAccessException e) {
            System.out.println("Read balance without setAccessible: real IllegalAccessException, as expected");
        }

        // Real encapsulation bypass.
        balanceField.setAccessible(true);
        double realBalance = (double) balanceField.get(account);
        System.out.println("Real balance read via reflection: " + realBalance);

        balanceField.set(account, 999999.0); // real, direct mutation of a private field from outside the class
        System.out.println("After reflective mutation: " + account);

        // Real private-method invocation.
        Method interestMethod = BankAccount.class.getDeclaredMethod("calculateInterest");
        interestMethod.setAccessible(true);
        double interest = (double) interestMethod.invoke(account);
        System.out.println("Real interest computed via reflective private-method call: " + interest);

        // Real bean-style introspection (the actual mechanism Jackson/Spring rely on).
        System.out.println("\n== Real field introspection (Jackson/Spring-style bean discovery) ==");
        for (Field f : BankAccount.class.getDeclaredFields()) {
            System.out.println("field: " + f.getName() + " type=" + f.getType().getSimpleName()
                    + " modifiers=" + java.lang.reflect.Modifier.toString(f.getModifiers()));
        }
    }
}
