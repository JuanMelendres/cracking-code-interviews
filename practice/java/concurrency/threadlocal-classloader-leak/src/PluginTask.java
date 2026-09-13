// Stands in for a class belonging to a "webapp" -- loaded by its own,
// isolated classloader (see IsolatedClassLoader), not the system classloader.
public class PluginTask implements Runnable {
    @Override
    public void run() {
        // The real leak-causing line: a webapp-loaded object registers
        // itself with a ThreadLocal owned by a long-lived framework thread,
        // and nothing ever calls remove().
        LeakyThreadLocalHolder.HOLDER.set(this);
    }
}
