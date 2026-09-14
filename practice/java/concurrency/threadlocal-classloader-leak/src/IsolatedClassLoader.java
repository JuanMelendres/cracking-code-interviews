import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// A real, minimal isolated classloader -- the same real mechanism a servlet
// container (Tomcat, Jetty) uses to give each deployed webapp its own
// classloader, independent of the container's own classes and of every
// other webapp's classes. It deliberately does NOT delegate to the parent
// for PluginTask specifically, so this loader becomes PluginTask's real,
// genuine defining classloader.
public class IsolatedClassLoader extends ClassLoader {
    private final Path classesDir;

    public IsolatedClassLoader(Path classesDir, ClassLoader parent) {
        super(parent);
        this.classesDir = classesDir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            Path classFile = classesDir.resolve(name.replace('.', '/') + ".class");
            byte[] bytes = Files.readAllBytes(classFile);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.equals("PluginTask")) {
            synchronized (getClassLoadingLock(name)) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded == null) {
                    loaded = findClass(name);
                }
                if (resolve) {
                    resolveClass(loaded);
                }
                return loaded;
            }
        }
        return super.loadClass(name, resolve);
    }
}
