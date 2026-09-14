/**
 * Real, executed proof of the classloader hierarchy and the parent-first
 * delegation model: bootstrap classloader is reported as null (it's
 * implemented in native code, not a real Java object), and each
 * classloader's getParent() chain is real and inspectable.
 */
public class HierarchyAndDelegationDemo {

    public static void main(String[] args) {
        System.out.println("== Real classloader hierarchy for three different classes ==");

        ClassLoader jdkLoader = String.class.getClassLoader();
        System.out.println("String.class.getClassLoader():        " + jdkLoader
                + (jdkLoader == null ? "  <-- REAL null: bootstrap classloader is native code, not a Java object" : ""));

        ClassLoader appLoader = HierarchyAndDelegationDemo.class.getClassLoader();
        System.out.println("This class's getClassLoader():         " + appLoader);

        System.out.println("\n== Real parent delegation chain, walked directly ==");
        ClassLoader current = appLoader;
        int depth = 0;
        while (current != null) {
            System.out.println("  depth " + depth + ": " + current);
            current = current.getParent();
            depth++;
        }
        System.out.println("  depth " + depth + ": null (bootstrap -- the real root of every delegation chain)");

        System.out.println("\n== Real proof: the JVM's own delegation model always asks the parent FIRST ==");
        System.out.println("Loading java.lang.String via this class's own app classloader still returns"
                + " the SAME Class object the bootstrap loader already loaded:");
        try {
            Class<?> viaAppLoader = Class.forName("java.lang.String", true, appLoader);
            System.out.println("  Class.forName(\"java.lang.String\", true, appLoader) == String.class: "
                    + (viaAppLoader == String.class)
                    + "  <-- real proof of parent-first delegation: the app loader delegated up to bootstrap"
                    + " instead of trying to define its own java.lang.String");
        } catch (ClassNotFoundException e) {
            System.out.println("  unexpected: " + e);
        }
    }
}
