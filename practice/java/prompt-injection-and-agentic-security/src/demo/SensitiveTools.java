package demo;

/**
 * A real, dangerous tool an agent could have access to, and a real,
 * deterministic gate around it -- privilege separation, not a hope that
 * the model "just won't" call it.
 */
public class SensitiveTools {

    private boolean databaseWiped = false;
    private int wipeAttempts = 0;

    /**
     * The actually-dangerous action. Only proceeds if humanApproved is
     * genuinely true -- there is no path to real effect without it.
     */
    public String wipeDatabase(boolean humanApproved) {
        wipeAttempts++;
        if (!humanApproved) {
            return "BLOCKED: wipe_database requires human approval, none given.";
        }
        databaseWiped = true;
        return "Database wiped.";
    }

    public boolean wasDatabaseWiped() {
        return databaseWiped;
    }

    public int wipeAttempts() {
        return wipeAttempts;
    }
}
