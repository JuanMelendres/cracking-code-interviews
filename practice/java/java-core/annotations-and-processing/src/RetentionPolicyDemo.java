import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Real, executed (and real, disassembled) proof of the three RetentionPolicy
 * values' actual, different lifetimes: SOURCE never reaches the .class file
 * at all; CLASS reaches the .class file but is invisible via reflection;
 * RUNTIME is visible via reflection. Verified with real javap output, not
 * assumed from the Javadoc description alone.
 */
public class RetentionPolicyDemo {

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface SourceOnly {
    }

    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    @interface ClassOnly {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface RuntimeVisible {
    }

    static class Annotated {
        @SourceOnly
        void sourceOnlyMethod() {
        }

        @ClassOnly
        void classOnlyMethod() {
        }

        @RuntimeVisible
        void runtimeVisibleMethod() {
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== Real reflective visibility at runtime ==");
        for (String methodName : new String[]{"sourceOnlyMethod", "classOnlyMethod", "runtimeVisibleMethod"}) {
            Method m = Annotated.class.getDeclaredMethod(methodName);
            System.out.println(methodName + ": getAnnotations().length = " + m.getAnnotations().length
                    + (m.getAnnotations().length == 0
                    ? "  <-- invisible via reflection at runtime"
                    : "  <-- REAL: " + m.getAnnotations()[0]));
        }

        System.out.println("\n== Real bytecode disassembly, captured separately via `javap -v -p` ==");
        System.out.println("See README.md for the exact real disassembly output: sourceOnlyMethod has NO annotation");
        System.out.println("attribute in the .class file at all; classOnlyMethod's annotation is real and present, but");
        System.out.println("in RuntimeInvisibleAnnotations (which reflection never reads); runtimeVisibleMethod's");
        System.out.println("annotation is in RuntimeVisibleAnnotations, exactly matching the reflective results above.");
    }
}
