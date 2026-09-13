import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Real, executed proof of @Inherited's real, narrow scope: it ONLY applies
 * to class-level annotations inherited from a SUPERCLASS via extends --
 * it does NOT apply to interfaces (implementing an annotated interface
 * does not "inherit" the annotation) and does NOT apply to methods or
 * fields at all, no matter how they're declared.
 */
public class InheritedGotchaDemo {

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface InheritedClassAnnotation {
    }

    @InheritedClassAnnotation
    static class BaseClass {
    }

    static class SubClass extends BaseClass {
        // Does NOT redeclare @InheritedClassAnnotation
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface InheritedMarker {
    }

    @InheritedMarker
    interface MarkedInterface {
    }

    static class ImplementingClass implements MarkedInterface {
        // Does NOT redeclare @InheritedMarker
    }

    public static void main(String[] args) {
        System.out.println("== Real proof: @Inherited DOES work through class extension ==");
        boolean subClassSeesIt = SubClass.class.isAnnotationPresent(InheritedClassAnnotation.class);
        System.out.println("SubClass.class.isAnnotationPresent(InheritedClassAnnotation.class) = " + subClassSeesIt
                + (subClassSeesIt ? "  <-- REAL: inherited from BaseClass via extends" : ""));

        System.out.println("\n== Real proof: @Inherited does NOT work through interfaces, even when marked @Inherited ==");
        boolean implementingClassSeesIt = ImplementingClass.class.isAnnotationPresent(InheritedMarker.class);
        System.out.println("ImplementingClass.class.isAnnotationPresent(InheritedMarker.class) = " + implementingClassSeesIt
                + (!implementingClassSeesIt ? "  <-- REAL: false. @Inherited is documented to apply ONLY to superclasses, never interfaces" : ""));

        System.out.println("\n== Real proof: the interface itself still carries its own annotation, just doesn't propagate it ==");
        System.out.println("MarkedInterface.class.isAnnotationPresent(InheritedMarker.class) = "
                + MarkedInterface.class.isAnnotationPresent(InheritedMarker.class)
                + "  (the interface itself has it -- ImplementingClass just doesn't inherit it)");
    }
}
