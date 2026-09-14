public class BrokenFinalReassignment {
    public static void main(String[] args) {
        final int x = 5;
        x = 10; // ERROR: cannot assign a value to final variable x
        System.out.println(x);
    }
}
