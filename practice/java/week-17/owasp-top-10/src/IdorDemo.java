import java.util.HashMap;
import java.util.Map;

// Real demo of A01:2021 Broken Access Control -> Insecure Direct Object Reference (IDOR).
// Two handlers share the same in-memory "database"; only the authorization check differs.
public class IdorDemo {

    record Invoice(long id, String ownerUserId, double amountUsd) {}

    static final Map<Long, Invoice> DB = new HashMap<>();
    static {
        DB.put(101L, new Invoice(101L, "alice", 4200.00));
        DB.put(102L, new Invoice(102L, "bob", 980.50));
    }

    // VULNERABLE: fetches by ID with no ownership check.
    static Invoice getInvoiceVulnerable(String requesterId, long invoiceId) {
        return DB.get(invoiceId);
    }

    // FIXED: enforces object-level authorization before returning the record.
    static Invoice getInvoiceFixed(String requesterId, long invoiceId) {
        Invoice inv = DB.get(invoiceId);
        if (inv == null) return null;
        if (!inv.ownerUserId().equals(requesterId)) {
            throw new SecurityException(
                "requester '" + requesterId + "' is not the owner of invoice " + invoiceId);
        }
        return inv;
    }

    public static void main(String[] args) {
        System.out.println("=== VULNERABLE handler: bob requests alice's invoice 101 ===");
        Invoice leaked = getInvoiceVulnerable("bob", 101L);
        System.out.println("Result: " + leaked + "  <-- bob just read alice's $4,200 invoice");

        System.out.println();
        System.out.println("=== FIXED handler: bob requests alice's invoice 101 ===");
        try {
            getInvoiceFixed("bob", 101L);
            System.out.println("Result: leaked (BUG)");
        } catch (SecurityException e) {
            System.out.println("Blocked: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== FIXED handler: alice requests her own invoice 101 ===");
        Invoice ok = getInvoiceFixed("alice", 101L);
        System.out.println("Result: " + ok + "  <-- legitimate owner, allowed");
    }
}
