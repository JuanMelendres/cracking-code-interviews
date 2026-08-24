public class LambdaExample {
    public static void main(String[] args) {
        Runnable r = () -> System.out.println("lambda running");
        r.run();
    }
}
