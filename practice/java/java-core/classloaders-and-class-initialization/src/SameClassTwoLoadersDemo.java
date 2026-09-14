import java.io.IOException;
import java.io.InputStream;

/**
 * Real, executed proof of the classic classloader identity gotcha: a class
 * is really identified by (fully-qualified name, defining ClassLoader) as
 * a PAIR, not by name alone. Loading the identical .class bytecode for
 * "Widget" through two different classloaders produces two genuinely
 * distinct Class objects -- real, verified via == and via a real
 * ClassCastException when trying to treat one as the other.
 */
public class SameClassTwoLoadersDemo {

    // A custom classloader that defines its OWN copy of Widget instead of
    // delegating to the parent for it -- deliberately breaking the normal
    // parent-first delegation model for this one class, to make the point.
    static class IsolatedClassLoader extends ClassLoader {
        IsolatedClassLoader() {
            super(null); // no parent at all -- forces this loader to define everything itself
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.equals("Widget")) {
                try {
                    byte[] bytes = readClassBytes(name);
                    return defineClass(name, bytes, 0, bytes.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name, e);
                }
            }
            // Delegate everything else (java.lang.Object, etc.) to the bootstrap loader.
            return super.loadClass(name, resolve);
        }

        byte[] readClassBytes(String name) throws IOException {
            String resourcePath = name.replace('.', '/') + ".class";
            try (InputStream in = SameClassTwoLoadersDemo.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (in == null) throw new IOException("class bytes not found: " + resourcePath);
                return in.readAllBytes();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Widget systemWidget = new Widget(); // loaded by the normal app classloader
        System.out.println("System-loaded Widget: " + systemWidget.label()
                + ", classloader=" + Widget.class.getClassLoader());

        IsolatedClassLoader isolated = new IsolatedClassLoader();
        Class<?> isolatedWidgetClass = isolated.loadClass("Widget");
        Object isolatedWidget = isolatedWidgetClass.getDeclaredConstructor().newInstance();
        System.out.println("Isolated-loaded Widget's Class: " + isolatedWidgetClass
                + ", classloader=" + isolatedWidgetClass.getClassLoader());

        System.out.println("\n== Real identity comparison: same source, same bytecode, different loaders ==");
        System.out.println("Widget.class == isolatedWidgetClass: " + (Widget.class == isolatedWidgetClass)
                + "  <-- REAL: two genuinely distinct Class objects for the identical class name/bytecode");
        System.out.println("isolatedWidget instanceof Widget: " + (isolatedWidget instanceof Widget)
                + "  <-- REAL: false, despite being the 'same' class by name and source");

        System.out.println("\n== Real ClassCastException from treating one as the other ==");
        try {
            Widget casted = (Widget) isolatedWidget;
            System.out.println("Cast succeeded (unexpected): " + casted.label());
        } catch (ClassCastException e) {
            System.out.println("Real ClassCastException: " + e.getMessage());
        }

        // Both are still perfectly usable on their own -- via reflection, or
        // via the isolated loader's own class reference.
        java.lang.reflect.Method labelMethod = isolatedWidgetClass.getMethod("label");
        System.out.println("\nCalling label() via reflection on the isolated instance still works fine: "
                + labelMethod.invoke(isolatedWidget));
    }
}
