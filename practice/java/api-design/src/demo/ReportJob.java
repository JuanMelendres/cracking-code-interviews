package demo;

import java.time.Instant;

/** The async operation's own state -- what the status-polling endpoint reports on. */
public class ReportJob {
    public enum Status { PENDING, RUNNING, COMPLETED }

    public final long id;
    public volatile Status status = Status.PENDING;
    public final Instant submittedAt = Instant.now();
    public volatile Instant completedAt;
    public volatile String result;

    public ReportJob(long id) {
        this.id = id;
    }
}
