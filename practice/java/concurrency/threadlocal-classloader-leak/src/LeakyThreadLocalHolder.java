// Stands in for a long-lived framework/container class -- loaded once by the
// system classloader and never reloaded, exactly like a real application
// server's own internal classes across many webapp redeploys.
public class LeakyThreadLocalHolder {
    public static final ThreadLocal<Object> HOLDER = new ThreadLocal<>();
}
