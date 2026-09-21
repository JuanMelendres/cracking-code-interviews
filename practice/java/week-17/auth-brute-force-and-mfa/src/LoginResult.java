public record LoginResult(Status status, long millisRemaining, int attemptsRemaining) {

    public enum Status { SUCCESS, FAILURE, JUST_LOCKED, LOCKED_OUT }

    public static LoginResult success() {
        return new LoginResult(Status.SUCCESS, 0, 0);
    }

    public static LoginResult failure(int attemptsRemaining) {
        return new LoginResult(Status.FAILURE, 0, attemptsRemaining);
    }

    public static LoginResult justLocked(long lockoutMillis) {
        return new LoginResult(Status.JUST_LOCKED, lockoutMillis, 0);
    }

    public static LoginResult lockedOut(long millisRemaining) {
        return new LoginResult(Status.LOCKED_OUT, millisRemaining, 0);
    }
}
