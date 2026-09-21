/** count++ is read-modify-write, not atomic -- two threads can both read
 * the same value before either writes back, silently losing an update. */
public class UnsafeCounter {
    private int count = 0;
    public void increment() { count++; }
    public int get() { return count; }
}
