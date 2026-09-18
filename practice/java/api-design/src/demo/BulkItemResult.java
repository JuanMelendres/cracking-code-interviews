package demo;

/** Per-item outcome in a bulk operation's response -- the real reason
 * bulk endpoints can't just return one status code: item 2 of 5 can
 * fail while 1, 3, 4, and 5 succeed, and the caller needs to know
 * exactly which one and why. */
public class BulkItemResult {
    public final int index;
    public final boolean success;
    public final Long id;
    public final String error;

    private BulkItemResult(int index, boolean success, Long id, String error) {
        this.index = index;
        this.success = success;
        this.id = id;
        this.error = error;
    }

    public static BulkItemResult ok(int index, long id) {
        return new BulkItemResult(index, true, id, null);
    }

    public static BulkItemResult failed(int index, String error) {
        return new BulkItemResult(index, false, null, error);
    }
}
