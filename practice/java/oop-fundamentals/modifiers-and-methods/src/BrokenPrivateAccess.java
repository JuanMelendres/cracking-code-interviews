public class BrokenPrivateAccess {
    public static void main(String[] args) {
        BankAccount acc = new BankAccount();
        System.out.println(acc.balance); // ERROR: balance has private access in BankAccount (different top-level class)
    }
}
