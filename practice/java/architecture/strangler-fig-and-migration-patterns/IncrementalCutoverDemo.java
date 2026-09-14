/**
 * Real proof that Strangler Fig extraction is gradual, not a single cutover event.
 * 1,000 distinct request keys are routed through the same {@link StranglerFacade} at
 * three real percentage settings (0%, 25%, 100%), and the real observed traffic split
 * is measured and printed at each step -- proving the facade's routing rule, not a
 * description of it, actually controls how much real traffic reaches each system.
 */
public final class IncrementalCutoverDemo {

    public static void main(String[] args) {
        StranglerFacade facade = new StranglerFacade(
                key -> "legacy-handled:" + key,
                key -> "new-handled:" + key);

        int totalRequests = 1000;

        for (int percentage : new int[]{0, 25, 100}) {
            facade.setNewSystemPercentage(percentage);
            int newCount = 0;
            int legacyCount = 0;
            for (int i = 0; i < totalRequests; i++) {
                String key = "request-" + i;
                String result = facade.handle(key);
                if (result.startsWith("new-handled")) {
                    newCount++;
                } else {
                    legacyCount++;
                }
            }
            System.out.printf("Configured new-system percentage: %3d%%  ->  real observed split: new=%d (%.1f%%) legacy=%d (%.1f%%)%n",
                    percentage, newCount, 100.0 * newCount / totalRequests, legacyCount, 100.0 * legacyCount / totalRequests);
        }
    }
}
