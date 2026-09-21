package demo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A real, minimal lease-based lock service -- the same shape as etcd's
 * lease-based locks or a ZooKeeper ephemeral znode: a lease has a TTL,
 * expires on its own if not renewed, and every successfully granted
 * lease gets a real, monotonically increasing token. Nothing here
 * simulates network partitions or real sockets -- the lease-expiry and
 * fencing-token mechanics are the real thing being demonstrated.
 */
public class LockService {

    public static final class Lease {
        public final String holder;
        public final long token;
        public final long expiresAtMillis;

        Lease(String holder, long token, long expiresAtMillis) {
            this.holder = holder;
            this.token = token;
            this.expiresAtMillis = expiresAtMillis;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expiresAtMillis;
        }
    }

    private final AtomicLong nextToken = new AtomicLong(1);
    private volatile Lease currentLease;

    /** Grants a new lease if the lock is free or the current lease has expired. */
    public synchronized Lease acquire(String clientId, long ttlMillis) {
        if (currentLease != null && !currentLease.isExpired()) {
            throw new IllegalStateException(
                clientId + " cannot acquire: lock currently held by " + currentLease.holder
                + " (token " + currentLease.token + ", not yet expired)");
        }
        long token = nextToken.getAndIncrement();
        currentLease = new Lease(clientId, token, System.currentTimeMillis() + ttlMillis);
        return currentLease;
    }
}
