import java.util.Random;

/**
 * T-1206 -- SLI, SLO, and error budgets, computed from a real (simulated)
 * 30-day request log rather than described abstractly. SLI = the measured
 * indicator (here, success rate); SLO = the target (99.9%); the error
 * budget is what's left to spend before the SLO is breached.
 */
public class ErrorBudgetDemo {
    public static void main(String[] args) {
        double sloTarget = 0.999; // "three nines"
        long requestsPerDay = 2_000_000;
        int days = 30;
        long totalRequests = requestsPerDay * days;

        long allowedFailures = (long) (totalRequests * (1 - sloTarget));
        System.out.printf("SLO: %.3f%% success rate over %d days (%,d total requests)%n", sloTarget * 100, days, totalRequests);
        System.out.printf("Error budget: %,d allowed failures over the period (%.4f%% of traffic)%n%n",
                allowedFailures, (1 - sloTarget) * 100);

        // simulate 30 days of real daily failure counts: normal days have a low
        // background failure rate, but day 17 has a real incident (a 40-minute
        // partial outage) that burns a large chunk of budget in one day
        Random random = new Random(1206);
        long budgetRemaining = allowedFailures;
        long totalFailures = 0;
        for (int day = 1; day <= days; day++) {
            long dailyFailures;
            if (day == 17) {
                // simulated incident: 40 minutes at a 15% failure rate during peak traffic
                long incidentRequests = (long) (requestsPerDay / 24.0 / 60.0 * 40); // ~40 min of a day's average per-minute rate
                dailyFailures = (long) (incidentRequests * 0.15) + backgroundFailures(requestsPerDay, random);
            } else {
                dailyFailures = backgroundFailures(requestsPerDay, random);
            }
            totalFailures += dailyFailures;
            budgetRemaining -= dailyFailures;
            String flag = dailyFailures > allowedFailures / days * 3 ? "  <-- incident day" : "";
            System.out.printf("day %2d: %,8d failures, budget remaining after today: %,10d%s%n",
                    day, dailyFailures, budgetRemaining, flag);
        }

        System.out.println();
        double actualSuccessRate = 1.0 - (double) totalFailures / totalRequests;
        System.out.printf("Actual success rate over the 30 days: %.5f%%  (SLO target: %.3f%%)%n", actualSuccessRate * 100, sloTarget * 100);
        System.out.printf("Total failures: %,d of %,d allowed (%.1f%% of budget consumed)%n",
                totalFailures, allowedFailures, 100.0 * totalFailures / allowedFailures);
        System.out.println(budgetRemaining >= 0
                ? "RESULT: SLO met -- budget remaining, but see day 17's single-day burn rate."
                : "RESULT: SLO BREACHED -- error budget went negative.");
    }

    static long backgroundFailures(long requestsPerDay, Random random) {
        // background failure rate ~0.02% with some day-to-day noise
        double rate = 0.0002 * (0.7 + random.nextDouble() * 0.6);
        return Math.round(requestsPerDay * rate);
    }
}
