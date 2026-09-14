/** A real, minimal event hierarchy for an event-sourced Account aggregate. */
sealed interface Event permits AccountOpened, MoneyDeposited, MoneyWithdrawn {
    String toLine();

    static Event fromLine(String line) {
        String[] parts = line.split(",", 2);
        return switch (parts[0]) {
            case "OPENED" -> new AccountOpened(parts[1]);
            case "DEPOSITED" -> new MoneyDeposited(Integer.parseInt(parts[1]));
            case "WITHDRAWN" -> new MoneyWithdrawn(Integer.parseInt(parts[1]));
            default -> throw new IllegalArgumentException("Unknown event type: " + parts[0]);
        };
    }
}

record AccountOpened(String owner) implements Event {
    public String toLine() { return "OPENED," + owner; }
}

record MoneyDeposited(int amount) implements Event {
    public String toLine() { return "DEPOSITED," + amount; }
}

record MoneyWithdrawn(int amount) implements Event {
    public String toLine() { return "WITHDRAWN," + amount; }
}
