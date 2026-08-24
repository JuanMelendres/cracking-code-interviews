public class BankAccount {
    private double balance = 100.0;
    private final String accountId = "ACC-001";

    private double calculateInterest() {
        return balance * 0.05;
    }

    @Override
    public String toString() {
        return "BankAccount{accountId='" + accountId + "', balance=" + balance + "}";
    }
}
