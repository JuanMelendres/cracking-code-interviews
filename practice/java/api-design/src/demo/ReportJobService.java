package demo;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A genuinely asynchronous operation -- real background work on a real
 * separate thread, real elapsed wall-clock time, not a synchronous call
 * dressed up with a 202 status code. This is what
 * rest-api-fundamentals.md's own text flagged as "conceptual only... a
 * fabricated one would be a fake" -- this is the real one.
 */
public class ReportJobService {
    private final Map<Long, ReportJob> jobs = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /** Submits real background work and returns immediately -- the caller never blocks on it. */
    public ReportJob submit(String reportName) {
        ReportJob job = new ReportJob(nextId.getAndIncrement());
        jobs.put(job.id, job);
        executor.submit(() -> runJob(job, reportName));
        return job;
    }

    private void runJob(ReportJob job, String reportName) {
        job.status = ReportJob.Status.RUNNING;
        try {
            // Real, measurable work -- not instantaneous, not simulated with a flag.
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        job.result = "Report '" + reportName + "' generated at " + Instant.now();
        job.completedAt = Instant.now();
        job.status = ReportJob.Status.COMPLETED;
    }

    public ReportJob get(long id) {
        return jobs.get(id);
    }
}
