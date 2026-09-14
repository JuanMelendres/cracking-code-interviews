import java.util.Random;

public class BurnRateAlertingDemo {
    static final double SLO = 0.999; // 99.9%
    static final double ALLOWED_ERROR_RATE = 1 - SLO; // 0.001
    static final int MINUTES = 7 * 24 * 60; // 7 days, 1-minute resolution

    public static void main(String[] args) {
        double[] errorRate = generateSyntheticSeries();

        int naiveAlerts = countRisingEdges(naiveThresholdFiring(errorRate, 0.01)); // fires above 1% in a single minute
        int burnRateAlerts = countRisingEdges(burnRateFiring(errorRate));

        System.out.println("Synthetic 7-day error-rate series generated (1-minute resolution, " + MINUTES + " points).");
        System.out.println("Contains 2 real, sustained incidents plus realistic single-minute noise spikes.");
        System.out.println();
        System.out.println("Naive static-threshold rule (fires any single minute > 1% error rate):");
        System.out.println("  distinct alert-firing events (rising edges) = " + naiveAlerts);
        System.out.println();
        System.out.println("Multi-window burn-rate rule (page severity, per Google's SRE Workbook: burn rate >= 14.4");
        System.out.println("sustained across BOTH a 5-minute AND a 1-hour window simultaneously):");
        System.out.println("  distinct alert-firing events (rising edges) = " + burnRateAlerts);
        System.out.println();
        System.out.printf("Noise reduction: naive rule fired %.1fx more often than the burn-rate rule,%n",
                (double) naiveAlerts / burnRateAlerts);
        System.out.println("while both rules detected both real injected incidents (verified below).");

        verifyIncidentsCaught(errorRate);
    }

    static double[] generateSyntheticSeries() {
        double[] series = new double[MINUTES];
        Random r = new Random(42);
        // background noise: fluctuates well under budget, with occasional
        // single-minute transient blips that are NOT real incidents
        for (int i = 0; i < MINUTES; i++) {
            series[i] = Math.max(0, 0.0003 + r.nextGaussian() * 0.0002);
            if (r.nextDouble() < 0.002) { // ~1 blip per ~500 minutes
                series[i] = 0.01 + r.nextDouble() * 0.04; // 1-5% single-minute blip
            }
        }
        // Real incident A: minutes [3000, 3020) sustained ~5% error rate
        for (int i = 3000; i < 3020; i++) series[i] = 0.05;
        // Real incident B: minutes [7000, 7045) sustained ~3% error rate
        for (int i = 7000; i < 7045; i++) series[i] = 0.03;
        return series;
    }

    static boolean[] naiveThresholdFiring(double[] series, double threshold) {
        boolean[] firing = new boolean[series.length];
        for (int i = 0; i < series.length; i++) firing[i] = series[i] > threshold;
        return firing;
    }

    static boolean[] burnRateFiring(double[] series) {
        boolean[] firing = new boolean[series.length];
        int shortWindow = 5, longWindow = 60;
        double burnRateThreshold = 14.4;
        for (int i = longWindow; i < series.length; i++) {
            double shortAvg = average(series, i - shortWindow, i);
            double longAvg = average(series, i - longWindow, i);
            double shortBurn = shortAvg / ALLOWED_ERROR_RATE;
            double longBurn = longAvg / ALLOWED_ERROR_RATE;
            firing[i] = shortBurn >= burnRateThreshold && longBurn >= burnRateThreshold;
        }
        return firing;
    }

    static double average(double[] series, int fromInclusive, int toExclusive) {
        double sum = 0;
        for (int i = fromInclusive; i < toExclusive; i++) sum += series[i];
        return sum / (toExclusive - fromInclusive);
    }

    static int countRisingEdges(boolean[] firing) {
        int count = 0;
        for (int i = 1; i < firing.length; i++) {
            if (firing[i] && !firing[i - 1]) count++;
        }
        if (firing[0]) count++;
        return count;
    }

    static void verifyIncidentsCaught(double[] series) {
        boolean[] naive = naiveThresholdFiring(series, 0.01);
        boolean[] burn = burnRateFiring(series);
        boolean naiveCaughtA = anyTrueInRange(naive, 3000, 3020);
        boolean naiveCaughtB = anyTrueInRange(naive, 7000, 7045);
        boolean burnCaughtA = anyTrueInRange(burn, 3000, 3060); // burn-rate needs the window to fill first
        boolean burnCaughtB = anyTrueInRange(burn, 7000, 7105);
        System.out.println();
        System.out.println("Incident A (minutes 3000-3020, ~5% sustained) caught by naive rule: " + naiveCaughtA
                + ", caught by burn-rate rule: " + burnCaughtA);
        System.out.println("Incident B (minutes 7000-7045, ~3% sustained) caught by naive rule: " + naiveCaughtB
                + ", caught by burn-rate rule: " + burnCaughtB);
    }

    static boolean anyTrueInRange(boolean[] arr, int from, int to) {
        for (int i = from; i < Math.min(to, arr.length); i++) if (arr[i]) return true;
        return false;
    }
}
