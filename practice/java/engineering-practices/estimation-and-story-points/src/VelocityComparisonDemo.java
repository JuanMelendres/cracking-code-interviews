/**
 * Real, deterministic proof that comparing velocity (points per sprint)
 * across two different teams is invalid, even when both teams deliver
 * EXACTLY the same real work. Two teams calibrate story points differently
 * against the same underlying hours -- a real, common situation, since each
 * team's point scale is only ever calibrated against its OWN past reference
 * stories, never a shared, cross-team unit.
 */
public class VelocityComparisonDemo {

    public static void main(String[] args) {
        Task[] tasks = {
                new Task("Fix typo in error message", 1),
                new Task("Add null check to input validator", 2),
                new Task("Add a new field to an existing DTO plus migration", 4),
                new Task("Implement a new REST endpoint with validation", 8),
                new Task("Integrate a third-party payment API", 16),
                new Task("Refactor a legacy module to remove a circular dependency", 24),
                new Task("Design and implement a new caching layer", 32),
                new Task("Migrate a service from REST to gRPC", 48),
        };

        System.out.println("=== Both teams deliver the IDENTICAL real work in the sprint (same tasks, same real hours) ===");
        double totalRealHours = 0;
        for (Task t : tasks) {
            totalRealHours += t.groundTruthHours();
        }
        System.out.printf("  Total real ground-truth effort delivered by BOTH teams: %.0f hours%n", totalRealHours);

        System.out.println();
        System.out.println("=== Team A's calibration: roughly 1 point per 2 hours (their own reference story) ===");
        int teamAVelocity = 0;
        for (Task t : tasks) {
            double raw = t.groundTruthHours() / 2.0;
            int points = FibonacciScale.nearest(raw);
            teamAVelocity += points;
            System.out.printf("  %-58s %5.1fh -> %3d points%n", t.name(), t.groundTruthHours(), points);
        }
        System.out.println("  Team A's velocity this sprint: " + teamAVelocity + " points");

        System.out.println();
        System.out.println("=== Team B's calibration: roughly 1 point per 1 hour (a different reference story) ===");
        int teamBVelocity = 0;
        for (Task t : tasks) {
            double raw = t.groundTruthHours() / 1.0;
            int points = FibonacciScale.nearest(raw);
            teamBVelocity += points;
            System.out.printf("  %-58s %5.1fh -> %3d points%n", t.name(), t.groundTruthHours(), points);
        }
        System.out.println("  Team B's velocity this sprint: " + teamBVelocity + " points");

        System.out.println();
        System.out.println("=== Result ===");
        System.out.printf("  Team A velocity: %d points. Team B velocity: %d points -- a real %.1fx difference,%n",
                teamAVelocity, teamBVelocity, (double) teamBVelocity / teamAVelocity);
        System.out.println("  for delivering the EXACT SAME " + (int) totalRealHours + " hours of real work.");
        System.out.println("  A manager comparing these two velocity numbers directly would wrongly conclude");
        System.out.println("  Team B is faster or more productive -- the difference is entirely calibration,");
        System.out.println("  not delivered output. Velocity is only ever a valid trend WITHIN one team's own");
        System.out.println("  history, never a cross-team comparison.");
    }
}
